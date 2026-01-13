/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.AddressMode
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gl.SamplerCache
 */
package net.minecraft.client.gl;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import java.util.OptionalDouble;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SamplerCache {
    private final GpuSampler[] samplers = new GpuSampler[32];

    public void init() {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if (AddressMode.values().length != 2 || FilterMode.values().length != 2) {
            throw new IllegalStateException("AddressMode and FilterMode enum sizes must be 2 - if you expanded them, please update SamplerCache");
        }
        for (AddressMode addressMode : AddressMode.values()) {
            for (AddressMode addressMode2 : AddressMode.values()) {
                for (FilterMode filterMode : FilterMode.values()) {
                    for (FilterMode filterMode2 : FilterMode.values()) {
                        for (boolean bl : new boolean[]{true, false}) {
                            this.samplers[SamplerCache.toIndex((AddressMode)addressMode, (AddressMode)addressMode2, (FilterMode)filterMode, (FilterMode)filterMode2, (boolean)bl)] = gpuDevice.createSampler(addressMode, addressMode2, filterMode, filterMode2, 1, bl ? OptionalDouble.empty() : OptionalDouble.of(0.0));
                        }
                    }
                }
            }
        }
    }

    public GpuSampler get(AddressMode addressModeU, AddressMode addressModeV, FilterMode minFilterMode, FilterMode magFilterMode, boolean defaultLineOfDetail) {
        return this.samplers[SamplerCache.toIndex((AddressMode)addressModeU, (AddressMode)addressModeV, (FilterMode)minFilterMode, (FilterMode)magFilterMode, (boolean)defaultLineOfDetail)];
    }

    public GpuSampler get(FilterMode filterMode) {
        return this.get(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, filterMode, filterMode, false);
    }

    public GpuSampler get(FilterMode filterMode, boolean defaultLineOfDetail) {
        return this.get(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, filterMode, filterMode, defaultLineOfDetail);
    }

    public GpuSampler getRepeated(FilterMode filterMode) {
        return this.get(AddressMode.REPEAT, AddressMode.REPEAT, filterMode, filterMode, false);
    }

    public GpuSampler getRepeated(FilterMode filterMode, boolean defaultLineOfDetail) {
        return this.get(AddressMode.REPEAT, AddressMode.REPEAT, filterMode, filterMode, defaultLineOfDetail);
    }

    public void close() {
        for (GpuSampler gpuSampler : this.samplers) {
            gpuSampler.close();
        }
    }

    @VisibleForTesting
    static int toIndex(AddressMode addressModeU, AddressMode addressModeV, FilterMode minFilterMode, FilterMode magFilterMode, boolean bl) {
        int i = 0;
        i |= addressModeU.ordinal() & 1;
        i |= (addressModeV.ordinal() & 1) << 1;
        i |= (minFilterMode.ordinal() & 1) << 2;
        i |= (magFilterMode.ordinal() & 1) << 3;
        if (bl) {
            i |= 0x10;
        }
        return i;
    }
}

