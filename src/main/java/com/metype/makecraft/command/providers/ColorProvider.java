package com.metype.makecraft.command.providers;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class ColorProvider implements SuggestionProvider<ServerCommandSource> {

    private ColorProvider() {

    }

    public static ColorProvider color() {
        return new ColorProvider();
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String input = builder.getRemainingLowerCase();
        if(input.isEmpty()) {
            builder.suggest("#FFFFFF");
            builder.suggest("#FFF");
        } else {
            if(!input.startsWith("#")) {
                throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Color must begin with #")), new LiteralMessage("Color must begin with #"));
            }
            if(input.length() <= 4) {
                builder.suggest("#FFF");
            }
            if(input.length() <= 7) {
                builder.suggest("#FFFFFF");
            } else {
                throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Too many characters")), new LiteralMessage("Too many characters"));
            }
        }

        return builder.buildFuture();
    }

    public static int parse(String colorCode) throws CommandSyntaxException {
        if(colorCode == null) throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Color must begin with #")), new LiteralMessage("Color must begin with #"));
        if(!colorCode.startsWith("#")) throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Color must begin with #")), new LiteralMessage("Color must begin with #"));

        colorCode = colorCode.substring(1);

        if(colorCode.length() == 3) {
            char r = colorCode.charAt(0);
            char g = colorCode.charAt(1);
            char b = colorCode.charAt(2);
            colorCode = "" + r + r + g + g + b + b;
        }

        try {
            int val = Integer.parseInt(colorCode, 16);
            if(val < 0) throw new NumberFormatException();

            return val;
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage("Invalid color.")), new LiteralMessage("Invalid color."));
        }
    }
}
