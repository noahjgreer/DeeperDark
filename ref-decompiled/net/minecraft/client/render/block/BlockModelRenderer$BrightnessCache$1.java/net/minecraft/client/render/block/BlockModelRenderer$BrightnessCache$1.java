/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
class BlockModelRenderer.BrightnessCache.1
extends Long2IntLinkedOpenHashMap {
    BlockModelRenderer.BrightnessCache.1(int i, float f) {
        super(i, f);
    }

    protected void rehash(int newN) {
    }
}
