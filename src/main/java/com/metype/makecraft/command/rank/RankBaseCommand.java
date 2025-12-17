package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.screen.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.*;

import static net.minecraft.server.command.CommandManager.literal;

public class RankBaseCommand implements ICommand {

    public static Text HELP = Text.of("Used to interact with ranks.");

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("rank")
                .then(new RankCreateCommand().register())
                .then(new RankDeleteCommand().register())
                .then(new RankGiveCommand().register())
                .then(new RankRevokeCommand().register())
                .then(new RankSelectCommand().register())
                .then(new RankUseCommand().register())
                .then(new RankSetCommand().register())
                .executes(this::execute);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> HELP, false);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
