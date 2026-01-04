package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.metype.makecraft.rank.Rank;
import com.metype.makecraft.rank.RankUtils;
import net.minecraft.block.BedBlock;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;
import net.minecraft.text.object.PlayerTextObjectContents;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @WrapMethod(method = "getDisplayName")
    public Text applyRank(Operation<Text> original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Rank userRank = RankUtils.getRankForUser(player.getUuid()).orElse(null);
        if(userRank == null) return original.call();
        return userRank.getRankFormatting(original.call().getString());
    }

    @WrapMethod(method = "getDisplayName")
    public Text addPlayerSkull(Operation<Text> original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        MutableText playerHead = Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(player.getUuid()), true));
        return playerHead.append(" ").append(original.call());
    }

}
