package com.metype.makecraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public interface ICommand {
    void register(CommandDispatcher<ServerCommandSource> dispatcher);
    int execute(CommandContext<ServerCommandSource> context);
}
