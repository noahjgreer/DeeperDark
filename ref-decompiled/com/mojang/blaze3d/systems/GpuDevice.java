/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$Usage
 *  com.mojang.blaze3d.pipeline.CompiledRenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.textures.AddressMode
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTexture$Usage
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gl.ShaderSourceGetter
 *  net.minecraft.util.annotation.DeobfuscateClass
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.ShaderSourceGetter;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public interface GpuDevice {
    public CommandEncoder createCommandEncoder();

    public GpuSampler createSampler(AddressMode var1, AddressMode var2, FilterMode var3, FilterMode var4, int var5, OptionalDouble var6);

    public GpuTexture createTexture(@Nullable Supplier<String> var1, @GpuTexture.Usage int var2, TextureFormat var3, int var4, int var5, int var6, int var7);

    public GpuTexture createTexture(@Nullable String var1, @GpuTexture.Usage int var2, TextureFormat var3, int var4, int var5, int var6, int var7);

    public GpuTextureView createTextureView(GpuTexture var1);

    public GpuTextureView createTextureView(GpuTexture var1, int var2, int var3);

    public GpuBuffer createBuffer(@Nullable Supplier<String> var1, @GpuBuffer.Usage int var2, long var3);

    public GpuBuffer createBuffer(@Nullable Supplier<String> var1, @GpuBuffer.Usage int var2, ByteBuffer var3);

    public String getImplementationInformation();

    public List<String> getLastDebugMessages();

    public boolean isDebuggingEnabled();

    public String getVendor();

    public String getBackendName();

    public String getVersion();

    public String getRenderer();

    public int getMaxTextureSize();

    public int getUniformOffsetAlignment();

    default public CompiledRenderPipeline precompilePipeline(RenderPipeline pipeline) {
        return this.precompilePipeline(pipeline, null);
    }

    public CompiledRenderPipeline precompilePipeline(RenderPipeline var1, @Nullable ShaderSourceGetter var2);

    public void clearPipelineCache();

    public List<String> getEnabledExtensions();

    public int getMaxSupportedAnisotropy();

    public void close();
}

