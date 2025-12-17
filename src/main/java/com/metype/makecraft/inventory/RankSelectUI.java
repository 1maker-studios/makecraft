package com.metype.makecraft.inventory;

import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Colors;

import java.util.ArrayList;
import java.util.List;

public class RankSelectUI extends InteractiveInventory {

    private final List<Rank> shownRanks;
    private final ServerPlayerEntity player;

    public RankSelectUI(ServerPlayerEntity player) {
        super(player);
        this.player = player;
        shownRanks = RankUtils.getRanksForUser(player.getUuid());
        int i = 0;

        Rank selectedRank = RankUtils.getRankForUser(player.getUuid()).orElse(null);

        for(Rank r : shownRanks) {
            List<Text> lore = new ArrayList<>(List.of(
                    MutableText.of(PlainTextContent.of("Name Color:")).setStyle(Style.EMPTY.withItalic(false).withColor(Colors.WHITE)),
                    MutableText.of(PlainTextContent.of(player.getStringifiedName())).setStyle(r.name_color)
            ));

            lore.add(MutableText.of(PlainTextContent.of("")));

            if(selectedRank == r) {
                lore.add(MutableText.of(PlainTextContent.of("Click To Deselect")).setStyle(Style.EMPTY.withItalic(false).withColor(Colors.LIGHT_RED)));
            } else {
                lore.add(MutableText.of(PlainTextContent.of("Click To Select")).setStyle(Style.EMPTY.withItalic(false).withColor(Colors.GREEN)));
            }

            uiButtons.put(rawIdToSlot(i),
                    new ItemStack(RegistryEntry.of(Items.NAME_TAG), 1,
                            ComponentChanges.builder().add(
                                    Component.of(
                                            DataComponentTypes.CUSTOM_NAME,
                                            MutableText.of(PlainTextContent.of(r.name.isEmpty() ? "Color Rank" : r.name)).setStyle(r.rank_color)
                                    )
                            ).add(
                                    Component.of(
                                            DataComponentTypes.LORE,
                                            new LoreComponent(lore)
                                    )
                            ).build()
                    )
            );
            i++;
        }
    }

    private int rawIdToSlot(int raw) {
        return raw + (2 * Math.floorDiv(raw, 7)) + 10;
    }

    private int slotToRawID(int slot) {
        return slot - 10 - (2 * (Math.floorDiv(slot, 9) - 1));
    }

    @Override
    public int size() {
        return 45;
    }

    @Override
    public void leftClick(int slot) {
        slot = slotToRawID(slot);
        if(shownRanks.size() <= slot) return;
        Rank selectedRank = shownRanks.get(slot);
        int ret = RankUtils.setRankForUser(player.getUuid(), selectedRank.id);
        switch(ret) {
            case -1:
                player.sendMessage(Text.of("An error occurred applying rank " + selectedRank.id + ". Contact staff."));
                break;
            case 0:
                player.sendMessage(MutableText.of(PlainTextContent.of("Applied rank! You are now ")).append(player.getName()));
                break;
            case 1:
                player.sendMessage(Text.of("User already had rank applied, it has been removed."));
                break;
        }
        player.closeHandledScreen();
    }

    @Override
    public void shiftClick(int slot) {
        super.shiftClick(slot);
    }
}
