package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.providers.RankIDProvider;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankSetCommand implements ICommand {

    public static Text HELP = Text.of("Used to modify parameters of ranks.");

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("set")
                .requires( Permissions.require("makecraft.rank.set", 2))
                .then(argument("identifier", IdentifierArgumentType.identifier())
                        .suggests(RankIDProvider.all())
                        .then(new RankSetNameColorCommand().register())
                        .then(new RankSetRankColorCommand().register())
                        .then(new RankSetNameCommand().register())
                ).executes(this::execute);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendFeedback(() -> HELP, false);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
