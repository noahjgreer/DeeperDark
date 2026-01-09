package net.minecraft.client.gui.render.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public record TexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
   public TexturedQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, @Nullable ScreenRect scissorArea) {
      this(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
   }

   public TexturedQuadGuiElementRenderState(RenderPipeline renderPipeline, TextureSetup textureSetup, Matrix3x2f matrix3x2f, int i, int j, int k, int l, float f, float g, float h, float m, int n, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.pipeline = renderPipeline;
      this.textureSetup = textureSetup;
      this.pose = matrix3x2f;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
      this.u1 = f;
      this.u2 = g;
      this.v1 = h;
      this.v2 = m;
      this.color = n;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
   }

   public void setupVertices(VertexConsumer vertices, float depth) {
      vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1(), depth).texture(this.u1(), this.v1()).color(this.color());
      vertices.vertex(this.pose(), (float)this.x1(), (float)this.y2(), depth).texture(this.u1(), this.v2()).color(this.color());
      vertices.vertex(this.pose(), (float)this.x2(), (float)this.y2(), depth).texture(this.u2(), this.v2()).color(this.color());
      vertices.vertex(this.pose(), (float)this.x2(), (float)this.y1(), depth).texture(this.u2(), this.v1()).color(this.color());
   }

   @Nullable
   private static ScreenRect createBounds(int x1, int y1, int x2, int y2, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
      ScreenRect screenRect = (new ScreenRect(x1, y1, x2 - x1, y2 - y1)).transformEachVertex(pose);
      return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
   }

   public RenderPipeline pipeline() {
      return this.pipeline;
   }

   public TextureSetup textureSetup() {
      return this.textureSetup;
   }

   public Matrix3x2f pose() {
      return this.pose;
   }

   public int x1() {
      return this.x1;
   }

   public int y1() {
      return this.y1;
   }

   public int x2() {
      return this.x2;
   }

   public int y2() {
      return this.y2;
   }

   public float u1() {
      return this.u1;
   }

   public float u2() {
      return this.u2;
   }

   public float v1() {
      return this.v1;
   }

   public float v2() {
      return this.v2;
   }

   public int color() {
      return this.color;
   }

   @Nullable
   public ScreenRect scissorArea() {
      return this.scissorArea;
   }

   @Nullable
   public ScreenRect bounds() {
      return this.bounds;
   }
}
