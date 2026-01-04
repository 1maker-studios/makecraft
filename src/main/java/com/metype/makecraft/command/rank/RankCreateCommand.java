package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankCreateCommand implements ICommand {

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("create")
                .requires(Permissions.require("makecraft.rank.create", 2))
                .then(argument("identifier", IdentifierArgumentType.identifier())
                        .then(argument("rank_name", StringArgumentType.string())
                                .executes(this::execute)
                        )
                ));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        String rank_name = StringArgumentType.getString(context, "rank_name");
        if(RankUtils.byID(id.toString()).isPresent()) {
            context.getSource().sendFeedback(() -> Text.of("A rank with this ID already exists."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        Rank rank = new Rank(id.asString(), rank_name, Style.EMPTY, Style.EMPTY);
        RankUtils.addRank(rank);
        context.getSource().sendFeedback(() -> Text.of("Rank " + id + " created!"), true);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
