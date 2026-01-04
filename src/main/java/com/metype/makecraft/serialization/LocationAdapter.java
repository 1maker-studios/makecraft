package com.metype.makecraft.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.metype.makecraft.MakeCraft;
import com.metype.makecraft.types.Location;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.io.IOException;

public class LocationAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject();
        out.name("x");
        out.value(value.getX());
        out.name("y");
        out.value(value.getY());
        out.name("z");
        out.value(value.getZ());
        out.name("pitch");
        out.value(value.getPitch());
        out.name("yaw");
        out.value(value.getYaw());
        out.name("world");
        if(value.getWorld() == null) {
            out.value((String) null);
        } else {
            out.value(value.getWorld().getRegistryKey().getValue().toString());
        }
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        Location value = new Location();
        in.beginObject();
        while(in.hasNext()) {
            if(in.nextName().equalsIgnoreCase("x")) {
                value.setX(in.nextInt());
            }
            if(in.nextName().equalsIgnoreCase("y")) {
                value.setY(in.nextInt());
            }
            if(in.nextName().equalsIgnoreCase("z")) {
                value.setZ(in.nextInt());
            }
            if(in.nextName().equalsIgnoreCase("pitch")) {
                value.setPitch((float) in.nextDouble());
            }
            if(in.nextName().equalsIgnoreCase("yaw")) {
                value.setYaw((float) in.nextDouble());
            }
            if(in.nextName().equalsIgnoreCase("world")) {
                String id = in.nextString();
                for(RegistryKey<World> key : MakeCraft.SERVER.getWorldRegistryKeys()) {
                    World world = MakeCraft.SERVER.getWorld(key);
                    if(world == null) continue;
                    String testID = key.getValue().toString();
                    if(testID.equalsIgnoreCase(id)) {
                        value.setWorld(world);
                        break;
                    }
                }
            }
        }
        in.endObject();
        return value;
    }
}
