package net.minecraft.world.gen.densityfunction;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.function.ToFloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.dimension.DimensionType;
import org.slf4j.Logger;

public final class DensityFunctionTypes {
   private static final Codec DYNAMIC_RANGE;
   protected static final double MAX_CONSTANT_VALUE = 1000000.0;
   static final Codec CONSTANT_RANGE;
   public static final Codec CODEC;

   public static MapCodec registerAndGetDefault(Registry registry) {
      register(registry, "blend_alpha", DensityFunctionTypes.BlendAlpha.CODEC);
      register(registry, "blend_offset", DensityFunctionTypes.BlendOffset.CODEC);
      register(registry, "beardifier", DensityFunctionTypes.Beardifier.CODEC_HOLDER);
      register(registry, "old_blended_noise", InterpolatedNoiseSampler.CODEC);
      Wrapping.Type[] var1 = DensityFunctionTypes.Wrapping.Type.values();
      int var2 = var1.length;

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         Wrapping.Type type = var1[var3];
         register(registry, type.asString(), type.codec);
      }

      register(registry, "noise", DensityFunctionTypes.Noise.CODEC_HOLDER);
      register(registry, "end_islands", DensityFunctionTypes.EndIslands.CODEC_HOLDER);
      register(registry, "weird_scaled_sampler", DensityFunctionTypes.WeirdScaledSampler.CODEC_HOLDER);
      register(registry, "shifted_noise", DensityFunctionTypes.ShiftedNoise.CODEC_HOLDER);
      register(registry, "range_choice", DensityFunctionTypes.RangeChoice.CODEC_HOLDER);
      register(registry, "shift_a", DensityFunctionTypes.ShiftA.CODEC_HOLDER);
      register(registry, "shift_b", DensityFunctionTypes.ShiftB.CODEC_HOLDER);
      register(registry, "shift", DensityFunctionTypes.Shift.CODEC_HOLDER);
      register(registry, "blend_density", DensityFunctionTypes.BlendDensity.CODEC_HOLDER);
      register(registry, "clamp", DensityFunctionTypes.Clamp.CODEC_HOLDER);
      UnaryOperation.Type[] var5 = DensityFunctionTypes.UnaryOperation.Type.values();
      var2 = var5.length;

      for(var3 = 0; var3 < var2; ++var3) {
         UnaryOperation.Type type2 = var5[var3];
         register(registry, type2.asString(), type2.codecHolder);
      }

      BinaryOperationLike.Type[] var6 = DensityFunctionTypes.BinaryOperationLike.Type.values();
      var2 = var6.length;

      for(var3 = 0; var3 < var2; ++var3) {
         BinaryOperationLike.Type type3 = var6[var3];
         register(registry, type3.asString(), type3.codecHolder);
      }

