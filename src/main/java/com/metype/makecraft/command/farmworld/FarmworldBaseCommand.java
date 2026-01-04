package com.metype.makecraft.command.farmworld;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class FarmworldBaseCommand implements ICommand {

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        LiteralArgumentBuilder<ServerCommandSource> farmCommand = literal("farm");

        new FarmworldOverworldTPCommand().build().forEach(farmCommand::then);
        new FarmworldNetherTPCommand().build().forEach(farmCommand::then);
        new FarmworldEndTPCommand().build().forEach(farmCommand::then);
        new FarmworldRefreshCommand().build().forEach(farmCommand::then);

        return List.of(farmCommand);
    }
}
