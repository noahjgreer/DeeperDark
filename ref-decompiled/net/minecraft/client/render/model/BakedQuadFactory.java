package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixUtil;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public class BakedQuadFactory {
   public static final int field_32796 = 8;
   public static final int field_32797 = 4;
   private static final int field_32799 = 3;
   public static final int field_32798 = 4;
   private static final Vector3fc field_60149 = new Vector3f(1.0F, 1.0F, 1.0F);
   private static final Vector3fc field_60150 = new Vector3f(0.5F, 0.5F, 0.5F);

   @VisibleForTesting
   static ModelElementFace.UV setDefaultUV(Vector3fc from, Vector3fc to, Direction facing) {
      ModelElementFace.UV var10000;
      switch (facing) {
         case DOWN:
            var10000 = new ModelElementFace.UV(from.x(), 16.0F - to.z(), to.x(), 16.0F - from.z());
            break;
         case UP:
            var10000 = new ModelElementFace.UV(from.x(), from.z(), to.x(), to.z());
            break;
         case NORTH:
            var10000 = new ModelElementFace.UV(16.0F - to.x(), 16.0F - to.y(), 16.0F - from.x(), 16.0F - from.y());
            break;
         case SOUTH:
            var10000 = new ModelElementFace.UV(from.x(), 16.0F - to.y(), to.x(), 16.0F - from.y());
            break;
         case WEST:
            var10000 = new ModelElementFace.UV(from.z(), 16.0F - to.y(), to.z(), 16.0F - from.y());
            break;
         case EAST:
            var10000 = new ModelElementFace.UV(16.0F - to.z(), 16.0F - to.y(), 16.0F - from.z(), 16.0F - from.y());
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static BakedQuad bake(Vector3fc from, Vector3fc to, ModelElementFace facing, Sprite sprite, Direction direction, ModelBakeSettings settings, @Nullable net.minecraft.client.render.model.json.ModelRotation rotation, boolean shade, int lightEmission) {
      ModelElementFace.UV uV = facing.uvs();
      if (uV == null) {
         uV = setDefaultUV(from, to, direction);
      }

      uV = compactUV(sprite, uV);
      Matrix4fc matrix4fc = settings.reverse(direction);
      int[] is = packVertexData(uV, facing.rotation(), matrix4fc, sprite, direction, getPositionMatrix(from, to), settings.getRotation(), rotation);
      Direction direction2 = decodeDirection(is);
      if (rotation == null) {
         encodeDirection(is, direction2);
      }

      return new BakedQuad(is, facing.tintIndex(), direction2, sprite, shade, lightEmission);
   }

   private static ModelElementFace.UV compactUV(Sprite sprite, ModelElementFace.UV uv) {
      float f = uv.minU();
      float g = uv.minV();
      float h = uv.maxU();
      float i = uv.maxV();
      float j = sprite.getUvScaleDelta();
      float k = (f + f + h + h) / 4.0F;
      float l = (g + g + i + i) / 4.0F;
      return new ModelElementFace.UV(MathHelper.lerp(j, f, k), MathHelper.lerp(j, g, l), MathHelper.lerp(j, h, k), MathHelper.lerp(j, i, l));
   }

   private static int[] packVertexData(ModelElementFace.UV texture, AxisRotation rotation, Matrix4fc matrix4fc, Sprite sprite, Direction facing, float[] fs, AffineTransformation transform, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation) {
      CubeFace cubeFace = CubeFace.getFace(facing);
      int[] is = new int[32];

      for(int i = 0; i < 4; ++i) {
         packVertexData(is, i, cubeFace, texture, rotation, matrix4fc, fs, sprite, transform, modelRotation);
      }

      return is;
   }

   private static float[] getPositionMatrix(Vector3fc from, Vector3fc to) {
      float[] fs = new float[Direction.values().length];
      fs[CubeFace.DirectionIds.WEST] = from.x() / 16.0F;
      fs[CubeFace.DirectionIds.DOWN] = from.y() / 16.0F;
      fs[CubeFace.DirectionIds.NORTH] = from.z() / 16.0F;
      fs[CubeFace.DirectionIds.EAST] = to.x() / 16.0F;
      fs[CubeFace.DirectionIds.UP] = to.y() / 16.0F;
      fs[CubeFace.DirectionIds.SOUTH] = to.z() / 16.0F;
      return fs;
   }

   private static void packVertexData(int[] vertices, int cornerIndex, CubeFace cubeFace, ModelElementFace.UV texture, AxisRotation axisRotation, Matrix4fc matrix4fc, float[] fs, Sprite sprite, AffineTransformation affineTransformation, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation) {
      CubeFace.Corner corner = cubeFace.getCorner(cornerIndex);
      Vector3f vector3f = new Vector3f(fs[corner.xSide], fs[corner.ySide], fs[corner.zSide]);
      rotateVertex(vector3f, modelRotation);
      transformVertex(vector3f, affineTransformation);
      float f = ModelElementFace.getUValue(texture, axisRotation, cornerIndex);
      float g = ModelElementFace.getVValue(texture, axisRotation, cornerIndex);
      float i;
      float h;
      if (MatrixUtil.isIdentity(matrix4fc)) {
         h = f;
         i = g;
      } else {
         Vector3f vector3f2 = matrix4fc.transformPosition(new Vector3f(setCenterBack(f), setCenterBack(g), 0.0F));
         h = setCenterForward(vector3f2.x);
         i = setCenterForward(vector3f2.y);
      }

      packVertexData(vertices, cornerIndex, vector3f, sprite, h, i);
   }

   private static float setCenterBack(float f) {
      return f - 0.5F;
   }

   private static float setCenterForward(float f) {
      return f + 0.5F;
   }

   private static void packVertexData(int[] vertices, int cornerIndex, Vector3f pos, Sprite sprite, float f, float g) {
      int i = cornerIndex * 8;
      vertices[i] = Float.floatToRawIntBits(pos.x());
      vertices[i + 1] = Float.floatToRawIntBits(pos.y());
      vertices[i + 2] = Float.floatToRawIntBits(pos.z());
      vertices[i + 3] = -1;
      vertices[i + 4] = Float.floatToRawIntBits(sprite.getFrameU(f));
      vertices[i + 4 + 1] = Float.floatToRawIntBits(sprite.getFrameV(g));
   }

   private static void rotateVertex(Vector3f vertex, @Nullable net.minecraft.client.render.model.json.ModelRotation rotation) {
      if (rotation != null) {
         Vector3fc vector3fc = rotation.axis().getPositiveDirection().getFloatVector();
         Matrix4fc matrix4fc = (new Matrix4f()).rotation(rotation.angle() * 0.017453292F, vector3fc);
         Vector3fc vector3fc2 = rotation.rescale() ? method_71135(rotation) : field_60149;
         transformVertex(vertex, rotation.origin(), matrix4fc, vector3fc2);
      }
   }

   private static Vector3fc method_71135(net.minecraft.client.render.model.json.ModelRotation modelRotation) {
      if (modelRotation.angle() == 0.0F) {
         return field_60149;
      } else {
         float f = Math.abs(modelRotation.angle());
         float g = 1.0F / MathHelper.cos(f * 0.017453292F);
         Vector3f var10000;
         switch (modelRotation.axis()) {
            case X:
               var10000 = new Vector3f(1.0F, g, g);
               break;
            case Y:
               var10000 = new Vector3f(g, 1.0F, g);
               break;
            case Z:
               var10000 = new Vector3f(g, g, 1.0F);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   private static void transformVertex(Vector3f vertex, AffineTransformation transformation) {
      if (transformation != AffineTransformation.identity()) {
         transformVertex(vertex, field_60150, transformation.getMatrix(), field_60149);
      }
   }

   private static void transformVertex(Vector3f vertex, Vector3fc vector3fc, Matrix4fc matrix4fc, Vector3fc vector3fc2) {
      vertex.sub(vector3fc);
      matrix4fc.transformPosition(vertex);
      vertex.mul(vector3fc2);
      vertex.add(vector3fc);
   }

   private static Direction decodeDirection(int[] rotationMatrix) {
      Vector3f vector3f = bakeVectors(rotationMatrix, 0);
      Vector3f vector3f2 = bakeVectors(rotationMatrix, 8);
      Vector3f vector3f3 = bakeVectors(rotationMatrix, 16);
      Vector3f vector3f4 = (new Vector3f(vector3f)).sub(vector3f2);
      Vector3f vector3f5 = (new Vector3f(vector3f3)).sub(vector3f2);
      Vector3f vector3f6 = (new Vector3f(vector3f5)).cross(vector3f4).normalize();
      if (!vector3f6.isFinite()) {
         return Direction.UP;
      } else {
         Direction direction = null;
         float f = 0.0F;
         Direction[] var9 = Direction.values();
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Direction direction2 = var9[var11];
            float g = vector3f6.dot(direction2.getFloatVector());
            if (g >= 0.0F && g > f) {
               f = g;
               direction = direction2;
            }
         }

         if (direction == null) {
            return Direction.UP;
         } else {
            return direction;
         }
      }
   }

   private static float bakeVectorX(int[] is, int i) {
      return Float.intBitsToFloat(is[i]);
   }

   private static float bakeVectorY(int[] is, int i) {
      return Float.intBitsToFloat(is[i + 1]);
   }

   private static float bakeVectorZ(int[] is, int i) {
      return Float.intBitsToFloat(is[i + 2]);
   }

   private static Vector3f bakeVectors(int[] is, int i) {
      return new Vector3f(bakeVectorX(is, i), bakeVectorY(is, i), bakeVectorZ(is, i));
   }

   private static void encodeDirection(int[] rotationMatrix, Direction direction) {
      int[] is = new int[rotationMatrix.length];
      System.arraycopy(rotationMatrix, 0, is, 0, rotationMatrix.length);
      float[] fs = new float[Direction.values().length];
      fs[CubeFace.DirectionIds.WEST] = 999.0F;
      fs[CubeFace.DirectionIds.DOWN] = 999.0F;
      fs[CubeFace.DirectionIds.NORTH] = 999.0F;
      fs[CubeFace.DirectionIds.EAST] = -999.0F;
      fs[CubeFace.DirectionIds.UP] = -999.0F;
      fs[CubeFace.DirectionIds.SOUTH] = -999.0F;

      int j;
      float h;
      for(int i = 0; i < 4; ++i) {
         j = 8 * i;
         float f = bakeVectorX(is, j);
         float g = bakeVectorY(is, j);
         h = bakeVectorZ(is, j);
         if (f < fs[CubeFace.DirectionIds.WEST]) {
            fs[CubeFace.DirectionIds.WEST] = f;
         }

         if (g < fs[CubeFace.DirectionIds.DOWN]) {
            fs[CubeFace.DirectionIds.DOWN] = g;
         }

         if (h < fs[CubeFace.DirectionIds.NORTH]) {
            fs[CubeFace.DirectionIds.NORTH] = h;
         }

         if (f > fs[CubeFace.DirectionIds.EAST]) {
            fs[CubeFace.DirectionIds.EAST] = f;
         }

         if (g > fs[CubeFace.DirectionIds.UP]) {
            fs[CubeFace.DirectionIds.UP] = g;
         }

         if (h > fs[CubeFace.DirectionIds.SOUTH]) {
            fs[CubeFace.DirectionIds.SOUTH] = h;
         }
      }

      CubeFace cubeFace = CubeFace.getFace(direction);

      for(j = 0; j < 4; ++j) {
         int k = 8 * j;
         CubeFace.Corner corner = cubeFace.getCorner(j);
         h = fs[corner.xSide];
         float l = fs[corner.ySide];
         float m = fs[corner.zSide];
         rotationMatrix[k] = Float.floatToRawIntBits(h);
         rotationMatrix[k + 1] = Float.floatToRawIntBits(l);
         rotationMatrix[k + 2] = Float.floatToRawIntBits(m);

         for(int n = 0; n < 4; ++n) {
            int o = 8 * n;
            float p = bakeVectorX(is, o);
            float q = bakeVectorY(is, o);
            float r = bakeVectorZ(is, o);
            if (MathHelper.approximatelyEquals(h, p) && MathHelper.approximatelyEquals(l, q) && MathHelper.approximatelyEquals(m, r)) {
               rotationMatrix[k + 4] = is[o + 4];
               rotationMatrix[k + 4 + 1] = is[o + 4 + 1];
            }
         }
      }

   }

   public static void calculatePosition(int[] is, Consumer consumer) {
      for(int i = 0; i < 4; ++i) {
         consumer.accept(bakeVectors(is, 8 * i));
      }

   }
}