      register(registry, "spline", DensityFunctionTypes.Spline.CODEC_HOLDER);
      register(registry, "constant", DensityFunctionTypes.Constant.CODEC_HOLDER);
      return register(registry, "y_clamped_gradient", DensityFunctionTypes.YClampedGradient.CODEC_HOLDER);
   }

   private static MapCodec register(Registry registry, String id, CodecHolder codecHolder) {
      return (MapCodec)Registry.register(registry, (String)id, codecHolder.codec());
   }

   static CodecHolder holderOf(Codec codec, Function creator, Function argumentGetter) {
      return CodecHolder.of(codec.fieldOf("argument").xmap(creator, argumentGetter));
   }

   static CodecHolder holderOf(Function creator, Function argumentGetter) {
      return holderOf(DensityFunction.FUNCTION_CODEC, creator, argumentGetter);
   }

   static CodecHolder holderOf(BiFunction creator, Function argument1Getter, Function argument2Getter) {
      return CodecHolder.of(RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DensityFunction.FUNCTION_CODEC.fieldOf("argument1").forGetter(argument1Getter), DensityFunction.FUNCTION_CODEC.fieldOf("argument2").forGetter(argument2Getter)).apply(instance, creator);
      }));
   }

   static CodecHolder holderOf(MapCodec mapCodec) {
      return CodecHolder.of(mapCodec);
   }

   private DensityFunctionTypes() {
   }

   public static DensityFunction interpolated(DensityFunction inputFunction) {
      return new Wrapping(DensityFunctionTypes.Wrapping.Type.INTERPOLATED, inputFunction);
   }

   public static DensityFunction flatCache(DensityFunction inputFunction) {
      return new Wrapping(DensityFunctionTypes.Wrapping.Type.FLAT_CACHE, inputFunction);
   }

   public static DensityFunction cache2d(DensityFunction inputFunction) {
      return new Wrapping(DensityFunctionTypes.Wrapping.Type.CACHE2D, inputFunction);
   }

   public static DensityFunction cacheOnce(DensityFunction inputFunction) {
      return new Wrapping(DensityFunctionTypes.Wrapping.Type.CACHE_ONCE, inputFunction);
   }

   public static DensityFunction cacheAllInCell(DensityFunction inputFunction) {
      return new Wrapping(DensityFunctionTypes.Wrapping.Type.CACHE_ALL_IN_CELL, inputFunction);
   }

   public static DensityFunction noiseInRange(RegistryEntry noiseParameters, @Deprecated double scaleXz, double scaleY, double min, double max) {
      return mapRange(new Noise(new DensityFunction.Noise(noiseParameters), scaleXz, scaleY), min, max);
   }

   public static DensityFunction noiseInRange(RegistryEntry noiseParameters, double scaleY, double min, double max) {
      return noiseInRange(noiseParameters, 1.0, scaleY, min, max);
   }

   public static DensityFunction noiseInRange(RegistryEntry noiseParameters, double min, double max) {
      return noiseInRange(noiseParameters, 1.0, 1.0, min, max);
   }

   public static DensityFunction shiftedNoise(DensityFunction shiftX, DensityFunction shiftZ, double xzScale, RegistryEntry noiseParameters) {
      return new ShiftedNoise(shiftX, zero(), shiftZ, xzScale, 0.0, new DensityFunction.Noise(noiseParameters));
   }

   public static DensityFunction noise(RegistryEntry noiseParameters) {
      return noise(noiseParameters, 1.0, 1.0);
   }

   public static DensityFunction noise(RegistryEntry noiseParameters, double scaleXz, double scaleY) {
      return new Noise(new DensityFunction.Noise(noiseParameters), scaleXz, scaleY);
   }

   public static DensityFunction noise(RegistryEntry noiseParameters, double scaleY) {
      return noise(noiseParameters, 1.0, scaleY);
   }

   public static DensityFunction rangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) {
      return new RangeChoice(input, minInclusive, maxExclusive, whenInRange, whenOutOfRange);
   }

   public static DensityFunction shiftA(RegistryEntry noiseParameters) {
      return new ShiftA(new DensityFunction.Noise(noiseParameters));
   }

   public static DensityFunction shiftB(RegistryEntry noiseParameters) {
      return new ShiftB(new DensityFunction.Noise(noiseParameters));
   }

   public static DensityFunction shift(RegistryEntry noiseParameters) {
      return new Shift(new DensityFunction.Noise(noiseParameters));
   }

   public static DensityFunction blendDensity(DensityFunction input) {
      return new BlendDensity(input);
   }

   public static DensityFunction endIslands(long seed) {
      return new EndIslands(seed);
   }

   public static DensityFunction weirdScaledSampler(DensityFunction input, RegistryEntry parameters, WeirdScaledSampler.RarityValueMapper mapper) {
      return new WeirdScaledSampler(input, new DensityFunction.Noise(parameters), mapper);
   }

   public static DensityFunction add(DensityFunction a, DensityFunction b) {
      return DensityFunctionTypes.BinaryOperationLike.create(DensityFunctionTypes.BinaryOperationLike.Type.ADD, a, b);
   }

   public static DensityFunction mul(DensityFunction a, DensityFunction b) {
      return DensityFunctionTypes.BinaryOperationLike.create(DensityFunctionTypes.BinaryOperationLike.Type.MUL, a, b);
   }

   public static DensityFunction min(DensityFunction a, DensityFunction b) {
      return DensityFunctionTypes.BinaryOperationLike.create(DensityFunctionTypes.BinaryOperationLike.Type.MIN, a, b);
   }

   public static DensityFunction max(DensityFunction a, DensityFunction b) {
      return DensityFunctionTypes.BinaryOperationLike.create(DensityFunctionTypes.BinaryOperationLike.Type.MAX, a, b);
   }

   public static DensityFunction spline(net.minecraft.util.math.Spline spline) {
      return new Spline(spline);
   }

   public static DensityFunction zero() {
      return DensityFunctionTypes.Constant.ZERO;
   }

   public static DensityFunction constant(double density) {
      return new Constant(density);
   }

   public static DensityFunction yClampedGradient(int fromY, int toY, double fromValue, double toValue) {
      return new YClampedGradient(fromY, toY, fromValue, toValue);
   }

   public static DensityFunction unary(DensityFunction input, UnaryOperation.Type type) {
      return DensityFunctionTypes.UnaryOperation.create(type, input);
   }

   private static DensityFunction mapRange(DensityFunction function, double min, double max) {
      double d = (min + max) * 0.5;
      double e = (max - min) * 0.5;
      return add(constant(d), mul(constant(e), function));
   }

   public static DensityFunction blendAlpha() {
      return DensityFunctionTypes.BlendAlpha.INSTANCE;
   }

   public static DensityFunction blendOffset() {
      return DensityFunctionTypes.BlendOffset.INSTANCE;
   }

   public static DensityFunction lerp(DensityFunction delta, DensityFunction start, DensityFunction end) {
      if (start instanceof Constant constant) {
         return lerp(delta, constant.value, end);
      } else {
         DensityFunction densityFunction = cacheOnce(delta);
         DensityFunction densityFunction2 = add(mul(densityFunction, constant(-1.0)), constant(1.0));
         return add(mul(start, densityFunction2), mul(end, densityFunction));
      }
   }

   public static DensityFunction lerp(DensityFunction delta, double start, DensityFunction end) {
      return add(mul(delta, add(end, constant(-start))), constant(start));
   }

   static {
      DYNAMIC_RANGE = Registries.DENSITY_FUNCTION_TYPE.getCodec().dispatch((densityFunction) -> {
         return densityFunction.getCodecHolder().codec();
      }, Function.identity());
      CONSTANT_RANGE = Codec.doubleRange(-1000000.0, 1000000.0);
      CODEC = Codec.either(CONSTANT_RANGE, DYNAMIC_RANGE).xmap((either) -> {
         return (DensityFunction)either.map(DensityFunctionTypes::constant, Function.identity());
      }, (densityFunction) -> {
         if (densityFunction instanceof Constant constant) {
            return Either.left(constant.value());
         } else {
            return Either.right(densityFunction);
         }
      });
   }

   protected static enum BlendAlpha implements DensityFunction.Base {
      INSTANCE;

      public static final CodecHolder CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

      public double sample(DensityFunction.NoisePos pos) {
         return 1.0;
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         Arrays.fill(densities, 1.0);
      }

      public double minValue() {
         return 1.0;
      }

      public double maxValue() {
         return 1.0;
      }

      public CodecHolder getCodecHolder() {
         return CODEC;
      }

      // $FF: synthetic method
      private static BlendAlpha[] method_40517() {
         return new BlendAlpha[]{INSTANCE};
      }
   }

   protected static enum BlendOffset implements DensityFunction.Base {
      INSTANCE;

      public static final CodecHolder CODEC = CodecHolder.of(MapCodec.unit(INSTANCE));

      public double sample(DensityFunction.NoisePos pos) {
         return 0.0;
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         Arrays.fill(densities, 0.0);
      }

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return 0.0;
      }

      public CodecHolder getCodecHolder() {
         return CODEC;
      }

      // $FF: synthetic method
      private static BlendOffset[] method_40519() {
         return new BlendOffset[]{INSTANCE};
      }
   }

   protected static enum Beardifier implements Beardifying {
      INSTANCE;

      public double sample(DensityFunction.NoisePos pos) {
         return 0.0;
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         Arrays.fill(densities, 0.0);
      }

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return 0.0;
      }

      // $FF: synthetic method
      private static Beardifier[] method_41077() {
         return new Beardifier[]{INSTANCE};
      }
   }

   protected static record Wrapping(Type type, DensityFunction wrapped) implements Wrapper {
      protected Wrapping(Type type, DensityFunction densityFunction) {
         this.type = type;
         this.wrapped = densityFunction;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return this.wrapped.sample(pos);
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         this.wrapped.fill(densities, applier);
      }

      public double minValue() {
         return this.wrapped.minValue();
      }

      public double maxValue() {
         return this.wrapped.maxValue();
      }

      public Type type() {
         return this.type;
      }

      public DensityFunction wrapped() {
         return this.wrapped;
      }

      static enum Type implements StringIdentifiable {
         INTERPOLATED("interpolated"),
         FLAT_CACHE("flat_cache"),
         CACHE2D("cache_2d"),
         CACHE_ONCE("cache_once"),
         CACHE_ALL_IN_CELL("cache_all_in_cell");

         private final String name;
         final CodecHolder codec = DensityFunctionTypes.holderOf((densityFunction) -> {
            return new Wrapping(this, densityFunction);
         }, Wrapper::wrapped);

         private Type(final String name) {
            this.name = name;
         }

         public String asString() {
            return this.name;
         }

         // $FF: synthetic method
         private static Type[] method_40523() {
            return new Type[]{INTERPOLATED, FLAT_CACHE, CACHE2D, CACHE_ONCE, CACHE_ALL_IN_CELL};
         }
      }
   }

   protected static record Noise(DensityFunction.Noise noise, double xzScale, double yScale) implements DensityFunction {
      public static final MapCodec NOISE_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DensityFunction.Noise.CODEC.fieldOf("noise").forGetter(Noise::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(Noise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(Noise::yScale)).apply(instance, Noise::new);
      });
      public static final CodecHolder CODEC_HOLDER;

      protected Noise(DensityFunction.Noise noise, @Deprecated double d, double e) {
         this.noise = noise;
         this.xzScale = d;
         this.yScale = e;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return this.noise.sample((double)pos.blockX() * this.xzScale, (double)pos.blockY() * this.yScale, (double)pos.blockZ() * this.xzScale);
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new Noise(visitor.apply(this.noise), this.xzScale, this.yScale)));
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise.getMaxValue();
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction.Noise noise() {
         return this.noise;
      }

      /** @deprecated */
      @Deprecated
      public double xzScale() {
         return this.xzScale;
      }

      public double yScale() {
         return this.yScale;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(NOISE_CODEC);
      }
   }

   protected static final class EndIslands implements DensityFunction.Base {
      public static final CodecHolder CODEC_HOLDER = CodecHolder.of(MapCodec.unit(new EndIslands(0L)));
      private static final float field_37677 = -0.9F;
      private final SimplexNoiseSampler sampler;

      public EndIslands(long seed) {
         Random random = new CheckedRandom(seed);
         random.skip(17292);
         this.sampler = new SimplexNoiseSampler(random);
      }

      private static float sample(SimplexNoiseSampler sampler, int x, int z) {
         int i = x / 2;
         int j = z / 2;
         int k = x % 2;
         int l = z % 2;
         float f = 100.0F - MathHelper.sqrt((float)(x * x + z * z)) * 8.0F;
         f = MathHelper.clamp(f, -100.0F, 80.0F);

         for(int m = -12; m <= 12; ++m) {
            for(int n = -12; n <= 12; ++n) {
               long o = (long)(i + m);
               long p = (long)(j + n);
               if (o * o + p * p > 4096L && sampler.sample((double)o, (double)p) < -0.8999999761581421) {
                  float g = (MathHelper.abs((float)o) * 3439.0F + MathHelper.abs((float)p) * 147.0F) % 13.0F + 9.0F;
                  float h = (float)(k - m * 2);
                  float q = (float)(l - n * 2);
                  float r = 100.0F - MathHelper.sqrt(h * h + q * q) * g;
                  r = MathHelper.clamp(r, -100.0F, 80.0F);
                  f = Math.max(f, r);
               }
            }
         }

         return f;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return ((double)sample(this.sampler, pos.blockX() / 8, pos.blockZ() / 8) - 8.0) / 128.0;
      }

      public double minValue() {
         return -0.84375;
      }

      public double maxValue() {
         return 0.5625;
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }
   }

   protected static record WeirdScaledSampler(DensityFunction input, DensityFunction.Noise noise, RarityValueMapper rarityValueMapper) implements Positional {
      private static final MapCodec WEIRD_SCALED_SAMPLER_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DensityFunction.FUNCTION_CODEC.fieldOf("input").forGetter(WeirdScaledSampler::input), DensityFunction.Noise.CODEC.fieldOf("noise").forGetter(WeirdScaledSampler::noise), DensityFunctionTypes.WeirdScaledSampler.RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(WeirdScaledSampler::rarityValueMapper)).apply(instance, WeirdScaledSampler::new);
      });
      public static final CodecHolder CODEC_HOLDER;

      protected WeirdScaledSampler(DensityFunction densityFunction, DensityFunction.Noise noise, RarityValueMapper rarityValueMapper) {
         this.input = densityFunction;
         this.noise = noise;
         this.rarityValueMapper = rarityValueMapper;
      }

      public double apply(DensityFunction.NoisePos pos, double density) {
         double d = this.rarityValueMapper.scaleFunction.get(density);
         return d * Math.abs(this.noise.sample((double)pos.blockX() / d, (double)pos.blockY() / d, (double)pos.blockZ() / d));
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new WeirdScaledSampler(this.input.apply(visitor), visitor.apply(this.noise), this.rarityValueMapper)));
      }

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return this.rarityValueMapper.maxValueMultiplier * this.noise.getMaxValue();
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction input() {
         return this.input;
      }

      public DensityFunction.Noise noise() {
         return this.noise;
      }

      public RarityValueMapper rarityValueMapper() {
         return this.rarityValueMapper;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(WEIRD_SCALED_SAMPLER_CODEC);
      }

      public static enum RarityValueMapper implements StringIdentifiable {
         TYPE1("type_1", DensityFunctions.CaveScaler::scaleTunnels, 2.0),
         TYPE2("type_2", DensityFunctions.CaveScaler::scaleCaves, 3.0);

         public static final Codec CODEC = StringIdentifiable.createCodec(RarityValueMapper::values);
         private final String name;
         final Double2DoubleFunction scaleFunction;
         final double maxValueMultiplier;

         private RarityValueMapper(final String name, final Double2DoubleFunction scaleFunction, final double maxValueMultiplier) {
            this.name = name;
            this.scaleFunction = scaleFunction;
            this.maxValueMultiplier = maxValueMultiplier;
         }

         public String asString() {
            return this.name;
         }

         // $FF: synthetic method
         private static RarityValueMapper[] method_41074() {
            return new RarityValueMapper[]{TYPE1, TYPE2};
         }
      }
   }

   protected static record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.Noise noise) implements DensityFunction {
      private static final MapCodec SHIFTED_NOISE_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DensityFunction.FUNCTION_CODEC.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX), DensityFunction.FUNCTION_CODEC.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY), DensityFunction.FUNCTION_CODEC.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale), DensityFunction.Noise.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noise)).apply(instance, ShiftedNoise::new);
      });
      public static final CodecHolder CODEC_HOLDER;

      protected ShiftedNoise(DensityFunction densityFunction, DensityFunction densityFunction2, DensityFunction densityFunction3, double d, double e, DensityFunction.Noise noise) {
         this.shiftX = densityFunction;
         this.shiftY = densityFunction2;
         this.shiftZ = densityFunction3;
         this.xzScale = d;
         this.yScale = e;
         this.noise = noise;
      }

      public double sample(DensityFunction.NoisePos pos) {
         double d = (double)pos.blockX() * this.xzScale + this.shiftX.sample(pos);
         double e = (double)pos.blockY() * this.yScale + this.shiftY.sample(pos);
         double f = (double)pos.blockZ() * this.xzScale + this.shiftZ.sample(pos);
         return this.noise.sample(d, e, f);
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new ShiftedNoise(this.shiftX.apply(visitor), this.shiftY.apply(visitor), this.shiftZ.apply(visitor), this.xzScale, this.yScale, visitor.apply(this.noise))));
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise.getMaxValue();
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction shiftX() {
         return this.shiftX;
      }

      public DensityFunction shiftY() {
         return this.shiftY;
      }

      public DensityFunction shiftZ() {
         return this.shiftZ;
      }

      public double xzScale() {
         return this.xzScale;
      }

      public double yScale() {
         return this.yScale;
      }

      public DensityFunction.Noise noise() {
         return this.noise;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(SHIFTED_NOISE_CODEC);
      }
   }

   private static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
      public static final MapCodec RANGE_CHOICE_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DensityFunction.FUNCTION_CODEC.fieldOf("input").forGetter(RangeChoice::input), DensityFunctionTypes.CONSTANT_RANGE.fieldOf("min_inclusive").forGetter(RangeChoice::minInclusive), DensityFunctionTypes.CONSTANT_RANGE.fieldOf("max_exclusive").forGetter(RangeChoice::maxExclusive), DensityFunction.FUNCTION_CODEC.fieldOf("when_in_range").forGetter(RangeChoice::whenInRange), DensityFunction.FUNCTION_CODEC.fieldOf("when_out_of_range").forGetter(RangeChoice::whenOutOfRange)).apply(instance, RangeChoice::new);
      });
      public static final CodecHolder CODEC_HOLDER;

      RangeChoice(DensityFunction densityFunction, double d, double e, DensityFunction densityFunction2, DensityFunction densityFunction3) {
         this.input = densityFunction;
         this.minInclusive = d;
         this.maxExclusive = e;
         this.whenInRange = densityFunction2;
         this.whenOutOfRange = densityFunction3;
      }

      public double sample(DensityFunction.NoisePos pos) {
         double d = this.input.sample(pos);
         return d >= this.minInclusive && d < this.maxExclusive ? this.whenInRange.sample(pos) : this.whenOutOfRange.sample(pos);
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         this.input.fill(densities, applier);

         for(int i = 0; i < densities.length; ++i) {
            double d = densities[i];
            if (d >= this.minInclusive && d < this.maxExclusive) {
               densities[i] = this.whenInRange.sample(applier.at(i));
            } else {
               densities[i] = this.whenOutOfRange.sample(applier.at(i));
            }
         }

      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new RangeChoice(this.input.apply(visitor), this.minInclusive, this.maxExclusive, this.whenInRange.apply(visitor), this.whenOutOfRange.apply(visitor))));
      }

      public double minValue() {
         return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
      }

      public double maxValue() {
         return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minInclusive() {
         return this.minInclusive;
      }

      public double maxExclusive() {
         return this.maxExclusive;
      }

      public DensityFunction whenInRange() {
         return this.whenInRange;
      }

      public DensityFunction whenOutOfRange() {
         return this.whenOutOfRange;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(RANGE_CHOICE_CODEC);
      }
   }

   protected static record ShiftA(DensityFunction.Noise offsetNoise) implements Offset {
      static final CodecHolder CODEC_HOLDER;

      protected ShiftA(DensityFunction.Noise noise) {
         this.offsetNoise = noise;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return this.sample((double)pos.blockX(), 0.0, (double)pos.blockZ());
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new ShiftA(visitor.apply(this.offsetNoise))));
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction.Noise offsetNoise() {
         return this.offsetNoise;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(DensityFunction.Noise.CODEC, ShiftA::new, ShiftA::offsetNoise);
      }
   }

   protected static record ShiftB(DensityFunction.Noise offsetNoise) implements Offset {
      static final CodecHolder CODEC_HOLDER;

      protected ShiftB(DensityFunction.Noise noise) {
         this.offsetNoise = noise;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return this.sample((double)pos.blockZ(), (double)pos.blockX(), 0.0);
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new ShiftB(visitor.apply(this.offsetNoise))));
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction.Noise offsetNoise() {
         return this.offsetNoise;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(DensityFunction.Noise.CODEC, ShiftB::new, ShiftB::offsetNoise);
      }
   }

   protected static record Shift(DensityFunction.Noise offsetNoise) implements Offset {
      static final CodecHolder CODEC_HOLDER;

      protected Shift(DensityFunction.Noise noise) {
         this.offsetNoise = noise;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return this.sample((double)pos.blockX(), (double)pos.blockY(), (double)pos.blockZ());
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new Shift(visitor.apply(this.offsetNoise))));
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction.Noise offsetNoise() {
         return this.offsetNoise;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(DensityFunction.Noise.CODEC, Shift::new, Shift::offsetNoise);
      }
   }

   private static record BlendDensity(DensityFunction input) implements Positional {
      static final CodecHolder CODEC_HOLDER = DensityFunctionTypes.holderOf(BlendDensity::new, BlendDensity::input);

      BlendDensity(DensityFunction densityFunction) {
         this.input = densityFunction;
      }

      public double apply(DensityFunction.NoisePos pos, double density) {
         return pos.getBlender().applyBlendDensity(pos, density);
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new BlendDensity(this.input.apply(visitor))));
      }

      public double minValue() {
         return Double.NEGATIVE_INFINITY;
      }

      public double maxValue() {
         return Double.POSITIVE_INFINITY;
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction input() {
         return this.input;
      }
   }

   protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements Unary {
      private static final MapCodec CLAMP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DensityFunction.CODEC.fieldOf("input").forGetter(Clamp::input), DensityFunctionTypes.CONSTANT_RANGE.fieldOf("min").forGetter(Clamp::minValue), DensityFunctionTypes.CONSTANT_RANGE.fieldOf("max").forGetter(Clamp::maxValue)).apply(instance, Clamp::new);
      });
      public static final CodecHolder CODEC_HOLDER;

      protected Clamp(DensityFunction densityFunction, double d, double e) {
         this.input = densityFunction;
         this.minValue = d;
         this.maxValue = e;
      }

      public double apply(double density) {
         return MathHelper.clamp(density, this.minValue, this.maxValue);
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return new Clamp(this.input.apply(visitor), this.minValue, this.maxValue);
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(CLAMP_CODEC);
      }
   }

   protected static record UnaryOperation(Type type, DensityFunction input, double minValue, double maxValue) implements Unary {
      protected UnaryOperation(Type type, DensityFunction densityFunction, double d, double e) {
         this.type = type;
         this.input = densityFunction;
         this.minValue = d;
         this.maxValue = e;
      }

      public static UnaryOperation create(Type type, DensityFunction input) {
         double d = input.minValue();
         double e = apply(type, d);
         double f = apply(type, input.maxValue());
         return type != DensityFunctionTypes.UnaryOperation.Type.ABS && type != DensityFunctionTypes.UnaryOperation.Type.SQUARE ? new UnaryOperation(type, input, e, f) : new UnaryOperation(type, input, Math.max(0.0, d), Math.max(e, f));
      }

      private static double apply(Type type, double density) {
         double var10000;
         switch (type.ordinal()) {
            case 0:
               var10000 = Math.abs(density);
               break;
            case 1:
               var10000 = density * density;
               break;
            case 2:
               var10000 = density * density * density;
               break;
            case 3:
               var10000 = density > 0.0 ? density : density * 0.5;
               break;
            case 4:
               var10000 = density > 0.0 ? density : density * 0.25;
               break;
            case 5:
               double d = MathHelper.clamp(density, -1.0, 1.0);
               var10000 = d / 2.0 - d * d * d / 24.0;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public double apply(double density) {
         return apply(this.type, density);
      }

      public UnaryOperation apply(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
         return create(this.type, this.input.apply(densityFunctionVisitor));
      }

      public CodecHolder getCodecHolder() {
         return this.type.codecHolder;
      }

      public Type type() {
         return this.type;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      // $FF: synthetic method
      public DensityFunction apply(final DensityFunction.DensityFunctionVisitor visitor) {
         return this.apply(visitor);
      }

      static enum Type implements StringIdentifiable {
         ABS("abs"),
         SQUARE("square"),
         CUBE("cube"),
         HALF_NEGATIVE("half_negative"),
         QUARTER_NEGATIVE("quarter_negative"),
         SQUEEZE("squeeze");

         private final String name;
         final CodecHolder codecHolder = DensityFunctionTypes.holderOf((input) -> {
            return DensityFunctionTypes.UnaryOperation.create(this, input);
         }, UnaryOperation::input);

         private Type(final String name) {
            this.name = name;
         }

         public String asString() {
            return this.name;
         }

         // $FF: synthetic method
         private static Type[] method_40522() {
            return new Type[]{ABS, SQUARE, CUBE, HALF_NEGATIVE, QUARTER_NEGATIVE, SQUEEZE};
         }
      }
   }

   interface BinaryOperationLike extends DensityFunction {
      Logger LOGGER = LogUtils.getLogger();

      static BinaryOperationLike create(Type type, DensityFunction argument1, DensityFunction argument2) {
         double d = argument1.minValue();
         double e = argument2.minValue();
         double f = argument1.maxValue();
         double g = argument2.maxValue();
         if (type == DensityFunctionTypes.BinaryOperationLike.Type.MIN || type == DensityFunctionTypes.BinaryOperationLike.Type.MAX) {
            boolean bl = d >= g;
            boolean bl2 = e >= f;
            if (bl || bl2) {
               Logger var10000 = LOGGER;
               String var10001 = String.valueOf(type);
               var10000.warn("Creating a " + var10001 + " function between two non-overlapping inputs: " + String.valueOf(argument1) + " and " + String.valueOf(argument2));
            }
         }

         double var17;
         switch (type.ordinal()) {
            case 0:
               var17 = d + e;
               break;
            case 1:
               var17 = d > 0.0 && e > 0.0 ? d * e : (f < 0.0 && g < 0.0 ? f * g : Math.min(d * g, f * e));
               break;
            case 2:
               var17 = Math.min(d, e);
               break;
            case 3:
               var17 = Math.max(d, e);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         double h = var17;
         switch (type.ordinal()) {
            case 0:
               var17 = f + g;
               break;
            case 1:
               var17 = d > 0.0 && e > 0.0 ? f * g : (f < 0.0 && g < 0.0 ? d * e : Math.max(d * e, f * g));
               break;
            case 2:
               var17 = Math.min(f, g);
               break;
            case 3:
               var17 = Math.max(f, g);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         double i = var17;
         if (type == DensityFunctionTypes.BinaryOperationLike.Type.MUL || type == DensityFunctionTypes.BinaryOperationLike.Type.ADD) {
            Constant constant;
            if (argument1 instanceof Constant) {
               constant = (Constant)argument1;
               return new LinearOperation(type == DensityFunctionTypes.BinaryOperationLike.Type.ADD ? DensityFunctionTypes.LinearOperation.SpecificType.ADD : DensityFunctionTypes.LinearOperation.SpecificType.MUL, argument2, h, i, constant.value);
            }

            if (argument2 instanceof Constant) {
               constant = (Constant)argument2;
               return new LinearOperation(type == DensityFunctionTypes.BinaryOperationLike.Type.ADD ? DensityFunctionTypes.LinearOperation.SpecificType.ADD : DensityFunctionTypes.LinearOperation.SpecificType.MUL, argument1, h, i, constant.value);
            }
         }

         return new BinaryOperation(type, argument1, argument2, h, i);
      }

      Type type();

      DensityFunction argument1();

      DensityFunction argument2();

      default CodecHolder getCodecHolder() {
         return this.type().codecHolder;
      }

      public static enum Type implements StringIdentifiable {
         ADD("add"),
         MUL("mul"),
         MIN("min"),
         MAX("max");

         final CodecHolder codecHolder = DensityFunctionTypes.holderOf((densityFunction, densityFunction2) -> {
            return DensityFunctionTypes.BinaryOperationLike.create(this, densityFunction, densityFunction2);
         }, BinaryOperationLike::argument1, BinaryOperationLike::argument2);
         private final String name;

         private Type(final String name) {
            this.name = name;
         }

         public String asString() {
            return this.name;
         }

         // $FF: synthetic method
         private static Type[] method_40516() {
            return new Type[]{ADD, MUL, MIN, MAX};
         }
      }
   }

   public static record Spline(net.minecraft.util.math.Spline spline) implements DensityFunction {
      private static final Codec SPLINE_CODEC;
      private static final MapCodec SPLINE_FUNCTION_CODEC;
      public static final CodecHolder CODEC_HOLDER;

      public Spline(net.minecraft.util.math.Spline spline) {
         this.spline = spline;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return (double)this.spline.apply(new SplinePos(pos));
      }

      public double minValue() {
         return (double)this.spline.min();
      }

      public double maxValue() {
         return (double)this.spline.max();
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new Spline(this.spline.apply((densityFunctionWrapper) -> {
            return densityFunctionWrapper.apply(visitor);
         }))));
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public net.minecraft.util.math.Spline spline() {
         return this.spline;
      }

      static {
         SPLINE_CODEC = net.minecraft.util.math.Spline.createCodec(DensityFunctionTypes.Spline.DensityFunctionWrapper.CODEC);
         SPLINE_FUNCTION_CODEC = SPLINE_CODEC.fieldOf("spline").xmap(Spline::new, Spline::spline);
         CODEC_HOLDER = DensityFunctionTypes.holderOf(SPLINE_FUNCTION_CODEC);
      }

      public static record SplinePos(DensityFunction.NoisePos context) {
         public SplinePos(DensityFunction.NoisePos noisePos) {
            this.context = noisePos;
         }

         public DensityFunction.NoisePos context() {
            return this.context;
         }
      }

      public static record DensityFunctionWrapper(RegistryEntry function) implements ToFloatFunction {
         public static final Codec CODEC;

         public DensityFunctionWrapper(RegistryEntry registryEntry) {
            this.function = registryEntry;
         }

         public String toString() {
            Optional optional = this.function.getKey();
            if (optional.isPresent()) {
               RegistryKey registryKey = (RegistryKey)optional.get();
               if (registryKey == DensityFunctions.CONTINENTS_OVERWORLD) {
                  return "continents";
               }

               if (registryKey == DensityFunctions.EROSION_OVERWORLD) {
                  return "erosion";
               }

               if (registryKey == DensityFunctions.RIDGES_OVERWORLD) {
                  return "weirdness";
               }

               if (registryKey == DensityFunctions.RIDGES_FOLDED_OVERWORLD) {
                  return "ridges";
               }
            }

            return "Coordinate[" + String.valueOf(this.function) + "]";
         }

         public float apply(SplinePos splinePos) {
            return (float)((DensityFunction)this.function.value()).sample(splinePos.context());
         }

         public float min() {
            return this.function.hasKeyAndValue() ? (float)((DensityFunction)this.function.value()).minValue() : Float.NEGATIVE_INFINITY;
         }

         public float max() {
            return this.function.hasKeyAndValue() ? (float)((DensityFunction)this.function.value()).maxValue() : Float.POSITIVE_INFINITY;
         }

         public DensityFunctionWrapper apply(DensityFunction.DensityFunctionVisitor visitor) {
            return new DensityFunctionWrapper(new RegistryEntry.Direct(((DensityFunction)this.function.value()).apply(visitor)));
         }

         public RegistryEntry function() {
            return this.function;
         }

         static {
            CODEC = DensityFunction.REGISTRY_ENTRY_CODEC.xmap(DensityFunctionWrapper::new, DensityFunctionWrapper::function);
         }
      }
   }

   private static record Constant(double value) implements DensityFunction.Base {
      final double value;
      static final CodecHolder CODEC_HOLDER;
      static final Constant ZERO;

      Constant(double d) {
         this.value = d;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return this.value;
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         Arrays.fill(densities, this.value);
      }

      public double minValue() {
         return this.value;
      }

      public double maxValue() {
         return this.value;
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public double value() {
         return this.value;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(DensityFunctionTypes.CONSTANT_RANGE, Constant::new, Constant::value);
         ZERO = new Constant(0.0);
      }
   }

   static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.Base {
      private static final MapCodec Y_CLAMPED_GRADIENT_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("from_y").forGetter(YClampedGradient::fromY), Codec.intRange(DimensionType.MIN_HEIGHT * 2, DimensionType.MAX_COLUMN_HEIGHT * 2).fieldOf("to_y").forGetter(YClampedGradient::toY), DensityFunctionTypes.CONSTANT_RANGE.fieldOf("from_value").forGetter(YClampedGradient::fromValue), DensityFunctionTypes.CONSTANT_RANGE.fieldOf("to_value").forGetter(YClampedGradient::toValue)).apply(instance, YClampedGradient::new);
      });
      public static final CodecHolder CODEC_HOLDER;

      YClampedGradient(int i, int j, double d, double e) {
         this.fromY = i;
         this.toY = j;
         this.fromValue = d;
         this.toValue = e;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return MathHelper.clampedMap((double)pos.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
      }

      public double minValue() {
         return Math.min(this.fromValue, this.toValue);
      }

      public double maxValue() {
         return Math.max(this.fromValue, this.toValue);
      }

      public CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }

      public int fromY() {
         return this.fromY;
      }

      public int toY() {
         return this.toY;
      }

      public double fromValue() {
         return this.fromValue;
      }

      public double toValue() {
         return this.toValue;
      }

      static {
         CODEC_HOLDER = DensityFunctionTypes.holderOf(Y_CLAMPED_GRADIENT_CODEC);
      }
   }

   private static record BinaryOperation(BinaryOperationLike.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements BinaryOperationLike {
      BinaryOperation(BinaryOperationLike.Type type, DensityFunction densityFunction, DensityFunction densityFunction2, double d, double e) {
         this.type = type;
         this.argument1 = densityFunction;
         this.argument2 = densityFunction2;
         this.minValue = d;
         this.maxValue = e;
      }

      public double sample(DensityFunction.NoisePos pos) {
         double d = this.argument1.sample(pos);
         double var10000;
         switch (this.type.ordinal()) {
            case 0:
               var10000 = d + this.argument2.sample(pos);
               break;
            case 1:
               var10000 = d == 0.0 ? 0.0 : d * this.argument2.sample(pos);
               break;
            case 2:
               var10000 = d < this.argument2.minValue() ? d : Math.min(d, this.argument2.sample(pos));
               break;
            case 3:
               var10000 = d > this.argument2.maxValue() ? d : Math.max(d, this.argument2.sample(pos));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         this.argument1.fill(densities, applier);
         double e;
         int k;
         double f;
         switch (this.type.ordinal()) {
            case 0:
               double[] ds = new double[densities.length];
               this.argument2.fill(ds, applier);

               for(int i = 0; i < densities.length; ++i) {
                  densities[i] += ds[i];
               }

               return;
            case 1:
               for(int j = 0; j < densities.length; ++j) {
                  double d = densities[j];
                  densities[j] = d == 0.0 ? 0.0 : d * this.argument2.sample(applier.at(j));
               }

               return;
            case 2:
               e = this.argument2.minValue();

               for(k = 0; k < densities.length; ++k) {
                  f = densities[k];
                  densities[k] = f < e ? f : Math.min(f, this.argument2.sample(applier.at(k)));
               }

               return;
            case 3:
               e = this.argument2.maxValue();

               for(k = 0; k < densities.length; ++k) {
                  f = densities[k];
                  densities[k] = f > e ? f : Math.max(f, this.argument2.sample(applier.at(k)));
               }
         }

      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)DensityFunctionTypes.BinaryOperationLike.create(this.type, this.argument1.apply(visitor), this.argument2.apply(visitor)));
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      public BinaryOperationLike.Type type() {
         return this.type;
      }

      public DensityFunction argument1() {
         return this.argument1;
      }

      public DensityFunction argument2() {
         return this.argument2;
      }
   }

   private static record LinearOperation(SpecificType specificType, DensityFunction input, double minValue, double maxValue, double argument) implements Unary, BinaryOperationLike {
      LinearOperation(SpecificType specificType, DensityFunction densityFunction, double d, double e, double f) {
         this.specificType = specificType;
         this.input = densityFunction;
         this.minValue = d;
         this.maxValue = e;
         this.argument = f;
      }

      public BinaryOperationLike.Type type() {
         return this.specificType == DensityFunctionTypes.LinearOperation.SpecificType.MUL ? DensityFunctionTypes.BinaryOperationLike.Type.MUL : DensityFunctionTypes.BinaryOperationLike.Type.ADD;
      }

      public DensityFunction argument1() {
         return DensityFunctionTypes.constant(this.argument);
      }

      public DensityFunction argument2() {
         return this.input;
      }

      public double apply(double density) {
         double var10000;
         switch (this.specificType.ordinal()) {
            case 0:
               var10000 = density * this.argument;
               break;
            case 1:
               var10000 = density + this.argument;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         DensityFunction densityFunction = this.input.apply(visitor);
         double d = densityFunction.minValue();
         double e = densityFunction.maxValue();
         double f;
         double g;
         if (this.specificType == DensityFunctionTypes.LinearOperation.SpecificType.ADD) {
            f = d + this.argument;
            g = e + this.argument;
         } else if (this.argument >= 0.0) {
            f = d * this.argument;
            g = e * this.argument;
         } else {
            f = e * this.argument;
            g = d * this.argument;
         }

         return new LinearOperation(this.specificType, densityFunction, f, g, this.argument);
      }

      public SpecificType specificType() {
         return this.specificType;
      }

      public DensityFunction input() {
         return this.input;
      }

      public double minValue() {
         return this.minValue;
      }

      public double maxValue() {
         return this.maxValue;
      }

      public double argument() {
         return this.argument;
      }

      static enum SpecificType {
         MUL,
         ADD;

         // $FF: synthetic method
         private static SpecificType[] method_40524() {
            return new SpecificType[]{MUL, ADD};
         }
      }
   }

   interface Offset extends DensityFunction {
      DensityFunction.Noise offsetNoise();

      default double minValue() {
         return -this.maxValue();
      }

      default double maxValue() {
         return this.offsetNoise().getMaxValue() * 4.0;
      }

      default double sample(double x, double y, double z) {
         return this.offsetNoise().sample(x * 0.25, y * 0.25, z * 0.25) * 4.0;
      }

      default void fill(double[] densities, DensityFunction.EachApplier applier) {
         applier.fill(densities, this);
      }
   }

   public interface Wrapper extends DensityFunction {
      Wrapping.Type type();

      DensityFunction wrapped();

      default CodecHolder getCodecHolder() {
         return this.type().codec;
      }

      default DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new Wrapping(this.type(), this.wrapped().apply(visitor))));
      }
   }

   @Debug
   public static record RegistryEntryHolder(RegistryEntry function) implements DensityFunction {
      public RegistryEntryHolder(RegistryEntry registryEntry) {
         this.function = registryEntry;
      }

      public double sample(DensityFunction.NoisePos pos) {
         return ((DensityFunction)this.function.value()).sample(pos);
      }

      public void fill(double[] densities, DensityFunction.EachApplier applier) {
         ((DensityFunction)this.function.value()).fill(densities, applier);
      }

      public DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
         return visitor.apply((DensityFunction)(new RegistryEntryHolder(new RegistryEntry.Direct(((DensityFunction)this.function.value()).apply(visitor)))));
      }

      public double minValue() {
         return this.function.hasKeyAndValue() ? ((DensityFunction)this.function.value()).minValue() : Double.NEGATIVE_INFINITY;
      }

      public double maxValue() {
         return this.function.hasKeyAndValue() ? ((DensityFunction)this.function.value()).maxValue() : Double.POSITIVE_INFINITY;
      }

      public CodecHolder getCodecHolder() {
         throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
      }

      public RegistryEntry function() {
         return this.function;
      }
   }

   public interface Beardifying extends DensityFunction.Base {
      CodecHolder CODEC_HOLDER = CodecHolder.of(MapCodec.unit(DensityFunctionTypes.Beardifier.INSTANCE));

      default CodecHolder getCodecHolder() {
         return CODEC_HOLDER;
      }
   }

   interface Unary extends DensityFunction {
      DensityFunction input();

      default double sample(DensityFunction.NoisePos pos) {
         return this.apply(this.input().sample(pos));
      }

      default void fill(double[] densities, DensityFunction.EachApplier applier) {
         this.input().fill(densities, applier);

         for(int i = 0; i < densities.length; ++i) {
            densities[i] = this.apply(densities[i]);
         }

      }

      double apply(double density);
   }

   interface Positional extends DensityFunction {
      DensityFunction input();

      default double sample(DensityFunction.NoisePos pos) {
         return this.apply(pos, this.input().sample(pos));
      }

      default void fill(double[] densities, DensityFunction.EachApplier applier) {
         this.input().fill(densities, applier);

         for(int i = 0; i < densities.length; ++i) {
            densities[i] = this.apply(applier.at(i), densities[i]);
         }

      }

      double apply(DensityFunction.NoisePos pos, double density);
   }
}
