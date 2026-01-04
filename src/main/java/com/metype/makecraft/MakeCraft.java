package com.metype.makecraft;

import com.metype.makecraft.command.*;
import com.metype.makecraft.command.farmworld.FarmworldBaseCommand;
import com.metype.makecraft.command.gamemode.GamemodeBaseCommand;
import com.metype.makecraft.command.near.NearCommand;
import com.metype.makecraft.command.rank.RankBaseCommand;
import com.metype.makecraft.command.reply.ReplyCommand;
import com.metype.makecraft.command.rtp.RTPCommand;
import com.metype.makecraft.command.spawn.SpawnSetCommand;
import com.metype.makecraft.command.spawn.SpawnBaseCommand;
import com.metype.makecraft.command.trash.DisposalCommand;
import com.metype.makecraft.config.MainConfig;
import com.metype.makecraft.events.ServerCloseEventListener;
import com.metype.makecraft.rank.RankUtils;
import com.metype.makecraft.utils.CommandUtils;
import com.metype.makecraft.utils.DBUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
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
import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class MakeCraft implements ModInitializer {

    public static final String MOD_ID = "MakeCraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MakeCraft.class);
    public static MinecraftServer SERVER = null;
    public static MainConfig MAIN_CONFIG = null;

    public static final List<ICommand> commands = List.of(
            new BaseCommand(),
            new SpawnBaseCommand(),
            new RankBaseCommand(),
            new FarmworldBaseCommand(),
            new GamemodeBaseCommand(),
            new RTPCommand(),
            new DisposalCommand(),
            new NearCommand(),
            new ReplyCommand()
    );

    @Override
    public void onInitialize() {
        MainConfig.CONFIG_RELOADED.register(() -> {
            LOGGER.info("Config loaded.");
            for(Map.Entry<String, String> alias: MAIN_CONFIG.commandAliases.entrySet()) {
                CommandDispatcher<ServerCommandSource> dispatcher = SERVER.getCommandManager().getDispatcher();
                LiteralCommandNode<ServerCommandSource> node = (LiteralCommandNode<ServerCommandSource>) dispatcher.getRoot().getChild(alias.getValue());

                if(node == null) {
                    LOGGER.warn(CommandUtils.format("Alias {alias} defined for {cmd}, but no command was found", Map.of("alias", alias.getKey(), "cmd", alias.getValue())));
                    continue;
                }

                LiteralArgumentBuilder<ServerCommandSource> aliasCmd = literal(alias.getKey());

                CommandNode<ServerCommandSource> result;
                if(!node.getChildren().isEmpty()) {
                    result = dispatcher.register(aliasCmd.redirect(node));
                } else {
                    result = dispatcher.register(aliasCmd.executes(node.getCommand()));
                }

                LOGGER.info("{} -> {}", result.getName(), node.getName());
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            SERVER = minecraftServer;

            MainConfig.load();
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
        for(ICommand command : commands) {
            command.register(dispatcher);
        }
        LOGGER.info("Registered commands.");
    }
}
