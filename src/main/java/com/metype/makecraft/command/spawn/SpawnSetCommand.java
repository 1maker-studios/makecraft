package com.metype.makecraft.command.spawn;

import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.types.Location;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class SpawnSetCommand implements ICommand {

    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        return List.of(literal("set")
                .requires(cs -> Permissions.check(cs, "makecraft.spawn.set", 2))
                .executes(this::execute));
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(!source.isExecutedByPlayer()) {
            source.sendFeedback(() -> Text.of("You cannot set the Spawn as console."), false);
        }
        ServerPlayerEntity player = source.getPlayer();
        assert player != null;

        MakeCraft.MAIN_CONFIG.spawnLocation = new Location(player.getBlockPos(), player.getPitch(), player.getYaw(), player.getEntityWorld());
        MakeCraft.MAIN_CONFIG.save();

        source.sendFeedback(() -> Text.of("Set spawn."), false);

        return CommandUtils.COMMAND_SUCCESS;
    }
}
