package com.metype.makecraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.serialization.LocationAdapter;
import com.metype.makecraft.types.Location;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public class MainConfig extends ConfigFile<MainConfig> {
    @Override
    protected String getPath() {
        return "config.json";
    }

    @Override
    protected MainConfig defaultConfig() {
        return new MainConfig();
    }

    @Override
    protected Class<MainConfig> getConfigClass() {
        return MainConfig.class;
    }

    @Override
    protected Gson getGsonInstance() {
        return new GsonBuilder().setPrettyPrinting().
                registerTypeAdapter(Location.class, new LocationAdapter())
                .create();
    }

    public static void load() {
        MakeCraft.MAIN_CONFIG = (new MainConfig()).createAndLoad();
        CONFIG_RELOADED.invoker().onConfigReloaded();
    }

    public static final Event<ConfigReloaded> CONFIG_RELOADED = EventFactory.createArrayBacked(ConfigReloaded.class, callbacks -> () -> {
        for (ConfigReloaded event : callbacks) {
            event.onConfigReloaded();
        }
    });

    public String restartTime = "7d 0h 0m 0s";
    public boolean useFarmWorlds = true;
    public boolean useFarmWorldOverworld = true;
    public boolean useFarmWorldNether = true;
    public boolean useFarmWorldEnd = true;
    public Location spawnLocation = new Location();
    public String joinRank = "";
    public long rtpCooldownSeconds = 600;
    public Map<String, String> commandAliases = Map.of("r", "reply", "b", "back");
}
