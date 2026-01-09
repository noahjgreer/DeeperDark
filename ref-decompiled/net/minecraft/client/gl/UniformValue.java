package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public interface UniformValue {
   Codec CODEC = UniformValue.Type.CODEC.dispatch(UniformValue::getType, (type) -> {
      return type.mapCodec;
   });

   void write(Std140Builder builder);

   void addSize(Std140SizeCalculator calculator);

   Type getType();

   @Environment(EnvType.CLIENT)
   public static enum Type implements StringIdentifiable {
      INT("int", UniformValue.IntValue.CODEC),
      IVEC3("ivec3", UniformValue.Vec3iValue.CODEC),
      FLOAT("float", UniformValue.FloatValue.CODEC),
      VEC2("vec2", UniformValue.Vec2fValue.CODEC),
      VEC3("vec3", UniformValue.Vec3fValue.CODEC),
      VEC4("vec4", UniformValue.Vec4fValue.CODEC),
      MATRIX4X4("matrix4x4", UniformValue.Matrix4fValue.CODEC);

      public static final StringIdentifiable.EnumCodec CODEC = StringIdentifiable.createCodec(Type::values);
      private final String name;
      final MapCodec mapCodec;

      private Type(final String name, final Codec codec) {
         this.name = name;
         this.mapCodec = codec.fieldOf("value");
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] method_71134() {
         return new Type[]{INT, IVEC3, FLOAT, VEC2, VEC3, VEC4, MATRIX4X4};
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Matrix4fValue(Matrix4fc value) implements UniformValue {
      public static final Codec CODEC;

      public Matrix4fValue(Matrix4fc matrix4fc) {
         this.value = matrix4fc;
      }

      public void write(Std140Builder builder) {
         builder.putMat4f(this.value);
      }

      public void addSize(Std140SizeCalculator calculator) {
         calculator.putMat4f();
      }

      public Type getType() {
         return UniformValue.Type.MATRIX4X4;
      }

      public Matrix4fc value() {
         return this.value;
      }

      static {
         CODEC = Codecs.MATRIX_4F.xmap(Matrix4fValue::new, Matrix4fValue::value);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Vec4fValue(Vector4f value) implements UniformValue {
      public static final Codec CODEC;

      public Vec4fValue(Vector4f vector4f) {
         this.value = vector4f;
      }

      public void write(Std140Builder builder) {
         builder.putVec4(this.value);
      }

      public void addSize(Std140SizeCalculator calculator) {
         calculator.putVec4();
      }

      public Type getType() {
         return UniformValue.Type.VEC4;
      }

      public Vector4f value() {
         return this.value;
      }

      static {
         CODEC = Codecs.VECTOR_4F.xmap(Vec4fValue::new, Vec4fValue::value);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Vec3fValue(Vector3f value) implements UniformValue {
      public static final Codec CODEC;

      public Vec3fValue(Vector3f vector3f) {
         this.value = vector3f;
      }

      public void write(Std140Builder builder) {
         builder.putVec3(this.value);
      }

      public void addSize(Std140SizeCalculator calculator) {
         calculator.putVec3();
      }

      public Type getType() {
         return UniformValue.Type.VEC3;
      }

      public Vector3f value() {
         return this.value;
      }

      static {
         CODEC = Codecs.VECTOR_3F.xmap(Vec3fValue::new, Vec3fValue::value);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Vec2fValue(Vector2f value) implements UniformValue {
      public static final Codec CODEC;

      public Vec2fValue(Vector2f vector2f) {
         this.value = vector2f;
      }

      public void write(Std140Builder builder) {
         builder.putVec2(this.value);
      }

      public void addSize(Std140SizeCalculator calculator) {
         calculator.putVec2();
      }

      public Type getType() {
         return UniformValue.Type.VEC2;
      }

      public Vector2f value() {
         return this.value;
      }

      static {
         CODEC = Codecs.VECTOR_2F.xmap(Vec2fValue::new, Vec2fValue::value);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record FloatValue(float value) implements UniformValue {
      public static final Codec CODEC;

      public FloatValue(float f) {
         this.value = f;
      }

      public void write(Std140Builder builder) {
         builder.putFloat(this.value);
      }

      public void addSize(Std140SizeCalculator calculator) {
         calculator.putFloat();
      }

      public Type getType() {
         return UniformValue.Type.FLOAT;
      }

      public float value() {
         return this.value;
      }

      static {
         CODEC = Codec.FLOAT.xmap(FloatValue::new, FloatValue::value);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Vec3iValue(Vector3i value) implements UniformValue {
      public static final Codec CODEC;

      public Vec3iValue(Vector3i vector3i) {
         this.value = vector3i;
      }

      public void write(Std140Builder builder) {
         builder.putIVec3(this.value);
      }

      public void addSize(Std140SizeCalculator calculator) {
         calculator.putIVec3();
      }

      public Type getType() {
         return UniformValue.Type.IVEC3;
      }

      public Vector3i value() {
         return this.value;
      }

      static {
         CODEC = Codecs.VECTOR_3I.xmap(Vec3iValue::new, Vec3iValue::value);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record IntValue(int value) implements UniformValue {
      public static final Codec CODEC;

      public IntValue(int i) {
         this.value = i;
      }

      public void write(Std140Builder builder) {
         builder.putInt(this.value);
      }

      public void addSize(Std140SizeCalculator calculator) {
         calculator.putInt();
      }

      public Type getType() {
         return UniformValue.Type.INT;
      }

      public int value() {
         return this.value;
      }

      static {
         CODEC = Codec.INT.xmap(IntValue::new, IntValue::value);
      }
   }
}
