package com.metype.makecraft.command;

import com.metype.makecraft.command.spawn.SpawnSetCommand;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class BaseCommand implements ICommand {
    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        LiteralArgumentBuilder<ServerCommandSource> makecraftCommand = literal("makecraft")
                .requires(Permissions.require("makecraft.use", 2));

        new ReloadConfigCommand().build().forEach(makecraftCommand::then);

        makecraftCommand.executes(this::execute);

        return List.of(makecraftCommand);
    }
}
