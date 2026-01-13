/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.Heightmap;

@Environment(value=EnvType.CLIENT)
static class HeightmapDebugRenderer.1 {
    static final /* synthetic */ int[] field_23778;

    static {
        field_23778 = new int[Heightmap.Type.values().length];
        try {
            HeightmapDebugRenderer.1.field_23778[Heightmap.Type.WORLD_SURFACE_WG.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeightmapDebugRenderer.1.field_23778[Heightmap.Type.OCEAN_FLOOR_WG.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeightmapDebugRenderer.1.field_23778[Heightmap.Type.WORLD_SURFACE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeightmapDebugRenderer.1.field_23778[Heightmap.Type.OCEAN_FLOOR.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeightmapDebugRenderer.1.field_23778[Heightmap.Type.MOTION_BLOCKING.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            HeightmapDebugRenderer.1.field_23778[Heightmap.Type.MOTION_BLOCKING_NO_LEAVES.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
