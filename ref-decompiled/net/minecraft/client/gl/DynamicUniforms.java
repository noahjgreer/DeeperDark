package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(EnvType.CLIENT)
public class DynamicUniforms implements AutoCloseable {
   public static final int SIZE = (new Std140SizeCalculator()).putMat4f().putVec4().putVec3().putMat4f().putFloat().get();
   private static final int DEFAULT_CAPACITY = 2;
   private final DynamicUniformStorage storage;

   public DynamicUniforms() {
      this.storage = new DynamicUniformStorage("Dynamic Transforms UBO", SIZE, 2);
   }

   public void clear() {
      this.storage.clear();
   }

   public void close() {
      this.storage.close();
   }

   public GpuBufferSlice write(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix, float lineWidth) {
      return this.storage.write(new UniformValue(new Matrix4f(modelView), new Vector4f(colorModulator), new Vector3f(modelOffset), new Matrix4f(textureMatrix), lineWidth));
   }

   public GpuBufferSlice[] writeAll(UniformValue... values) {
      return this.storage.writeAll(values);
   }

   @Environment(EnvType.CLIENT)
   public static record UniformValue(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix, float lineWidth) implements DynamicUniformStorage.Uploadable {
      public UniformValue(Matrix4fc matrix4fc, Vector4fc vector4fc, Vector3fc vector3fc, Matrix4fc matrix4fc2, float f) {
         this.modelView = matrix4fc;
         this.colorModulator = vector4fc;
         this.modelOffset = vector3fc;
         this.textureMatrix = matrix4fc2;
         this.lineWidth = f;
      }

      public void write(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix).putFloat(this.lineWidth);
      }

      public Matrix4fc modelView() {
         return this.modelView;
      }

      public Vector4fc colorModulator() {
         return this.colorModulator;
      }

      public Vector3fc modelOffset() {
         return this.modelOffset;
      }

      public Matrix4fc textureMatrix() {
         return this.textureMatrix;
      }

      public float lineWidth() {
         return this.lineWidth;
      }
   }
}
