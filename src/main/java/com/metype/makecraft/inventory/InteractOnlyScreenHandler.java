package com.metype.makecraft.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class InteractOnlyScreenHandler extends GenericContainerScreenHandler {
    public InteractOnlyScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory, int rows) {
        super(type, syncId, playerInventory, inventory, rows);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        if(getInventory() instanceof InteractiveInventory inventory) {
            inventory.shiftClick(slot);
        }
        return ItemStack.EMPTY;
    }
}

