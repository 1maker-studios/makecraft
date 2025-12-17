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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankGiveCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("give")
                .requires(Permissions.require("makecraft.rank.give", 2))
                .then(argument("identifier", IdentifierArgumentType.identifier())
                        .suggests(RankIDProvider.all())
                        .then(argument("players", EntityArgumentType.players())
                                .executes(this::execute)
                        )
                );
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        Collection<ServerPlayerEntity> users = EntityArgumentType.getPlayers(context, "players");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        RankUtils.giveRankToUsers(rank.get(), users, context);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
