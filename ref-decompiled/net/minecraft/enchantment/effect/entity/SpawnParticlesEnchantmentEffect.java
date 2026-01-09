package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.random.Random;

public record SpawnParticlesEnchantmentEffect(ParticleEffect particle, PositionSource horizontalPosition, PositionSource verticalPosition, VelocitySource horizontalVelocity, VelocitySource verticalVelocity, FloatProvider speed) implements EnchantmentEntityEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ParticleTypes.TYPE_CODEC.fieldOf("particle").forGetter(SpawnParticlesEnchantmentEffect::particle), SpawnParticlesEnchantmentEffect.PositionSource.CODEC.fieldOf("horizontal_position").forGetter(SpawnParticlesEnchantmentEffect::horizontalPosition), SpawnParticlesEnchantmentEffect.PositionSource.CODEC.fieldOf("vertical_position").forGetter(SpawnParticlesEnchantmentEffect::verticalPosition), SpawnParticlesEnchantmentEffect.VelocitySource.CODEC.fieldOf("horizontal_velocity").forGetter(SpawnParticlesEnchantmentEffect::horizontalVelocity), SpawnParticlesEnchantmentEffect.VelocitySource.CODEC.fieldOf("vertical_velocity").forGetter(SpawnParticlesEnchantmentEffect::verticalVelocity), FloatProvider.VALUE_CODEC.optionalFieldOf("speed", ConstantFloatProvider.ZERO).forGetter(SpawnParticlesEnchantmentEffect::speed)).apply(instance, SpawnParticlesEnchantmentEffect::new);
   });

   public SpawnParticlesEnchantmentEffect(ParticleEffect particleEffect, PositionSource positionSource, PositionSource positionSource2, VelocitySource velocitySource, VelocitySource velocitySource2, FloatProvider floatProvider) {
      this.particle = particleEffect;
      this.horizontalPosition = positionSource;
      this.verticalPosition = positionSource2;
      this.horizontalVelocity = velocitySource;
      this.verticalVelocity = velocitySource2;
      this.speed = floatProvider;
   }

   public static PositionSource entityPosition(float offset) {
      return new PositionSource(SpawnParticlesEnchantmentEffect.PositionSourceType.ENTITY_POSITION, offset, 1.0F);
   }

   public static PositionSource withinBoundingBox() {
      return new PositionSource(SpawnParticlesEnchantmentEffect.PositionSourceType.BOUNDING_BOX, 0.0F, 1.0F);
   }

   public static VelocitySource scaledVelocity(float movementScale) {
      return new VelocitySource(movementScale, ConstantFloatProvider.ZERO);
   }

   public static VelocitySource fixedVelocity(FloatProvider base) {
      return new VelocitySource(0.0F, base);
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
      Random random = user.getRandom();
      Vec3d vec3d = user.getMovement();
      float f = user.getWidth();
      float g = user.getHeight();
      world.spawnParticles(this.particle, this.horizontalPosition.getPosition(pos.getX(), pos.getX(), f, random), this.verticalPosition.getPosition(pos.getY(), pos.getY() + (double)(g / 2.0F), g, random), this.horizontalPosition.getPosition(pos.getZ(), pos.getZ(), f, random), 0, this.horizontalVelocity.getVelocity(vec3d.getX(), random), this.verticalVelocity.getVelocity(vec3d.getY(), random), this.horizontalVelocity.getVelocity(vec3d.getZ(), random), (double)this.speed.get(random));
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public ParticleEffect particle() {
      return this.particle;
   }

   public PositionSource horizontalPosition() {
      return this.horizontalPosition;
   }

   public PositionSource verticalPosition() {
      return this.verticalPosition;
   }

   public VelocitySource horizontalVelocity() {
      return this.horizontalVelocity;
   }

   public VelocitySource verticalVelocity() {
      return this.verticalVelocity;
   }

   public FloatProvider speed() {
      return this.speed;
   }

   public static record PositionSource(PositionSourceType type, float offset, float scale) {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(SpawnParticlesEnchantmentEffect.PositionSourceType.CODEC.fieldOf("type").forGetter(PositionSource::type), Codec.FLOAT.optionalFieldOf("offset", 0.0F).forGetter(PositionSource::offset), Codecs.POSITIVE_FLOAT.optionalFieldOf("scale", 1.0F).forGetter(PositionSource::scale)).apply(instance, PositionSource::new);
      }).validate((source) -> {
         return source.type() == SpawnParticlesEnchantmentEffect.PositionSourceType.ENTITY_POSITION && source.scale() != 1.0F ? DataResult.error(() -> {
            return "Cannot scale an entity position coordinate source";
         }) : DataResult.success(source);
      });

      public PositionSource(PositionSourceType positionSourceType, float f, float g) {
         this.type = positionSourceType;
         this.offset = f;
         this.scale = g;
      }

      public double getPosition(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random) {
         return this.type.getCoordinate(entityPosition, boundingBoxCenter, boundingBoxSize * this.scale, random) + (double)this.offset;
      }

      public PositionSourceType type() {
         return this.type;
      }

      public float offset() {
         return this.offset;
      }

      public float scale() {
         return this.scale;
      }
   }

   public static record VelocitySource(float movementScale, FloatProvider base) {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.FLOAT.optionalFieldOf("movement_scale", 0.0F).forGetter(VelocitySource::movementScale), FloatProvider.VALUE_CODEC.optionalFieldOf("base", ConstantFloatProvider.ZERO).forGetter(VelocitySource::base)).apply(instance, VelocitySource::new);
      });

      public VelocitySource(float f, FloatProvider floatProvider) {
         this.movementScale = f;
         this.base = floatProvider;
      }

      public double getVelocity(double entityVelocity, Random random) {
         return entityVelocity * (double)this.movementScale + (double)this.base.get(random);
      }

      public float movementScale() {
         return this.movementScale;
      }

      public FloatProvider base() {
         return this.base;
      }
   }

   public static enum PositionSourceType implements StringIdentifiable {
      ENTITY_POSITION("entity_position", (entityPosition, boundingBoxCenter, boundingBoxSize, random) -> {
         return entityPosition;
      }),
      BOUNDING_BOX("in_bounding_box", (entityPosition, boundingBoxCenter, boundingBoxSize, random) -> {
         return boundingBoxCenter + (random.nextDouble() - 0.5) * (double)boundingBoxSize;
      });

      public static final Codec CODEC = StringIdentifiable.createCodec(PositionSourceType::values);
      private final String id;
      private final CoordinateSource coordinateSource;

      private PositionSourceType(final String id, final CoordinateSource coordinateSource) {
         this.id = id;
         this.coordinateSource = coordinateSource;
      }

      public double getCoordinate(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random) {
         return this.coordinateSource.getCoordinate(entityPosition, boundingBoxCenter, boundingBoxSize, random);
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static PositionSourceType[] method_60258() {
         return new PositionSourceType[]{ENTITY_POSITION, BOUNDING_BOX};
      }

      @FunctionalInterface
      interface CoordinateSource {
         double getCoordinate(double entityPosition, double boundingBoxCenter, float boundingBoxSize, Random random);
      }
   }
}
