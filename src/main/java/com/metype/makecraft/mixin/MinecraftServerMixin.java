package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.types.Location;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @WrapMethod(method = "getSpawnPoint")
    public WorldProperties.SpawnPoint getSpawnPoint(Operation<WorldProperties.SpawnPoint> original) {
        Location spawnPos = MakeCraft.MAIN_CONFIG.spawnLocation;
        if(spawnPos == null) return original.call();
        return new WorldProperties.SpawnPoint (
                new GlobalPos(
                        World.OVERWORLD,
                        spawnPos.getBlockPos()
                ),
                spawnPos.getYaw(),
                spawnPos.getPitch()
        );
    }
}
