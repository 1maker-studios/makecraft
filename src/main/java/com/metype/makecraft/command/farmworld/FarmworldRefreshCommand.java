package com.metype.makecraft.command.farmworld;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.events.ServerCloseEventListener;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class FarmworldRefreshCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("refresh")
                .requires(Permissions.require("makecraft.farmworld.refresh", 2))
                .executes(this::execute);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        ServerCloseEventListener.refreshFarmWorlds(context.getSource().getServer());
        return CommandUtils.COMMAND_SUCCESS;
    }
}
