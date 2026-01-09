package net.minecraft.client.texture;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record TextureSetup(@Nullable GpuTextureView texure0, @Nullable GpuTextureView texure1, @Nullable GpuTextureView texure2) {
   private static final TextureSetup EMPTY = new TextureSetup((GpuTextureView)null, (GpuTextureView)null, (GpuTextureView)null);
   private static int field_60313;

   public TextureSetup(@Nullable GpuTextureView gpuTextureView, @Nullable GpuTextureView gpuTextureView2, @Nullable GpuTextureView gpuTextureView3) {
      this.texure0 = gpuTextureView;
      this.texure1 = gpuTextureView2;
      this.texure2 = gpuTextureView3;
   }

   public static TextureSetup withoutGlTexture(GpuTextureView texture) {
      return new TextureSetup(texture, (GpuTextureView)null, (GpuTextureView)null);
   }

   public static TextureSetup of(GpuTextureView texture) {
      return new TextureSetup(texture, (GpuTextureView)null, MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().getGlTextureView());
   }

   public static TextureSetup of(GpuTextureView texture0, GpuTextureView texture1) {
      return new TextureSetup(texture0, texture1, (GpuTextureView)null);
   }

   public static TextureSetup empty() {
      return EMPTY;
   }

   public int getSortKey() {
      return this.hashCode();
   }

   public static void method_71298() {
      field_60313 = Math.round(100000.0F * (float)Math.random());
   }

   @Nullable
   public GpuTextureView texure0() {
      return this.texure0;
   }

   @Nullable
   public GpuTextureView texure1() {
      return this.texure1;
   }

   @Nullable
   public GpuTextureView texure2() {
      return this.texure2;
   }
}
