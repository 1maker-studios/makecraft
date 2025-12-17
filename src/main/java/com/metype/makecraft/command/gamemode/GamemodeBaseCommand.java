package com.metype.makecraft.command.gamemode;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.command.farmworld.FarmworldNetherTPCommand;
import com.metype.makecraft.command.farmworld.FarmworldOverworldTPCommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import static net.minecraft.server.command.CommandManager.literal;

public class GamemodeBaseCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("gm")
                .requires(Permissions.require("makecraft.gamemode", 2))
                .then(new GamemodeSurvivalCommand().register())
                .then(new GamemodeCreativeCommand().register())
                .then(new GamemodeAdventureCommand().register())
                .then(new GamemodeSpectatorCommand().register())
                .executes(this::execute);
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
