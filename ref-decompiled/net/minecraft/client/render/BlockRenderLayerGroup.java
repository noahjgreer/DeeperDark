package net.minecraft.client.render;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

@Environment(EnvType.CLIENT)
public enum BlockRenderLayerGroup {
   OPAQUE(new BlockRenderLayer[]{BlockRenderLayer.SOLID, BlockRenderLayer.CUTOUT_MIPPED, BlockRenderLayer.CUTOUT}),
   TRANSLUCENT(new BlockRenderLayer[]{BlockRenderLayer.TRANSLUCENT}),
   TRIPWIRE(new BlockRenderLayer[]{BlockRenderLayer.TRIPWIRE});

   private final String name;
   private final BlockRenderLayer[] layers;

   private BlockRenderLayerGroup(final BlockRenderLayer... layers) {
      this.layers = layers;
      this.name = this.toString().toLowerCase(Locale.ROOT);
   }

   public String getName() {
      return this.name;
   }

   public BlockRenderLayer[] getLayers() {
      return this.layers;
   }

   public Framebuffer getFramebuffer() {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      Framebuffer var10000;
      switch (this.ordinal()) {
         case 1:
            var10000 = minecraftClient.worldRenderer.getTranslucentFramebuffer();
            break;
         case 2:
            var10000 = minecraftClient.worldRenderer.getWeatherFramebuffer();
            break;
         default:
            var10000 = minecraftClient.getFramebuffer();
      }

      Framebuffer framebuffer = var10000;
      return framebuffer != null ? framebuffer : minecraftClient.getFramebuffer();
   }

   // $FF: synthetic method
   private static BlockRenderLayerGroup[] method_72169() {
      return new BlockRenderLayerGroup[]{OPAQUE, TRANSLUCENT, TRIPWIRE};
   }
}
