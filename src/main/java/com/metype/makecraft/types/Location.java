package com.metype.makecraft.types;

import com.metype.makecraft.MakeCraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Location {
    private float pitch;
    private float yaw;
    private BlockPos blockPos;
    private World world;

    public Location() {
        this.blockPos = new BlockPos(0,0,0);
        this.world = null;//MakeCraft.SERVER.getSpawnWorld();
        this.pitch = 0;
        this.yaw = 0;
    }

    public Location(int x, int y, int z, float pitch, float yaw, World world) {
        this.blockPos = new BlockPos(x, y, z);
        this.world = world;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Location(BlockPos position, float pitch, float yaw, World world) {
        this.blockPos = position;
        this.world = world;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public int getX() {
        return this.blockPos.getX();
    }

    public int getY() {
        return this.blockPos.getY();
    }

    public int getZ() {
        return this.blockPos.getZ();
    }

    public BlockPos getBlockPos() { return this.blockPos; }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public World getWorld() {
        return this.world;
    }

    public void setX(int x) {
        this.blockPos = new BlockPos(x, blockPos.getY(), blockPos.getZ());
    }

    public void setY(int y) {
        this.blockPos = new BlockPos(blockPos.getX(), y, blockPos.getZ());
    }

    public void setZ(int z) {
        this.blockPos = new BlockPos(blockPos.getX(), blockPos.getY(), z);
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
