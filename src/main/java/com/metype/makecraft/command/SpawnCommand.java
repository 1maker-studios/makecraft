package com.metype.makecraft.command;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.types.Location;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Set;

public class SpawnCommand implements ICommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] aliases = new String[]{"spawn"};

        for(String alias : aliases) {
            LiteralArgumentBuilder<ServerCommandSource> args = CommandManager.literal(alias).requires(cs -> Permissions.check(cs, "makecraft.spawn", 0) )
                    .executes(this::execute);
            dispatcher.register(args);
        }
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(!source.isExecutedByPlayer()) {
            source.sendFeedback(() -> Text.of("You cannot teleport the console to Spawn."), false);
        }
        ServerPlayerEntity player = source.getPlayer();
        assert player != null;
        return TeleportPlayerToSpawn(player);
    }

    public static int TeleportPlayerToSpawn(ServerPlayerEntity player) {
        Location tpLocation = MakeCraft.MAIN_CONFIG.spawnLocation;
        if(!(tpLocation.getWorld() instanceof ServerWorld serverWorld)) return CommandUtils.COMMAND_FAILURE;
        player.sendMessage(Text.of("Teleporting to Spawn..."), false);
        player.fallDistance = 0;
        player.teleport(serverWorld, tpLocation.getX() + 0.5f, tpLocation.getY(), tpLocation.getZ() + 0.5f, Set.of(), tpLocation.getYaw(), tpLocation.getPitch(), true);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
