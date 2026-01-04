package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.providers.RankIDProvider;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankSetCommand implements ICommand {

    public static Text HELP = Text.of("Used to modify parameters of ranks.");

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        RequiredArgumentBuilder<ServerCommandSource, Identifier> setIDArg = argument("identifier", IdentifierArgumentType.identifier())
                .suggests(RankIDProvider.all());

        new RankSetNameColorCommand().build().forEach(setIDArg::then);
        new RankSetRankColorCommand().build().forEach(setIDArg::then);
        new RankSetNameCommand().build().forEach(setIDArg::then);

        LiteralArgumentBuilder<ServerCommandSource> setCommand = literal("set")
                .requires(Permissions.require("makecraft.rank.set", 2))
                .then(setIDArg);

        setCommand.executes(this::execute);

        return List.of(setCommand);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(() -> HELP, false);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
