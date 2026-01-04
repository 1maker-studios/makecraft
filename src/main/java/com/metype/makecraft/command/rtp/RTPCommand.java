package com.metype.makecraft.command.rtp;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.utils.CommandUtils;
import com.metype.makecraft.utils.Utils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.*;


import static net.minecraft.server.command.CommandManager.literal;

public class RTPCommand implements ICommand {

    private final static Map<UUID, Long> lastExecuted = new HashMap<>();

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("rtp")
                .requires(Permissions.require("makecraft.rtp", 2))
                .requires(ServerCommandSource::isExecutedByPlayer)
                .executes(this::execute));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        long current = System.currentTimeMillis();
        long previous = lastExecuted.getOrDefault(Objects.requireNonNull(context.getSource().getPlayer()).getUuid(), 0L);
        long timeSince = current - previous;
        long waitTime = MakeCraft.MAIN_CONFIG.rtpCooldownSeconds * 1000;
        if(timeSince < waitTime && !Permissions.check(context.getSource(), "makecraft.rtp.no_delay", 2)) {
            context.getSource().sendFeedback(() -> Text.of(CommandUtils.format("You must wait {} seconds before using /rtp again.", (int)((waitTime - timeSince) / 1000))), true);
            return CommandUtils.COMMAND_FAILURE;
        }

        context.getSource().sendFeedback(() -> Text.of("Finding location..."), true);
        World world = context.getSource().getWorld();
        WorldBorder border = world.getWorldBorder();
        ServerPlayerEntity player = context.getSource().getPlayer();

        long startedAt = System.currentTimeMillis();

        Random rng = new Random();
        new Thread(() -> {
            Vec3d pos = null;
            while(pos == null) {
                long now = System.currentTimeMillis();
                if(now - startedAt > 10000) {
                    context.getSource().sendFeedback(() -> Text.of("Failed to find a location, try again!"), true);
                    return;
                }
                pos = GetValidRTPLocation(rng, world, border);
            }

            final Vec3d finalPos = pos;

            Utils.registerTickAction(server -> {
                ChunkPos chunk = new ChunkPos(new BlockPos((int) finalPos.x, (int) finalPos.y, (int) finalPos.z));
                Objects.requireNonNull(server.getWorld(world.getRegistryKey())).getChunkManager().addTicket(ChunkTicketType.PLAYER_LOADING, chunk, 4);
                TeleportPlayerTo(player, (int)finalPos.x, finalPos.y + 1, (int)finalPos.z);
                return false;
            });

            lastExecuted.put(player.getUuid(), System.currentTimeMillis());
        }).start();

        return CommandUtils.COMMAND_SUCCESS;
    }

    public static Vec3d GetValidRTPLocation(Random rng, World world, WorldBorder border) {
        double centerX = border.getCenterX();
        double centerZ = border.getCenterZ();
        double maxX = centerX + border.getBoundEast();
        double minX = centerX + border.getBoundWest();
        double maxZ = centerZ + border.getBoundNorth();
        double minZ = centerZ + border.getBoundSouth();
        int xPos = (int) (rng.nextDouble() * (maxX - minX) + minX);
        int zPos = (int) (rng.nextDouble() * (maxZ - minZ) + minZ);
        RegistryEntry<DimensionType> dimID = world.getDimensionEntry();
        double yPos = dimID.matchesKey(DimensionTypes.THE_NETHER) ? 120 : 255;
        boolean allowNoSky = dimID.matchesKey(DimensionTypes.THE_NETHER);
        int yMin = allowNoSky ? 12 : dimID.matchesKey(DimensionTypes.THE_END) ? 40 : 63;

        BlockPos.Mutable mutable = new BlockPos.Mutable(xPos, yPos, zPos);

        boolean isAirBelow;

        for(boolean isAirOn = world.getBlockState(mutable).isAir();
            mutable.getY() > yMin;
            isAirOn = isAirBelow
        ) {
            mutable.move(Direction.DOWN);
            BlockState state = world.getBlockState(mutable);

            if(!world.getFluidState(mutable).isEmpty() || (!isAirOn && !allowNoSky)) break;

            isAirBelow = state.isAir();

            if (!isAirBelow && isAirOn && state.allowsSpawning(world, mutable, EntityType.PLAYER)) {
                return new Vec3d(xPos, mutable.getY() + 1, zPos);
            }
        }

        return null;
    }

    public static void TeleportPlayerTo(ServerPlayerEntity player, double x, double y, double z) {
        player.fallDistance = 0;
        player.teleport(player.getEntityWorld(), x + 0.5f, y, z + 0.5f, Set.of(), 0, 0, true);
    }
}
