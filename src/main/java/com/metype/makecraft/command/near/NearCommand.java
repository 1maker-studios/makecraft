package com.metype.makecraft.command.near;

import com.metype.makecraft.command.ICommand;
import com.metype.makecraft.inventory.DummyInventory;
import com.metype.makecraft.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class NearCommand implements ICommand {
    @Override
    public List<LiteralArgumentBuilder<ServerCommandSource>> build() {
        LiteralArgumentBuilder<ServerCommandSource> nearCommand = literal("near")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .requires(cs -> Permissions.check(cs, "makecraft.near", 0));

        nearCommand.executes(this::execute);

        return List.of(nearCommand);
    }

    @Override
    public int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ServerWorld world = player.getEntityWorld();
        Vec3d pos = player.getEntityPos();
        player.sendMessage(Component.text("Locating nearby players...", TextColor.color(0x72, 0xDE, 0x96)));
        for(ServerPlayerEntity p : world.getPlayers()) {
            if(p == player) continue;;
            double distance = p.getEntityPos().distanceTo(pos);
            if(distance < 100.0f) {
                MutableText name = Objects.requireNonNull(p.getDisplayName()).copy();
                name = name.append(" is ").append(MutableText.of(PlainTextContent.of(String.valueOf((int)distance))).withColor(12566296)).append(MutableText.of(PlainTextContent.of(" blocks away.")).withColor(-1));
                player.sendMessage(name);
            }
        }
        return CommandUtils.COMMAND_SUCCESS;
    }
}
