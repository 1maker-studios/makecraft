package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class ServerPlayerEntityMixin {

    @WrapMethod(method = "getDisplayName")
    public Text getDisplayName(Operation<Text> original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Rank userRank = RankUtils.getRankForUser(player.getUuid()).orElse(null);
        if(userRank == null) return original.call();
        return userRank.getRankFormatting(original.call().getString());
    }

}
