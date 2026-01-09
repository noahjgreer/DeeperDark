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
public record ColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
   public ColoredQuadGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int x0, int y0, int x1, int y1, int col1, int col2, @Nullable ScreenRect scissorArea) {
      this(pipeline, textureSetup, pose, x0, y0, x1, y1, col1, col2, scissorArea, createBounds(x0, y0, x1, y1, pose, scissorArea));
   }

   public ColoredQuadGuiElementRenderState(RenderPipeline renderPipeline, TextureSetup textureSetup, Matrix3x2f matrix3x2f, int i, int j, int k, int l, int m, int n, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.pipeline = renderPipeline;
      this.textureSetup = textureSetup;
      this.pose = matrix3x2f;
      this.x0 = i;
      this.y0 = j;
      this.x1 = k;
      this.y1 = l;
      this.col1 = m;
      this.col2 = n;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
   }

   public void setupVertices(VertexConsumer vertices, float depth) {
      vertices.vertex(this.pose(), (float)this.x0(), (float)this.y0(), depth).color(this.col1());
      vertices.vertex(this.pose(), (float)this.x0(), (float)this.y1(), depth).color(this.col2());
      vertices.vertex(this.pose(), (float)this.x1(), (float)this.y1(), depth).color(this.col2());
      vertices.vertex(this.pose(), (float)this.x1(), (float)this.y0(), depth).color(this.col1());
   }

   @Nullable
   private static ScreenRect createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
      ScreenRect screenRect = (new ScreenRect(x0, y0, x1 - x0, y1 - y0)).transformEachVertex(pose);
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

   public int x0() {
      return this.x0;
   }

   public int y0() {
      return this.y0;
   }

   public int x1() {
      return this.x1;
   }

   public int y1() {
      return this.y1;
   }

   public int col1() {
      return this.col1;
   }

   public int col2() {
      return this.col2;
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
