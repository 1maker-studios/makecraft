package com.metype.makecraft.command;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.types.Location;
import com.metype.makecraft.utils.CommandUtils;
import com.metype.makecraft.utils.Utils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.isaiah.multiworld.command.SpawnCommand;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Set;

public class FarmworldTeleportCommand implements ICommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("farm-overworld").requires(cs -> Permissions.check(cs, "makecraft.farmworld-teleport", 0))
        .executes((context -> {
            if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            MakeCraft.SERVER.getCommandManager().parseAndExecute(player.getCommandSource(), "mw tp farmworld:overworld");
            Utils.registerTickAction((server) -> {
                player.teleport(player.lastX + 0.5f, player.lastY, player.lastZ + 0.5, true);
                return false;
            });
            return CommandUtils.COMMAND_SUCCESS;
        })));

        dispatcher.register(CommandManager.literal("farm-nether").requires(cs -> Permissions.check(cs, "makecraft.farmworld-teleport", 0))
        .executes((context -> {
            if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            MakeCraft.SERVER.getCommandManager().parseAndExecute(player.getCommandSource(), "mw tp farmworld:nether");
            Utils.registerTickAction((server) -> {
                player.teleport(player.lastX + 0.5f, player.lastY, player.lastZ + 0.5, true);
                return false;
            });
            return CommandUtils.COMMAND_SUCCESS;
        })));

        dispatcher.register(CommandManager.literal("farm-end").requires(cs -> Permissions.check(cs, "makecraft.farmworld-teleport", 0))
        .executes((context -> {
            if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
            MakeCraft.SERVER.getCommandManager().parseAndExecute(player.getCommandSource(), "mw tp farmworld:end");
            Utils.registerTickAction((server) -> {
                player.teleport(player.lastX + 0.5f, player.lastY, player.lastZ + 0.5, true);
                return false;
            });
            return CommandUtils.COMMAND_SUCCESS;
        })));
    }

    public static HashMap<String, ServerWorld> getWorlds() {
        HashMap<String, ServerWorld> worlds = new HashMap<>();
        MakeCraft.SERVER.getWorldRegistryKeys().forEach((r) -> {
            ServerWorld world = MakeCraft.SERVER.getWorld(r);
            worlds.put(r.getValue().toString(), world);
        });
        return worlds;
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        return CommandUtils.COMMAND_SUCCESS;
    }
}
