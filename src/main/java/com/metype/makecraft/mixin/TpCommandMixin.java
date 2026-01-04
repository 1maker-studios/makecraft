package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.isaiah.multiworld.command.TpCommand;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TpCommand.class)
public class TpCommandMixin {
    @ModifyExpressionValue(method = "run", at = @At(value = "INVOKE", target = "Lme/isaiah/multiworld/command/TpCommand;read_env_from_config(Ljava/lang/String;)Ljava/lang/String;"))
    private static String read_env_from_config(String arg) {
        if(arg.contains("end")) {
            return "END";
        }
        if(arg.contains("nether")) {
            return "NETHER";
        }
        return arg;
    }

    @WrapMethod(method = "method_29200_createEndSpawnPlatform")
    private static void createEndSpawnPlatform(ServerWorld world, Operation<Void> original) {
        // do fuck all, we handle this elsewhere lmao
    }
}
