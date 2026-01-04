package com.metype.makecraft.command.gamemode;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.farmworld.FarmworldEndTPCommand;
import com.metype.makecraft.command.farmworld.FarmworldNetherTPCommand;
import com.metype.makecraft.command.farmworld.FarmworldOverworldTPCommand;
import com.metype.makecraft.command.farmworld.FarmworldRefreshCommand;
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

public class GamemodeBaseCommand implements ICommand {

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        LiteralArgumentBuilder<ServerCommandSource> gmCommand = literal("gm")
                .requires(Permissions.require("makecraft.gamemode", 2));

        new GamemodeSurvivalCommand().build().forEach(gmCommand::then);
        new GamemodeCreativeCommand().build().forEach(gmCommand::then);
        new GamemodeAdventureCommand().build().forEach(gmCommand::then);
        new GamemodeSpectatorCommand().build().forEach(gmCommand::then);

        gmCommand.executes(this::execute);

        return List.of(gmCommand);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        GameMode playerMode = player.getGameMode();
        player.sendMessage(Text.of(playerMode.getId()));
        return CommandUtils.COMMAND_SUCCESS;
    }
}
