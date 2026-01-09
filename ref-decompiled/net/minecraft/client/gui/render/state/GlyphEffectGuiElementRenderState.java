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
public record GlyphEffectGuiElementRenderState(Matrix3x2f pose, BakedGlyph whiteGlyph, BakedGlyph.Rectangle effect, @Nullable ScreenRect scissorArea) implements SimpleGuiElementRenderState {
   public GlyphEffectGuiElementRenderState(Matrix3x2f matrix3x2f, BakedGlyph bakedGlyph, BakedGlyph.Rectangle rectangle, @Nullable ScreenRect screenRect) {
      this.pose = matrix3x2f;
      this.whiteGlyph = bakedGlyph;
      this.effect = rectangle;
      this.scissorArea = screenRect;
   }

   public void setupVertices(VertexConsumer vertices, float depth) {
      Matrix4f matrix4f = (new Matrix4f()).mul(this.pose).translate(0.0F, 0.0F, depth);
      this.whiteGlyph.drawRectangle(this.effect, matrix4f, vertices, 15728880, true);
   }

   public RenderPipeline pipeline() {
      return this.whiteGlyph.getPipeline();
   }

   public TextureSetup textureSetup() {
      return TextureSetup.of((GpuTextureView)Objects.requireNonNull(this.whiteGlyph.getTexture()));
   }

   @Nullable
   public ScreenRect bounds() {
      return null;
   }

   public Matrix3x2f pose() {
      return this.pose;
   }

   public BakedGlyph whiteGlyph() {
      return this.whiteGlyph;
   }

   public BakedGlyph.Rectangle effect() {
      return this.effect;
   }

   @Nullable
   public ScreenRect scissorArea() {
      return this.scissorArea;
   }
}
