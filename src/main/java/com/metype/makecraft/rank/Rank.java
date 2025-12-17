package com.metype.makecraft.rank;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

public class Rank {
    public String id;
    public String name;
    public Style rank_color;
    public Style name_color;

    public Rank(String id, String name, Style rank_color, Style name_color) {
        this.id = id;
        this.name = name;
        this.rank_color = rank_color;
        this.name_color = name_color;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Rank rank) {
            return rank.id.equals(id);
        }
        return false;
    }

    public Text getRankFormatting(String originalName) {
        MutableText playerName = MutableText.of(PlainTextContent.of(originalName)).setStyle(name_color);
        if(!name.isEmpty()) {
            MutableText playerRank = MutableText.of(PlainTextContent.of("["))
                    .setStyle(Style.EMPTY.withColor(Colors.GRAY))
                    .append(MutableText.of(PlainTextContent.of(name))
                            .setStyle(rank_color)
                            .append(MutableText.of(PlainTextContent.of("]"))
                                    .setStyle(Style.EMPTY.withColor(Colors.GRAY))));
            return playerRank.append(" ").append(playerName);
        }
        return playerName;
    }
}
