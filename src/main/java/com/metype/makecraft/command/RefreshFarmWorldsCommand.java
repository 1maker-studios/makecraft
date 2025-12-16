package com.metype.makecraft.command;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.events.ServerCloseEventListener;
import com.metype.makecraft.types.Location;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Set;

public class RefreshFarmWorldsCommand implements ICommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] aliases = new String[]{"refresh-fw"};

        for(String alias : aliases) {
            LiteralArgumentBuilder<ServerCommandSource> args = CommandManager.literal(alias).requires(cs -> Permissions.check(cs, "makecraft.refresh.farmworld", 2) )
                    .executes(this::execute);
            dispatcher.register(args);
        }
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        ServerCloseEventListener.refreshFarmWorlds(context.getSource().getServer());
        return CommandUtils.COMMAND_SUCCESS;
    }
}
