package net.minecraft.enchantment;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.MathHelper;

public interface EnchantmentLevelBasedValue {
   Codec BASE_CODEC = Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE.getCodec().dispatch(EnchantmentLevelBasedValue::getCodec, (codec) -> {
      return codec;
   });
   Codec CODEC = Codec.either(EnchantmentLevelBasedValue.Constant.CODEC, BASE_CODEC).xmap((either) -> {
      return (EnchantmentLevelBasedValue)either.map((type) -> {
         return type;
      }, (type) -> {
         return type;
      });
   }, (type) -> {
      Either var10000;
      if (type instanceof Constant constant) {
         var10000 = Either.left(constant);
      } else {
         var10000 = Either.right(type);
      }

      return var10000;
   });

   static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"clamped", EnchantmentLevelBasedValue.Clamped.CODEC);
      Registry.register(registry, (String)"fraction", EnchantmentLevelBasedValue.Fraction.CODEC);
      Registry.register(registry, (String)"levels_squared", EnchantmentLevelBasedValue.LevelsSquared.CODEC);
      Registry.register(registry, (String)"linear", EnchantmentLevelBasedValue.Linear.CODEC);
      return (MapCodec)Registry.register(registry, (String)"lookup", EnchantmentLevelBasedValue.Lookup.CODEC);
   }

   static Constant constant(float value) {
      return new Constant(value);
   }

   static Linear linear(float base, float perLevelAboveFirst) {
      return new Linear(base, perLevelAboveFirst);
   }

   static Linear linear(float base) {
      return linear(base, base);
   }

   static Lookup lookup(List values, EnchantmentLevelBasedValue fallback) {
      return new Lookup(values, fallback);
   }

   float getValue(int level);

   MapCodec getCodec();

   public static record Clamped(EnchantmentLevelBasedValue value, float min, float max) implements EnchantmentLevelBasedValue {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("value").forGetter(Clamped::value), Codec.FLOAT.fieldOf("min").forGetter(Clamped::min), Codec.FLOAT.fieldOf("max").forGetter(Clamped::max)).apply(instance, Clamped::new);
      }).validate((type) -> {
         return type.max <= type.min ? DataResult.error(() -> {
            return "Max must be larger than min, min: " + type.min + ", max: " + type.max;
         }) : DataResult.success(type);
      });

      public Clamped(EnchantmentLevelBasedValue enchantmentLevelBasedValue, float f, float g) {
         this.value = enchantmentLevelBasedValue;
         this.min = f;
         this.max = g;
      }

      public float getValue(int level) {
         return MathHelper.clamp(this.value.getValue(level), this.min, this.max);
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public EnchantmentLevelBasedValue value() {
         return this.value;
      }

      public float min() {
         return this.min;
      }

      public float max() {
         return this.max;
      }
   }

   public static record Fraction(EnchantmentLevelBasedValue numerator, EnchantmentLevelBasedValue denominator) implements EnchantmentLevelBasedValue {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("numerator").forGetter(Fraction::numerator), EnchantmentLevelBasedValue.CODEC.fieldOf("denominator").forGetter(Fraction::denominator)).apply(instance, Fraction::new);
      });

      public Fraction(EnchantmentLevelBasedValue enchantmentLevelBasedValue, EnchantmentLevelBasedValue enchantmentLevelBasedValue2) {
         this.numerator = enchantmentLevelBasedValue;
         this.denominator = enchantmentLevelBasedValue2;
      }

      public float getValue(int level) {
         float f = this.denominator.getValue(level);
         return f == 0.0F ? 0.0F : this.numerator.getValue(level) / f;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public EnchantmentLevelBasedValue numerator() {
         return this.numerator;
      }

      public EnchantmentLevelBasedValue denominator() {
         return this.denominator;
      }
   }

   public static record LevelsSquared(float added) implements EnchantmentLevelBasedValue {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.FLOAT.fieldOf("added").forGetter(LevelsSquared::added)).apply(instance, LevelsSquared::new);
      });

      public LevelsSquared(float f) {
         this.added = f;
      }

      public float getValue(int level) {
         return (float)MathHelper.square(level) + this.added;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public float added() {
         return this.added;
      }
   }

   public static record Linear(float base, float perLevelAboveFirst) implements EnchantmentLevelBasedValue {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.FLOAT.fieldOf("base").forGetter(Linear::base), Codec.FLOAT.fieldOf("per_level_above_first").forGetter(Linear::perLevelAboveFirst)).apply(instance, Linear::new);
      });

      public Linear(float f, float g) {
         this.base = f;
         this.perLevelAboveFirst = g;
      }

      public float getValue(int level) {
         return this.base + this.perLevelAboveFirst * (float)(level - 1);
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public float base() {
         return this.base;
      }

      public float perLevelAboveFirst() {
         return this.perLevelAboveFirst;
      }
   }

   public static record Lookup(List values, EnchantmentLevelBasedValue fallback) implements EnchantmentLevelBasedValue {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.FLOAT.listOf().fieldOf("values").forGetter(Lookup::values), EnchantmentLevelBasedValue.CODEC.fieldOf("fallback").forGetter(Lookup::fallback)).apply(instance, Lookup::new);
      });

      public Lookup(List list, EnchantmentLevelBasedValue enchantmentLevelBasedValue) {
         this.values = list;
         this.fallback = enchantmentLevelBasedValue;
      }

      public float getValue(int level) {
         return level <= this.values.size() ? (Float)this.values.get(level - 1) : this.fallback.getValue(level);
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public List values() {
         return this.values;
      }

      public EnchantmentLevelBasedValue fallback() {
         return this.fallback;
      }
   }

   public static record Constant(float value) implements EnchantmentLevelBasedValue {
      public static final Codec CODEC;
      public static final MapCodec TYPE_CODEC;

      public Constant(float f) {
         this.value = f;
      }

      public float getValue(int level) {
         return this.value;
      }

      public MapCodec getCodec() {
         return TYPE_CODEC;
      }

      public float value() {
         return this.value;
      }

      static {
         CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
         TYPE_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.FLOAT.fieldOf("value").forGetter(Constant::value)).apply(instance, Constant::new);
         });
      }
   }
}
