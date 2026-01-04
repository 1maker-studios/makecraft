package com.metype.makecraft.command;

import com.metype.makecraft.config.MainConfig;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class ReloadConfigCommand implements ICommand {
    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("reload")
                .requires(cs -> Permissions.check(cs, "makecraft.reload", 2))
                .executes(this::execute));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MainConfig.load();
        context.getSource().sendFeedback(() -> Text.of("Reloaded config."), true);
        return CommandUtils.COMMAND_SUCCESS;
    }
}
