package com.metype.makecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public interface ICommand {
    LiteralArgumentBuilder<ServerCommandSource> register();
    int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
}
