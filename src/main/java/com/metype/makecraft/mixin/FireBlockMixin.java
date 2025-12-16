package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireBlock.class)
public class FireBlockMixin {
//    @WrapMethod(method = "trySpreadingFire")
//    public void trySpreadingFire(World world, BlockPos pos, int spreadFactor, Random random, int currentAge, Operation<Void> original) {
//        original.call(world, pos, 1, random, 0);
//    }

    @WrapMethod(method = "getBurnChance(Lnet/minecraft/block/BlockState;)I")
    public int getBurnChance(BlockState state, Operation<Integer> original) {
        if(state.getBlock() == Blocks.AIR) {
            return 0;
        }
        return 10;
    }

    @WrapMethod(method = "getSpreadChance")
    public int getSpreadChance(BlockState state, Operation<Integer> original) {
        return 100;
    }
}
