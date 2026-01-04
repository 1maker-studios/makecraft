package com.metype.makecraft.command.farmworld;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.utils.CommandUtils;
import com.metype.makecraft.utils.Utils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class FarmworldNetherTPCommand implements ICommand {

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("nether")
                .requires(Permissions.require("makecraft.farmworld.nether", 0))
                .executes(this::execute));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        MakeCraft.SERVER.getCommandManager().parseAndExecute(player.getCommandSource(), "mw tp farmworld:nether");
        Utils.registerTickAction((server) -> {
            player.teleport(player.lastX + 0.5f, player.lastY, player.lastZ + 0.5, true);
            return false;
        });
        return CommandUtils.COMMAND_SUCCESS;
    }
}
