package com.metype.makecraft.command;

import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import static net.minecraft.server.command.CommandManager.literal;

public class BaseCommand implements ICommand {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        String[] aliases = new String[]{"makecraft"};

        for(String alias : aliases) {
            LiteralArgumentBuilder<ServerCommandSource> args = literal(alias).requires(cs -> Permissions.check(cs, "makecraft.use", 5) )
                    .executes(this::execute);
            dispatcher.register(args);
        }
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {

        return CommandUtils.COMMAND_SUCCESS;
    }
}
