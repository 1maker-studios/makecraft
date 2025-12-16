package com.metype.makecraft.rank;

import net.minecraft.text.Style;

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
}
