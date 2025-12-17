package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.providers.RankIDProvider;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankDeleteCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("delete")
                .requires(Permissions.require("makecraft.rank.delete", 2))
                .then(argument("identifier", IdentifierArgumentType.identifier())
                        .suggests(RankIDProvider.all())
                        .executes(this::execute)
                );
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        RankUtils.deleteRank(id.toString());
        context.getSource().sendFeedback(() -> Text.of("Rank " + id + " deleted!"), true);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
