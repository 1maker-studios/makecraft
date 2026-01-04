package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.isaiah.multiworld.portal.WandEventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WandEventHandler.class)
public class WandEventHandlerMixin { // Literally just exists to NOP out annoying useless methods.
    @WrapMethod(method = "leftClickBlock")
    private static ActionResult leftClickBlock(PlayerEntity player, World world, BlockPos pos, Operation<ActionResult> original) {
        return ActionResult.PASS;
    }

    @WrapMethod(method = "rightClickBlock")
    private static ActionResult rightClickBlock(PlayerEntity player, World world, BlockHitResult hitResult, Operation<ActionResult> original) {
        return ActionResult.PASS;
    }
}
