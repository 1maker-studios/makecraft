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

import java.util.HashMap;
import java.util.Map;

public class InteractiveInventory implements Inventory {
    protected final Map<Integer, ItemStack> uiButtons = new HashMap<>();
    protected final ItemStack bgItem = new ItemStack(RegistryEntry.of(Items.LIGHT_GRAY_STAINED_GLASS_PANE), 1, ComponentChanges.builder().add(Component.of(DataComponentTypes.CUSTOM_NAME, Text.of(""))).build());

    protected ServerPlayerEntity interactor;

    public InteractiveInventory(ServerPlayerEntity interactor) {
        this.interactor = interactor;
    }

    @Override
    public int size() {
        return 45;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return uiButtons.getOrDefault(slot, bgItem);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        leftClick(slot);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        leftClick(slot);
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

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

    public void leftClick(int slot) {

    }

    public void shiftClick(int slot) {

    }
}
