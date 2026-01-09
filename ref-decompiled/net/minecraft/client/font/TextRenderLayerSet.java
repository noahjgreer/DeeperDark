package net.minecraft.client.font;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record TextRenderLayerSet(RenderLayer normal, RenderLayer seeThrough, RenderLayer polygonOffset, RenderPipeline guiPipeline) {
   public TextRenderLayerSet(RenderLayer renderLayer, RenderLayer renderLayer2, RenderLayer renderLayer3, RenderPipeline renderPipeline) {
      this.normal = renderLayer;
      this.seeThrough = renderLayer2;
      this.polygonOffset = renderLayer3;
      this.guiPipeline = renderPipeline;
   }

   public static TextRenderLayerSet ofIntensity(Identifier textureId) {
      return new TextRenderLayerSet(RenderLayer.getTextIntensity(textureId), RenderLayer.getTextIntensitySeeThrough(textureId), RenderLayer.getTextIntensityPolygonOffset(textureId), RenderPipelines.RENDERTYPE_TEXT_INTENSITY);
   }

   public static TextRenderLayerSet of(Identifier textureId) {
      return new TextRenderLayerSet(RenderLayer.getText(textureId), RenderLayer.getTextSeeThrough(textureId), RenderLayer.getTextPolygonOffset(textureId), RenderPipelines.RENDERTYPE_TEXT);
   }

   public RenderLayer getRenderLayer(TextRenderer.TextLayerType layerType) {
      RenderLayer var10000;
      switch (layerType) {
         case NORMAL:
            var10000 = this.normal;
            break;
         case SEE_THROUGH:
            var10000 = this.seeThrough;
            break;
         case POLYGON_OFFSET:
            var10000 = this.polygonOffset;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public RenderLayer normal() {
      return this.normal;
   }

   public RenderLayer seeThrough() {
      return this.seeThrough;
   }

   public RenderLayer polygonOffset() {
      return this.polygonOffset;
   }

   public RenderPipeline guiPipeline() {
      return this.guiPipeline;
   }
}
