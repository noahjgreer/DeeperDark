package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public record GlyphGuiElementRenderState(Matrix3x2f pose, BakedGlyph.DrawnGlyph instance, @Nullable ScreenRect scissorArea) implements SimpleGuiElementRenderState {
   public GlyphGuiElementRenderState(Matrix3x2f matrix3x2f, BakedGlyph.DrawnGlyph drawnGlyph, @Nullable ScreenRect screenRect) {
      this.pose = matrix3x2f;
      this.instance = drawnGlyph;
      this.scissorArea = screenRect;
   }

   public void setupVertices(VertexConsumer vertices, float depth) {
      Matrix4f matrix4f = (new Matrix4f()).mul(this.pose).translate(0.0F, 0.0F, depth);
      this.instance.glyph().draw(this.instance, matrix4f, vertices, 15728880, true);
   }

   public RenderPipeline pipeline() {
      return this.instance.glyph().getPipeline();
   }

   public TextureSetup textureSetup() {
      return TextureSetup.of((GpuTextureView)Objects.requireNonNull(this.instance.glyph().getTexture()));
   }

   @Nullable
   public ScreenRect bounds() {
      return null;
   }

   public Matrix3x2f pose() {
      return this.pose;
   }

   public BakedGlyph.DrawnGlyph instance() {
      return this.instance;
   }

   @Nullable
   public ScreenRect scissorArea() {
      return this.scissorArea;
   }
}
