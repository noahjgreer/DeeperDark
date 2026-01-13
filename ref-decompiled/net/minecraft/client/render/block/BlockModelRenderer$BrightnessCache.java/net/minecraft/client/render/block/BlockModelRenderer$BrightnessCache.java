/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@Environment(value=EnvType.CLIENT)
static class BlockModelRenderer.BrightnessCache {
    private boolean enabled;
    private final Long2IntLinkedOpenHashMap intCache = Util.make(() -> {
        Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25f){

            protected void rehash(int newN) {
            }
        };
        long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
        return long2IntLinkedOpenHashMap;
    });
    private final Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
        Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25f){

            protected void rehash(int newN) {
            }
        };
        long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
        return long2FloatLinkedOpenHashMap;
    });
    private final WorldRenderer.BrightnessGetter brightnessCache = (world, pos) -> {
        long l = pos.asLong();
        int i = this.intCache.get(l);
        if (i != Integer.MAX_VALUE) {
            return i;
        }
        int j = WorldRenderer.BrightnessGetter.DEFAULT.packedBrightness(world, pos);
        if (this.intCache.size() == 100) {
            this.intCache.removeFirstInt();
        }
        this.intCache.put(l, j);
        return j;
    };

    private BlockModelRenderer.BrightnessCache() {
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
        this.intCache.clear();
        this.floatCache.clear();
    }

    public int getInt(BlockState state, BlockRenderView world, BlockPos pos) {
        return WorldRenderer.getLightmapCoordinates(this.enabled ? this.brightnessCache : WorldRenderer.BrightnessGetter.DEFAULT, world, state, pos);
    }

    public float getFloat(BlockState state, BlockRenderView blockView, BlockPos pos) {
        float f;
        long l = pos.asLong();
        if (this.enabled && !Float.isNaN(f = this.floatCache.get(l))) {
            return f;
        }
        f = state.getAmbientOcclusionLightLevel(blockView, pos);
        if (this.enabled) {
            if (this.floatCache.size() == 100) {
                this.floatCache.removeFirstFloat();
            }
            this.floatCache.put(l, f);
        }
        return f;
    }
}
