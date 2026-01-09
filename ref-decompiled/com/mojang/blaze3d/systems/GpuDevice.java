package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public interface GpuDevice {
   CommandEncoder createCommandEncoder();

   GpuTexture createTexture(@Nullable Supplier labelGetter, int usage, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels);

   GpuTexture createTexture(@Nullable String label, int usage, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels);

   GpuTextureView createTextureView(GpuTexture texture);

   GpuTextureView createTextureView(GpuTexture texture, int baseMipLevel, int mipLevels);

   GpuBuffer createBuffer(@Nullable Supplier labelGetter, int usage, int size);

   GpuBuffer createBuffer(@Nullable Supplier labelGetter, int usage, ByteBuffer data);

   String getImplementationInformation();

   List getLastDebugMessages();

   boolean isDebuggingEnabled();

   String getVendor();

   String getBackendName();

   String getVersion();

   String getRenderer();

   int getMaxTextureSize();

   int getUniformOffsetAlignment();

   default CompiledRenderPipeline precompilePipeline(RenderPipeline pipeline) {
      return this.precompilePipeline(pipeline, (BiFunction)null);
   }

   CompiledRenderPipeline precompilePipeline(RenderPipeline pipeline, @Nullable BiFunction sourceRetriever);

   void clearPipelineCache();

   List getEnabledExtensions();

   void close();
}
