package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.providers.RankIDProvider;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankUseCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("use")
                .requires(Permissions.require("makecraft.rank.use", 2))
                .then(argument("identifier", IdentifierArgumentType.identifier())
                        .suggests(RankIDProvider.personal())
                        .executes(this::execute)
                );
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");

        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        Optional<Rank> rank = RankUtils.byID(id.toString());

        if(rank.isEmpty() || !RankUtils.getRanksForUser(player.getUuid()).contains(rank.get())) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }

        int ret = RankUtils.setRankForUser(player.getUuid(), rank.get().id);
        switch(ret) {
            case -1:
                context.getSource().sendFeedback(() -> Text.of("An error occurred applying rank " + id + ". Contact staff."), true);
                break;
            case 0:
                context.getSource().sendFeedback(() -> rank.get().getRankFormatting(player.getStringifiedName()), false);
                break;
            case 1:
                context.getSource().sendFeedback(() -> Text.of("User already had rank applied, it has been removed."), false);
                break;
        }
        return CommandUtils.COMMAND_SUCCESS;
    }
}
