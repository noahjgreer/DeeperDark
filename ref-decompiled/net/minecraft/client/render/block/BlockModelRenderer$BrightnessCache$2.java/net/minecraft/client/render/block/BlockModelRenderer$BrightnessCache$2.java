/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
class BlockModelRenderer.BrightnessCache.2
extends Long2FloatLinkedOpenHashMap {
    BlockModelRenderer.BrightnessCache.2(int i, float f) {
        super(i, f);
    }

    protected void rehash(int newN) {
    }
}
