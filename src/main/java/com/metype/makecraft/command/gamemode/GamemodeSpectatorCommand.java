package com.metype.makecraft.command.gamemode;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class GamemodeSpectatorCommand implements ICommand {

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("spectator")
                .requires(Permissions.require("makecraft.gamemode.spectator", 2))
                .executes(this::execute));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        GameMode playerMode = player.getGameMode();
        if(CommandUtils.savePlayerInventoryTo(playerMode.getId(), player)) {
            context.getSource().sendFeedback(() -> Text.of("Failed to store inventory data, bailing."), true);
            return CommandUtils.COMMAND_FAILURE;
        }

        GameMode newMode = GameMode.SPECTATOR;
        player.changeGameMode(newMode);
        CommandUtils.loadPlayerInventoryFrom(newMode.getId(), player);

        return CommandUtils.COMMAND_SUCCESS;
    }
}
