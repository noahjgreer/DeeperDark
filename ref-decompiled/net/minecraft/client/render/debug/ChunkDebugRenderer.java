package net.minecraft.client.render.debug;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class ChunkDebugRenderer implements DebugRenderer.Renderer {
   public static final Direction[] DIRECTIONS = Direction.values();
   private final MinecraftClient client;

   public ChunkDebugRenderer(MinecraftClient client) {
      this.client = client;
   }

   public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
      WorldRenderer worldRenderer = this.client.worldRenderer;
      if (this.client.debugChunkInfo || this.client.debugChunkOcclusion) {
         ChunkRenderingDataPreparer chunkRenderingDataPreparer = worldRenderer.getChunkRenderingDataPreparer();
         ObjectListIterator var11 = worldRenderer.getBuiltChunks().iterator();

         label75:
         while(true) {
            ChunkBuilder.BuiltChunk builtChunk;
            ChunkRenderingDataPreparer.ChunkInfo chunkInfo;
            do {
               if (!var11.hasNext()) {
                  break label75;
               }

               builtChunk = (ChunkBuilder.BuiltChunk)var11.next();
               chunkInfo = chunkRenderingDataPreparer.getInfo(builtChunk);
            } while(chunkInfo == null);

            BlockPos blockPos = builtChunk.getOrigin();
            matrices.push();
            matrices.translate((double)blockPos.getX() - cameraX, (double)blockPos.getY() - cameraY, (double)blockPos.getZ() - cameraZ);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            VertexConsumer vertexConsumer;
            int i;
            int k;
            int l;
            if (this.client.debugChunkInfo) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
               i = chunkInfo.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)chunkInfo.propagationLevel / 50.0F, 0.9F, 0.9F);
               int j = i >> 16 & 255;
               k = i >> 8 & 255;
               l = i & 255;

               for(int m = 0; m < DIRECTIONS.length; ++m) {
                  if (chunkInfo.hasDirection(m)) {
                     Direction direction = DIRECTIONS[m];
                     vertexConsumer.vertex(matrix4f, 8.0F, 8.0F, 8.0F).color(j, k, l, 255).normal((float)direction.getOffsetX(), (float)direction.getOffsetY(), (float)direction.getOffsetZ());
                     vertexConsumer.vertex(matrix4f, (float)(8 - 16 * direction.getOffsetX()), (float)(8 - 16 * direction.getOffsetY()), (float)(8 - 16 * direction.getOffsetZ())).color(j, k, l, 255).normal((float)direction.getOffsetX(), (float)direction.getOffsetY(), (float)direction.getOffsetZ());
                  }
               }
            }

            if (this.client.debugChunkOcclusion && builtChunk.getCurrentRenderData().hasData()) {
               vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
               i = 0;
               Direction[] var32 = DIRECTIONS;
               k = var32.length;
               l = 0;

               while(true) {
                  if (l >= k) {
                     if (i > 0) {
                        VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
                        float f = 0.5F;
                        float g = 0.2F;
                        vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                        vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
                     }
                     break;
                  }

                  Direction direction2 = var32[l];
                  Direction[] var37 = DIRECTIONS;
                  int var23 = var37.length;

                  for(int var24 = 0; var24 < var23; ++var24) {
                     Direction direction3 = var37[var24];
                     boolean bl = builtChunk.getCurrentRenderData().isVisibleThrough(direction2, direction3);
                     if (!bl) {
                        ++i;
                        vertexConsumer.vertex(matrix4f, (float)(8 + 8 * direction2.getOffsetX()), (float)(8 + 8 * direction2.getOffsetY()), (float)(8 + 8 * direction2.getOffsetZ())).color(255, 0, 0, 255).normal((float)direction2.getOffsetX(), (float)direction2.getOffsetY(), (float)direction2.getOffsetZ());
                        vertexConsumer.vertex(matrix4f, (float)(8 + 8 * direction3.getOffsetX()), (float)(8 + 8 * direction3.getOffsetY()), (float)(8 + 8 * direction3.getOffsetZ())).color(255, 0, 0, 255).normal((float)direction3.getOffsetX(), (float)direction3.getOffsetY(), (float)direction3.getOffsetZ());
                     }
                  }

                  ++l;
               }
            }

            matrices.pop();
         }
      }

      Frustum frustum = worldRenderer.getCapturedFrustum();
      if (frustum != null) {
         matrices.push();
         matrices.translate((float)(frustum.getX() - cameraX), (float)(frustum.getY() - cameraY), (float)(frustum.getZ() - cameraZ));
         Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
         Vector4f[] vector4fs = frustum.getBoundaryPoints();
         VertexConsumer vertexConsumer3 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
         this.addFace(vertexConsumer3, matrix4f2, vector4fs, 0, 1, 2, 3, 0, 1, 1);
         this.addFace(vertexConsumer3, matrix4f2, vector4fs, 4, 5, 6, 7, 1, 0, 0);
         this.addFace(vertexConsumer3, matrix4f2, vector4fs, 0, 1, 5, 4, 1, 1, 0);
         this.addFace(vertexConsumer3, matrix4f2, vector4fs, 2, 3, 7, 6, 0, 0, 1);
         this.addFace(vertexConsumer3, matrix4f2, vector4fs, 0, 4, 7, 3, 0, 1, 0);
         this.addFace(vertexConsumer3, matrix4f2, vector4fs, 1, 5, 6, 2, 1, 0, 1);
         VertexConsumer vertexConsumer4 = vertexConsumers.getBuffer(RenderLayer.getLines());
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[0]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[1]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[1]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[2]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[2]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[3]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[3]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[0]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[4]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[5]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[5]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[6]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[6]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[7]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[7]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[4]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[0]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[4]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[1]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[5]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[2]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[6]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[3]);
         this.addEndpoint(vertexConsumer4, matrix4f2, vector4fs[7]);
         matrices.pop();
      }

   }

   private void addEndpoint(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vector4f vertex) {
      vertexConsumer.vertex(positionMatrix, vertex.x(), vertex.y(), vertex.z()).color(-16777216).normal(0.0F, 0.0F, -1.0F);
   }

   private void addFace(VertexConsumer vertexConsumer, Matrix4f positionMatrix, Vector4f[] vertices, int i1, int i2, int i3, int i4, int r, int g, int b) {
      float f = 0.25F;
      vertexConsumer.vertex(positionMatrix, vertices[i1].x(), vertices[i1].y(), vertices[i1].z()).color((float)r, (float)g, (float)b, 0.25F);
      vertexConsumer.vertex(positionMatrix, vertices[i2].x(), vertices[i2].y(), vertices[i2].z()).color((float)r, (float)g, (float)b, 0.25F);
      vertexConsumer.vertex(positionMatrix, vertices[i3].x(), vertices[i3].y(), vertices[i3].z()).color((float)r, (float)g, (float)b, 0.25F);
      vertexConsumer.vertex(positionMatrix, vertices[i4].x(), vertices[i4].y(), vertices[i4].z()).color((float)r, (float)g, (float)b, 0.25F);
   }
}
