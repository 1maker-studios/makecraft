package com.metype.makecraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metype.makecraft.serialization.LocationAdapter;
import com.metype.makecraft.types.Location;
import net.minecraft.util.math.BlockPos;

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

    public static MainConfig load() {
        return (new MainConfig()).createAndLoad();
    }

    public String restartTime = "7d 0h 0m 0s";
    public boolean useFarmWorlds = true;
    public boolean useFarmWorldOverworld = true;
    public boolean useFarmWorldNether = true;
    public boolean useFarmWorldEnd = true;
    public Location spawnLocation = new Location();
    public String joinRank = "";
}
