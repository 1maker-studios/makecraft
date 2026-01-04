package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.isaiah.multiworld.command.CreateCommand;
import me.isaiah.multiworld.command.TpCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CreateCommand.class)
public class CreateCommandMixin {
    @ModifyArg(method = "reinit_world_from_config", at = @At(value = "INVOKE", target = "Lme/isaiah/multiworld/config/FileConfiguration;getString(Ljava/lang/String;)Ljava/lang/String;"))
    private static String read_env_from_config(String id) {
        return "custom_generator";
    }
}
