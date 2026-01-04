package com.metype.makecraft.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Unique
    private boolean dontSetSpawnPoint = false;

    @Inject(method = "trySleep", at = @At(value = "HEAD"))
    public void trySleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        dontSetSpawnPoint = self.getEntityWorld().getRegistryKey() != World.OVERWORLD;
    }

    @Inject(method = "setSpawnPoint", at = @At(value = "HEAD"), cancellable = true)
    public void trySleep(ServerPlayerEntity.Respawn respawn, boolean sendMessage, CallbackInfo ci) {
        if(dontSetSpawnPoint) ci.cancel();
    }
}
