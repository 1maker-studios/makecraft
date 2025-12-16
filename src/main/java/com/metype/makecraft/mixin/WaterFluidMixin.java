package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(FlowableFluid.class)
public class WaterFluidMixin {

    @WrapMethod(method="getSpread")
    protected Map<Direction, FluidState> getSpread(ServerWorld world, BlockPos pos, BlockState state, Operation<Map<Direction, FluidState>> original) {
        FlowableFluid self = (FlowableFluid) (Object) this;
        Map<Direction, FluidState> map = original.call(world, pos, state);
        map.replaceAll((d, v) -> self.getStill(false));
        return map;
    }
}
