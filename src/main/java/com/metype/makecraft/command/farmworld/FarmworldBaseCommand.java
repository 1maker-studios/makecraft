package com.metype.makecraft.command.farmworld;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class FarmworldBaseCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("farm")
                .then(new FarmworldOverworldTPCommand().register())
                .then(new FarmworldNetherTPCommand().register())
                .then(new FarmworldOverworldTPCommand().register());
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        return CommandUtils.COMMAND_SUCCESS;
    }
}
