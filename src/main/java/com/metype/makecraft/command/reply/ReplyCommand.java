package com.metype.makecraft.command.reply;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ReplyCommand implements ICommand {

    public final static Map<UUID, Collection<ServerPlayerEntity>> MostRecentMessage = new HashMap<>();

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("reply")
                    .requires(ServerCommandSource::isExecutedByPlayer)
                    .then(argument("message", StringArgumentType.greedyString())
                        .executes(this::execute)
                ));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        Collection<ServerPlayerEntity> targets = MostRecentMessage.getOrDefault(player.getUuid(), List.of());

        targets = targets.stream()
                .map(playerEntity -> {
                    if(playerEntity.networkHandler.isConnectionOpen()) {
                        return playerEntity;
                    }
                    return null;
                }).filter(Objects::nonNull).toList();

        MostRecentMessage.put(player.getUuid(), targets);

        if(targets.isEmpty()) {
            player.sendMessage(Component.text("No targets to reply to."));
            return CommandUtils.COMMAND_FAILURE;
        }

        String message = StringArgumentType.getString(context, "message");

        for(ServerPlayerEntity playerEntity: targets) {
            MakeCraft.SERVER.getCommandManager().parseAndExecute(context.getSource(), String.format("/msg %s %s", playerEntity.getName().getString(), message));
        }

        return CommandUtils.COMMAND_SUCCESS;
    }
}
