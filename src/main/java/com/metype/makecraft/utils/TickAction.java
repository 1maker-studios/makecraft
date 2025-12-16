package com.metype.makecraft.utils;

import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public interface TickAction {
    boolean execute(MinecraftServer server);
}