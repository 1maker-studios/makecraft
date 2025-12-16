package com.metype.makecraft.command;

import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RankIDProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        List<Rank> ranks = RankUtils.getRanks();

        if(context.getSource().isExecutedByPlayer()) {
            ranks = RankUtils.getRanksForUser(context.getSource().getPlayerOrThrow().getUuid());
        }

        String input = builder.getRemainingLowerCase();
        for(Rank r : ranks) {
            if(r.id.contains(input)) {
                builder.suggest(r.id);
            }
        }
        return builder.buildFuture();
    }
}
