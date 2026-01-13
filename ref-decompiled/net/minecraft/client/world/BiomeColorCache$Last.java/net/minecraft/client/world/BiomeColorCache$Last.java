/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class BiomeColorCache.Last {
    public int x = Integer.MIN_VALUE;
    public int z = Integer.MIN_VALUE;
     @Nullable BiomeColorCache.Colors colors;

    private BiomeColorCache.Last() {
    }
}
