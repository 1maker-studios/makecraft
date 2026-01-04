package com.metype.makecraft.command;

import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public interface ICommand {
    List<LiteralArgumentBuilder<ServerCommandSource>> build();
    default int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return CommandUtils.COMMAND_SUCCESS;
    }
    default void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        build().forEach(dispatcher::register);
    }
}
