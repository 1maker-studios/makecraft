package com.metype.makecraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.isaiah.multiworld.command.TpCommand;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static com.metype.makecraft.command.reply.ReplyCommand.MostRecentMessage;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {
    @Inject(method="execute", at = @At("HEAD"))
    private static void execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message, CallbackInfo ci) throws CommandSyntaxException {
        if(source.isExecutedByPlayer()) {
            ServerPlayerEntity executor = source.getPlayerOrThrow();
            MostRecentMessage.put(executor.getUuid(), targets);
            for(ServerPlayerEntity target: targets) {
                MostRecentMessage.put(target.getUuid(), List.of(executor));
            }
        }
    }
}
