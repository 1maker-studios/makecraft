package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.gamemode.GamemodeAdventureCommand;
import com.metype.makecraft.command.gamemode.GamemodeCreativeCommand;
import com.metype.makecraft.command.gamemode.GamemodeSpectatorCommand;
import com.metype.makecraft.command.gamemode.GamemodeSurvivalCommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class RankBaseCommand implements ICommand {

    public static Text HELP = Text.of("Used to interact with ranks.");

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        LiteralArgumentBuilder<ServerCommandSource> rankCommand = literal("rank");

        new RankCreateCommand().build().forEach(rankCommand::then);
        new RankDeleteCommand().build().forEach(rankCommand::then);
        new RankGiveCommand().build().forEach(rankCommand::then);
        new RankRevokeCommand().build().forEach(rankCommand::then);
        new RankSelectCommand().build().forEach(rankCommand::then);
        new RankUseCommand().build().forEach(rankCommand::then);
        new RankSetCommand().build().forEach(rankCommand::then);

        rankCommand.executes(this::execute);

        return List.of(rankCommand);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> HELP, false);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
