package com.metype.makecraft.command.trash;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.inventory.DummyInventory;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class DisposalCommand  implements ICommand {
    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        LiteralArgumentBuilder<ServerCommandSource> disposalCommand = literal("disposal")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .requires(cs -> Permissions.check(cs, "makecraft.disposal", 0));

        disposalCommand.executes(this::execute);

        return List.of(disposalCommand);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return MutableText.of(PlainTextContent.of("Disposal")).withColor(14162206);
            }

            @Override
            public @NonNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, playerInventory, new DummyInventory(), 5);
            }
        });
        return CommandUtils.COMMAND_SUCCESS;
    }
}
