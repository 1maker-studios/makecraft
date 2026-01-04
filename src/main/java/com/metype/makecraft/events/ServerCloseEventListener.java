package com.metype.makecraft.events;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.command.spawn.SpawnBaseCommand;
import com.metype.makecraft.config.ConfigUtils;
import com.metype.makecraft.utils.Utils;
import me.isaiah.multiworld.ICreator;
import me.isaiah.multiworld.MultiworldMod;
import me.isaiah.multiworld.command.CreateCommand;
import me.isaiah.multiworld.command.SetspawnCommand;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.joml.Random;

import static me.isaiah.multiworld.command.CreateCommand.make_config;

public class ServerCloseEventListener {
    public static void onServerClose(MinecraftServer ignored) {
        MakeCraft.MAIN_CONFIG.save();
    }

    private static boolean GIVEN_FIVE_MINUTE_WARNING = false;


    public static void runRestartChecks() {
        long restartTime = ConfigUtils.getRestartTime(MakeCraft.MAIN_CONFIG.restartTime).toInstant(ZoneOffset.systemDefault().getRules().getOffset(Instant.now())).toEpochMilli();
        if (restartTime - Instant.now().toEpochMilli() < 10 * 60 * 1000 && restartTime - Instant.now().toEpochMilli() > 0) { // Within Ten Minutes
            ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
                long timeLeft = restartTime - Instant.now().toEpochMilli();
                if (timeLeft < 5 * 60 * 1000 && !GIVEN_FIVE_MINUTE_WARNING) {
                    for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                        player.sendMessage(Component.text("Server will restart in five minutes!"));
                    }
                    GIVEN_FIVE_MINUTE_WARNING = true;
                }
                if (timeLeft < 60 * 1000) {
                    for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                        player.sendActionBar(Component.text("Server will restart in " + String.format("%02d", Math.floorDiv(timeLeft, 60 * 1000)) + ":" + String.format("%02d", Math.floorDiv(timeLeft, 1000) % 60)));
                    }
                }
                if (timeLeft <= 0) {
                    for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                        player.networkHandler.disconnect(Text.of("Server is restarting."));
                    }

