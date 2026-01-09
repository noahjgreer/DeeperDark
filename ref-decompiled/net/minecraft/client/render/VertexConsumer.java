package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

@Environment(EnvType.CLIENT)
public interface VertexConsumer {
   VertexConsumer vertex(float x, float y, float z);

   VertexConsumer color(int red, int green, int blue, int alpha);

   VertexConsumer texture(float u, float v);

   VertexConsumer overlay(int u, int v);

   VertexConsumer light(int u, int v);

   VertexConsumer normal(float x, float y, float z);

   default void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
      this.vertex(x, y, z);
      this.color(color);
      this.texture(u, v);
      this.overlay(overlay);
      this.light(light);
      this.normal(normalX, normalY, normalZ);
   }

   default VertexConsumer color(float red, float green, float blue, float alpha) {
      return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   default VertexConsumer color(int argb) {
      return this.color(ColorHelper.getRed(argb), ColorHelper.getGreen(argb), ColorHelper.getBlue(argb), ColorHelper.getAlpha(argb));
   }

   default VertexConsumer colorRgb(int rgb) {
      return this.color(ColorHelper.withAlpha(rgb, -1));
   }

   default VertexConsumer light(int uv) {
      return this.light(uv & '\uffff', uv >> 16 & '\uffff');
   }

   default VertexConsumer overlay(int uv) {
      return this.overlay(uv & '\uffff', uv >> 16 & '\uffff');
   }

   default void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float alpha, int light, int overlay) {
      this.quad(matrixEntry, quad, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, red, green, blue, alpha, new int[]{light, light, light, light}, overlay, false);
   }

   default void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay, boolean colorize) {
      int[] is = quad.vertexData();
      Vector3fc vector3fc = quad.face().getFloatVector();
      Matrix4f matrix4f = matrixEntry.getPositionMatrix();
      Vector3f vector3f = matrixEntry.transformNormal(vector3fc, new Vector3f());
      int i = true;
      int j = is.length / 8;
      int k = (int)(alpha * 255.0F);
      int l = quad.lightEmission();
      MemoryStack memoryStack = MemoryStack.stackPush();

      try {
         ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
         IntBuffer intBuffer = byteBuffer.asIntBuffer();

         for(int m = 0; m < j; ++m) {
            intBuffer.clear();
            intBuffer.put(is, m * 8, 8);
            float f = byteBuffer.getFloat(0);
            float g = byteBuffer.getFloat(4);
            float h = byteBuffer.getFloat(8);
            float q;
            float r;
            float s;
            float p;
            if (colorize) {
               float n = (float)(byteBuffer.get(12) & 255);
               float o = (float)(byteBuffer.get(13) & 255);
               p = (float)(byteBuffer.get(14) & 255);
               q = n * brightnesses[m] * red;
               r = o * brightnesses[m] * green;
               s = p * brightnesses[m] * blue;
            } else {
               q = brightnesses[m] * red * 255.0F;
               r = brightnesses[m] * green * 255.0F;
               s = brightnesses[m] * blue * 255.0F;
            }

            int t = ColorHelper.getArgb(k, (int)q, (int)r, (int)s);
            int u = LightmapTextureManager.applyEmission(lights[m], l);
            p = byteBuffer.getFloat(16);
            float v = byteBuffer.getFloat(20);
            Vector3f vector3f2 = matrix4f.transformPosition(f, g, h, new Vector3f());
            this.vertex(vector3f2.x(), vector3f2.y(), vector3f2.z(), t, p, v, overlay, u, vector3f.x(), vector3f.y(), vector3f.z());
         }
      } catch (Throwable var35) {
         if (memoryStack != null) {
            try {
               memoryStack.close();
            } catch (Throwable var34) {
               var35.addSuppressed(var34);
            }
         }

         throw var35;
      }

      if (memoryStack != null) {
         memoryStack.close();
      }

   }

   default VertexConsumer vertex(Vector3f vec) {
      return this.vertex(vec.x(), vec.y(), vec.z());
   }

   default VertexConsumer vertex(MatrixStack.Entry matrix, Vector3f vec) {
      return this.vertex(matrix, vec.x(), vec.y(), vec.z());
   }

   default VertexConsumer vertex(MatrixStack.Entry matrix, float x, float y, float z) {
      return this.vertex(matrix.getPositionMatrix(), x, y, z);
   }

   default VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
      Vector3f vector3f = matrix.transformPosition(x, y, z, new Vector3f());
      return this.vertex(vector3f.x(), vector3f.y(), vector3f.z());
   }

   default VertexConsumer vertex(Matrix3x2f matrix, float x, float y, float z) {
      Vector2f vector2f = matrix.transformPosition(x, y, new Vector2f());
      return this.vertex(vector2f.x(), vector2f.y(), z);
   }

   default VertexConsumer normal(MatrixStack.Entry matrix, float x, float y, float z) {
      Vector3f vector3f = matrix.transformNormal(x, y, z, new Vector3f());
      return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
   }

   default VertexConsumer normal(MatrixStack.Entry matrix, Vector3f vec) {
      return this.normal(matrix, vec.x(), vec.y(), vec.z());
   }
}
