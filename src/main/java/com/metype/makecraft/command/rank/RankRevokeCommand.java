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

import java.util.List;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankRevokeCommand implements ICommand {

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("revoke")
                .requires(Permissions.require("makecraft.rank.give", 2))
                .then(argument("identifier", IdentifierArgumentType.identifier())
                        .suggests(RankIDProvider.all())
                        .then(argument("player", EntityArgumentType.player())
                                .executes(this::execute)
                        )
                ));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "identifier");
        ServerPlayerEntity user = EntityArgumentType.getPlayer(context, "player");
        Optional<Rank> rank = RankUtils.byID(id.toString());
        if(rank.isEmpty()) {
            context.getSource().sendFeedback(() -> Text.of("No rank found."), false);
            return CommandUtils.COMMAND_FAILURE;
        }
        RankUtils.removeRankFromUser(user.getUuid(), rank.get());
        context.getSource().sendFeedback(() -> Text.of("Rank " + id + " revoked from " + user.getStringifiedName()), true);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