                    refreshFarmWorlds(minecraftServer, server -> {
                        server.stop(false);
                    });
                }
            });
        }
    }
    public static void onServerTick(MinecraftServer server) {
        Utils.tick(server);
        if(server.getTicks() % 6000 != 0) return;
        runRestartChecks();
    }

    public static void onServerSave(MinecraftServer ignored, boolean ignored1, boolean ignored2) {
        MakeCraft.MAIN_CONFIG.save();
    }

    @FunctionalInterface
    public interface AfterRefreshAction {
        void execute(MinecraftServer server);
    }

    public static void refreshFarmWorlds(MinecraftServer minecraftServer) {
        refreshFarmWorlds(minecraftServer, null);
    }

    public static void refreshFarmWorlds(MinecraftServer minecraftServer, AfterRefreshAction action) {
        ICreator creator = MultiworldMod.get_world_creator();

        deleteWorld(creator, "farmworld:overworld");
        deleteWorld(creator, "farmworld:nether");
        deleteWorld(creator, "farmworld:end");

        AtomicInteger i = new AtomicInteger();

        Utils.registerTickAction((server) -> {
            if(i.incrementAndGet() < 40) return true;
            ICreator localCreator = MultiworldMod.get_world_creator();
            Random random = new Random();
            long seed = random.nextInt(Integer.MAX_VALUE);
            for(ServerPlayerEntity player: server.getPlayerManager().getPlayerList()) {
                if(player.getEntityWorld().getRegistryKey().getValue().getNamespace().equalsIgnoreCase("farmworld")){
                    SpawnBaseCommand.TeleportPlayerToSpawn(player);
                }
            }
            if(MakeCraft.MAIN_CONFIG.useFarmWorlds) {
                if(MakeCraft.MAIN_CONFIG.useFarmWorldOverworld) {
                    ServerWorld world = recreateWorld(localCreator, server, seed, VanillaChunkGenerator.OVERWORLD, "farmworld:overworld", DimensionTypes.OVERWORLD.getValue());
                    createPlatform(world, true);
                }
                if(MakeCraft.MAIN_CONFIG.useFarmWorldNether) {
                    ServerWorld world = recreateWorld(localCreator, server, seed, VanillaChunkGenerator.NETHER, "farmworld:nether", DimensionTypes.THE_NETHER.getValue());
                    createPlatform(world, false);
                }
                if(MakeCraft.MAIN_CONFIG.useFarmWorldEnd) {
                    ServerWorld world = recreateWorld(localCreator, server, seed, VanillaChunkGenerator.END, "farmworld:end", DimensionTypes.THE_END.getValue());
                    createPlatform(world, true, true);
                }
            }
            if(action != null) {
                action.execute(minecraftServer);
            }
            return false;
        });
    }

    public static class VanillaChunkGenerator {
        public static final String OVERWORLD = "NORMAL";
        public static final String NETHER = "NETHER";
        public static final String END = "END";
    }

    public static void deleteWorld(ICreator creator, String name) {
        try { creator.delete_world(name); } catch (Exception ignored) { }
    }

    public static ServerWorld recreateWorld(ICreator creator, MinecraftServer minecraftServer, long seed, String type, String name, Identifier worldType) {
        ChunkGenerator gen = CreateCommand.get_chunk_gen(minecraftServer, type);
        ServerWorld world = creator.create_world(name, worldType, gen, Difficulty.HARD, seed);
        make_config(world, name, seed, type);
        return world;
    }

    public static void createPlatform(ServerWorld world, boolean startTop) {
        createPlatform(world, startTop, false);
    }

    public static void createPlatform(ServerWorld world, boolean startTop, boolean gateway) {
        BlockPos spawnPos = gateway ? ServerWorld.END_SPAWN_POS : me.isaiah.multiworld.command.SpawnCommand.getSpawn(world);
        if(startTop) {
            spawnPos = new BlockPos(spawnPos.getX(), 128, spawnPos.getZ());
        } else {
            spawnPos = new BlockPos(spawnPos.getX(), 0, spawnPos.getZ());
        }
        while(world.getBlockState(spawnPos).getBlock() != Blocks.AIR) {
            spawnPos = spawnPos.add(0, 2, 0);
        }
        while(world.getBlockState(spawnPos).getBlock() == Blocks.AIR) {
            spawnPos = spawnPos.add(0, -1, 0);
        }
        spawnPos = spawnPos.add(0, 2, 0);
        if(spawnPos.getY() <= 2) {
            spawnPos = new BlockPos(spawnPos.getX(), 2, spawnPos.getZ());
        }
        int i = spawnPos.getX();
        int j = spawnPos.getY() - 2;
        int k = spawnPos.getZ();
        try {
            SetspawnCommand.setSpawn(world, spawnPos);
        } catch (IOException e) {
            MakeCraft.LOGGER.error("Failed to set world spawn for farmworld.");
        }
        //world.setSpawnPoint(WorldProperties.SpawnPoint.create(ServerWorld.NETHER, spawnPos, 0, 0));
        BlockPos.iterate(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach((pos) -> world.setBlockState(pos, Blocks.AIR.getDefaultState()));
        BlockPos.iterate(i - 2, j, k - 2, i + 2, j, k + 2).forEach((pos) -> world.setBlockState(pos, Blocks.BEDROCK.getDefaultState()));
        world.setBlockState(new BlockPos(i, j, k), Blocks.LODESTONE.getDefaultState());
        world.setBlockState(new BlockPos(i, j - 1, k), Blocks.BEDROCK.getDefaultState());
        if(gateway) {
            world.setBlockState(new BlockPos(i + 3, j + 1, k), Blocks.END_GATEWAY.getDefaultState());
        }
    }
}
