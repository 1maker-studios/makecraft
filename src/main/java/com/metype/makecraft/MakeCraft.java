package com.metype.makecraft;

import com.metype.makecraft.command.*;
import com.metype.makecraft.command.farmworld.FarmworldBaseCommand;
import com.metype.makecraft.command.gamemode.GamemodeBaseCommand;
import com.metype.makecraft.command.rank.RankBaseCommand;
import com.metype.makecraft.command.spawn.SpawnSetCommand;
import com.metype.makecraft.command.spawn.SpawnBaseCommand;
import com.metype.makecraft.config.MainConfig;
import com.metype.makecraft.events.ServerCloseEventListener;
import com.metype.makecraft.rank.RankUtils;
import com.metype.makecraft.utils.DBUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class MakeCraft implements ModInitializer {

    public static final String MOD_ID = "MakeCraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MakeCraft.class);
    public static MinecraftServer SERVER = null;
    public static MainConfig MAIN_CONFIG = null;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            SERVER = minecraftServer;
            MAIN_CONFIG = MainConfig.load();
            MAIN_CONFIG.save();
            ServerLifecycleEvents.AFTER_SAVE.register(ServerCloseEventListener::onServerSave);
            ServerLifecycleEvents.SERVER_STOPPING.register(ServerCloseEventListener::onServerClose);
            ServerTickEvents.END_SERVER_TICK.register(ServerCloseEventListener::onServerTick);
            ServerCloseEventListener.runRestartChecks();
            ServerPlayerEvents.JOIN.register(serverPlayerEntity -> {
                if(serverPlayerEntity.getEntityWorld().getRegistryKey().getValue().getNamespace().equalsIgnoreCase("farmworld")){
                    SpawnBaseCommand.TeleportPlayerToSpawn(serverPlayerEntity);
                    serverPlayerEntity.sendMessage(Text.of("You were in the farmworlds when you logged out and have been returned to spawn."));
                }
                if(RankUtils.byID(MAIN_CONFIG.joinRank).isPresent()) {
                    SERVER.getCommandManager().parseAndExecute(SERVER.getCommandSource(), "/rank give " + MAIN_CONFIG.joinRank + " " + serverPlayerEntity.getName().getString());
                }
            });
            try {
                DBUtils.init();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            RankUtils.init();
        });

        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(new BaseCommand().register());
        dispatcher.register(new SpawnSetCommand().register());
        dispatcher.register(new SpawnBaseCommand().register());
        dispatcher.register(new RankBaseCommand().register());
        dispatcher.register(new FarmworldBaseCommand().register());
        dispatcher.register(new GamemodeBaseCommand().register());
    }
}
