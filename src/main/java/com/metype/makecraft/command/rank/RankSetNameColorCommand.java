package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.providers.ColorProvider;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankSetNameColorCommand implements ICommand {
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("name_color").
                then(argument("color", StringArgumentType.greedyString()).
                        suggests(ColorProvider.color()).
                        executes(this::execute)
                );
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        String colorStr = StringArgumentType.getString(context, "color");
        int color = ColorProvider.parse(colorStr);
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        Rank rankVal = rank.get();
        rankVal.name_color = rankVal.name_color.withColor(color);
        RankUtils.addRank(rankVal);
        context.getSource().sendFeedback(() -> MutableText.of(PlainTextContent.of("Updated rank to display as: ")).append(rank.get().getRankFormatting("Player")), true);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
