package com.metype.makecraft.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.text.MutableText;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.ErrorReporter;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class CommandUtils {

    public static final int COMMAND_FAILURE = 0;
    public static final int COMMAND_SUCCESS = 1;

    public static Optional<ServerPlayerEntity> commandContextServerPlayer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(!source.isExecutedByPlayer()) {
            return Optional.empty();
        }
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(player);
    }

    private static void appendLore(ItemStack stack, MutableText line) {
        LoreComponent existing = stack.get(DataComponentTypes.LORE);
        if (existing == null) {
            existing = LoreComponent.DEFAULT;
        }
        existing = existing.with(line);
        stack.set(DataComponentTypes.LORE, existing);
    }

    public static String format(String string, Object... objects) {
        for(Object o : objects) {
            string = string.replaceFirst("\\{}", String.valueOf(o));
        }
        return string;
    }

    public static String format(String input, Map<String, Object> args) {
        for(String argID : args.keySet()) {
            if(!input.contains("{" + argID  + "}")) continue;
            input = input.replaceFirst("\\{" + argID  + "}", args.get(argID).toString());
        }
        return input;
    }

    public static boolean savePlayerInventoryTo(String dataName, ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();

        NbtWriteView wv = NbtWriteView.create(ErrorReporter.EMPTY, player.getEntityWorld().getRegistryManager());

        inv.writeData(wv.getListAppender("Inventory", StackWithSlot.CODEC));

        NbtWriteView.ListAppender<StackWithSlot> list = wv.getListAppender("Equipment", StackWithSlot.CODEC);

        for(int slot : PlayerInventory.EQUIPMENT_SLOTS.keySet()) {
            ItemStack itemStack = inv.getStack(slot);
            if (!itemStack.isEmpty()) {
                list.add(new StackWithSlot(slot, itemStack));
            }
        }

        String invData = wv.getNbt().toString();

        try {
            DBUtils.getInstance().saveInventory(player.getUuid(), dataName, invData);
        } catch (SQLException e) {
            return true;
        }
        return false;
    }

    public static void loadPlayerInventoryFrom(String dataName, ServerPlayerEntity player) {
        try {
            String data = DBUtils.getInstance().getInventory(player.getUuid(), dataName);
            PlayerInventory inv = player.getInventory();
            inv.clear();
            if(data == null) {
                return;
            }
            ReadView rv = NbtReadView.create(ErrorReporter.EMPTY, player.getEntityWorld().getRegistryManager(), NbtHelper.fromNbtProviderString(data));
            inv.readData(rv.getTypedListView("Inventory", StackWithSlot.CODEC));
            ReadView.TypedListReadView<StackWithSlot> equipment = rv.getTypedListView("Equipment", StackWithSlot.CODEC);
            equipment.forEach(stackWithSlot -> {
                inv.setStack(stackWithSlot.slot(), stackWithSlot.stack());
            });
        } catch (CommandSyntaxException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
