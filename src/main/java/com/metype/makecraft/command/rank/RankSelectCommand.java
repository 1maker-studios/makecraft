package com.metype.makecraft.command.rank;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.inventory.InteractOnlyScreenHandler;
import com.metype.makecraft.inventory.RankSelectUI;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jspecify.annotations.NonNull;

import static net.minecraft.server.command.CommandManager.literal;

public class RankSelectCommand implements ICommand {

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> register() {
        return literal("select")
                .requires(Permissions.require("makecraft.rank.use", 2))
                .executes(this::execute);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if(!context.getSource().isExecutedByPlayer()) return CommandUtils.COMMAND_FAILURE;
        context.getSource().getPlayerOrThrow().openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.of("Select Rank");
            }

            @Override
            public @NonNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new InteractOnlyScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, playerInventory, new RankSelectUI((ServerPlayerEntity) player), 5);
            }
        });
        return CommandUtils.COMMAND_SUCCESS;
    }
}
