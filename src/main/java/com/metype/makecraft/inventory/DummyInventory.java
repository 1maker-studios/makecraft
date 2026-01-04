package com.metype.makecraft.inventory;


import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyInventory implements Inventory {
    protected List<ItemStack> items = new ArrayList<>();

    public DummyInventory() {
        for(int i = 0; i < size(); i++) {
            items.add(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int size() {
        return 45;
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        if(slot < 0 || slot >= items.size()) return ItemStack.EMPTY;
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack get = getStack(slot);
        if(amount < 0) amount = get.getCount();
        if(get.isEmpty()) return ItemStack.EMPTY;
        ItemStack ret = get.copyWithCount(amount);
        get.setCount(get.getCount() - amount);
        return ret;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, -1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {

    }
}
