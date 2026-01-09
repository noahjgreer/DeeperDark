package net.minecraft.util.math;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ToFloatFunction;
import org.apache.commons.lang3.mutable.MutableObject;

public interface Spline extends ToFloatFunction {
   @Debug
   String getDebugString();

   Spline apply(Visitor visitor);

   static Codec createCodec(Codec locationFunctionCodec) {
      MutableObject mutableObject = new MutableObject();
      Codec codec = RecordCodecBuilder.create((instance) -> {
         RecordCodecBuilder var10001 = Codec.FLOAT.fieldOf("location").forGetter(Serialized::location);
         Objects.requireNonNull(mutableObject);
         return instance.group(var10001, Codec.lazyInitialized(mutableObject::getValue).fieldOf("value").forGetter(Serialized::value), Codec.FLOAT.fieldOf("derivative").forGetter(Serialized::derivative)).apply(instance, (location, value, derivative) -> {
            record Serialized(float location, Spline value, float derivative) {
               Serialized(float f, Spline spline, float g) {
                  this.location = f;
                  this.value = spline;
                  this.derivative = g;
               }

               public float location() {
                  return this.location;
               }

               public Spline value() {
                  return this.value;
               }

               public float derivative() {
                  return this.derivative;
               }
            }

            return new Serialized(location, value, derivative);
         });
      });
      Codec codec2 = RecordCodecBuilder.create((instance) -> {
         return instance.group(locationFunctionCodec.fieldOf("coordinate").forGetter(Implementation::locationFunction), Codecs.nonEmptyList(codec.listOf()).fieldOf("points").forGetter((spline) -> {
            return IntStream.range(0, spline.locations.length).mapToObj((index) -> {
               return new Serialized(spline.locations()[index], (Spline)spline.values().get(index), spline.derivatives()[index]);
            }).toList();
         })).apply(instance, (locationFunction, splines) -> {
            float[] fs = new float[splines.size()];
            ImmutableList.Builder builder = ImmutableList.builder();
            float[] gs = new float[splines.size()];

            for(int i = 0; i < splines.size(); ++i) {
               Serialized serialized = (Serialized)splines.get(i);
               fs[i] = serialized.location();
               builder.add(serialized.value());
               gs[i] = serialized.derivative();
            }

            return Spline.Implementation.build(locationFunction, fs, builder.build(), gs);
         });
      });
      mutableObject.setValue(Codec.either(Codec.FLOAT, codec2).xmap((either) -> {
         return (Spline)either.map(FixedFloatFunction::new, (spline) -> {
            return spline;
         });
      }, (spline) -> {
         Either var10000;
         if (spline instanceof FixedFloatFunction fixedFloatFunction) {
            var10000 = Either.left(fixedFloatFunction.value());
         } else {
            var10000 = Either.right((Implementation)spline);
         }

         return var10000;
      }));
      return (Codec)mutableObject.getValue();
   }

   static Spline fixedFloatFunction(float value) {
      return new FixedFloatFunction(value);
   }

   static Builder builder(ToFloatFunction locationFunction) {
      return new Builder(locationFunction);
   }

   static Builder builder(ToFloatFunction locationFunction, ToFloatFunction amplifier) {
      return new Builder(locationFunction, amplifier);
   }

   @Debug
   public static record FixedFloatFunction(float value) implements Spline {
      public FixedFloatFunction(float value) {
         this.value = value;
      }

      public float apply(Object x) {
         return this.value;
      }

      public String getDebugString() {
         return String.format(Locale.ROOT, "k=%.3f", this.value);
      }

      public float min() {
         return this.value;
      }

      public float max() {
         return this.value;
      }

      public Spline apply(Visitor visitor) {
         return this;
      }

      public float value() {
         return this.value;
      }
   }

   public static final class Builder {
      private final ToFloatFunction locationFunction;
      private final ToFloatFunction amplifier;
      private final FloatList locations;
      private final List values;
      private final FloatList derivatives;

      protected Builder(ToFloatFunction locationFunction) {
         this(locationFunction, ToFloatFunction.IDENTITY);
      }

      protected Builder(ToFloatFunction locationFunction, ToFloatFunction amplifier) {
         this.locations = new FloatArrayList();
         this.values = Lists.newArrayList();
         this.derivatives = new FloatArrayList();
         this.locationFunction = locationFunction;
         this.amplifier = amplifier;
      }

      public Builder add(float location, float value) {
         return this.addPoint(location, new FixedFloatFunction(this.amplifier.apply(value)), 0.0F);
      }

      public Builder add(float location, float value, float derivative) {
         return this.addPoint(location, new FixedFloatFunction(this.amplifier.apply(value)), derivative);
      }

      public Builder add(float location, Spline value) {
         return this.addPoint(location, value, 0.0F);
      }

      private Builder addPoint(float location, Spline value, float derivative) {
         if (!this.locations.isEmpty() && location <= this.locations.getFloat(this.locations.size() - 1)) {
            throw new IllegalArgumentException("Please register points in ascending order");
         } else {
            this.locations.add(location);
            this.values.add(value);
            this.derivatives.add(derivative);
            return this;
         }
      }

      public Spline build() {
         if (this.locations.isEmpty()) {
            throw new IllegalStateException("No elements added");
         } else {
            return Spline.Implementation.build(this.locationFunction, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
         }
      }
   }

   @Debug
   public static record Implementation(ToFloatFunction locationFunction, float[] locations, List values, float[] derivatives, float min, float max) implements Spline {
      final float[] locations;

      public Implementation(ToFloatFunction toFloatFunction, float[] fs, List list, float[] gs, float f, float g) {
         assertParametersValid(fs, list, gs);
         this.locationFunction = toFloatFunction;
         this.locations = fs;
         this.values = list;
         this.derivatives = gs;
         this.min = f;
         this.max = g;
      }

      static Implementation build(ToFloatFunction locationFunction, float[] locations, List values, float[] derivatives) {
         assertParametersValid(locations, values, derivatives);
         int i = locations.length - 1;
         float f = Float.POSITIVE_INFINITY;
         float g = Float.NEGATIVE_INFINITY;
         float h = locationFunction.min();
         float j = locationFunction.max();
         float k;
         float l;
         if (h < locations[0]) {
            k = sampleOutsideRange(h, locations, ((Spline)values.get(0)).min(), derivatives, 0);
            l = sampleOutsideRange(h, locations, ((Spline)values.get(0)).max(), derivatives, 0);
            f = Math.min(f, Math.min(k, l));
            g = Math.max(g, Math.max(k, l));
         }

         if (j > locations[i]) {
            k = sampleOutsideRange(j, locations, ((Spline)values.get(i)).min(), derivatives, i);
            l = sampleOutsideRange(j, locations, ((Spline)values.get(i)).max(), derivatives, i);
            f = Math.min(f, Math.min(k, l));
            g = Math.max(g, Math.max(k, l));
         }

         Spline spline;
         for(Iterator var31 = values.iterator(); var31.hasNext(); g = Math.max(g, spline.max())) {
            spline = (Spline)var31.next();
            f = Math.min(f, spline.min());
         }

         for(int m = 0; m < i; ++m) {
            l = locations[m];
            float n = locations[m + 1];
            float o = n - l;
            Spline spline2 = (Spline)values.get(m);
            Spline spline3 = (Spline)values.get(m + 1);
            float p = spline2.min();
            float q = spline2.max();
            float r = spline3.min();
            float s = spline3.max();
            float t = derivatives[m];
            float u = derivatives[m + 1];
            if (t != 0.0F || u != 0.0F) {
               float v = t * o;
               float w = u * o;
               float x = Math.min(p, r);
               float y = Math.max(q, s);
               float z = v - s + p;
               float aa = v - r + q;
               float ab = -w + r - q;
               float ac = -w + s - p;
               float ad = Math.min(z, ab);
               float ae = Math.max(aa, ac);
               f = Math.min(f, x + 0.25F * ad);
               g = Math.max(g, y + 0.25F * ae);
            }
         }

         return new Implementation(locationFunction, locations, values, derivatives, f, g);
      }

      private static float sampleOutsideRange(float point, float[] locations, float value, float[] derivatives, int i) {
         float f = derivatives[i];
         return f == 0.0F ? value : value + f * (point - locations[i]);
      }

      private static void assertParametersValid(float[] locations, List values, float[] derivatives) {
         if (locations.length == values.size() && locations.length == derivatives.length) {
            if (locations.length == 0) {
               throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
            }
         } else {
            throw new IllegalArgumentException("All lengths must be equal, got: " + locations.length + " " + values.size() + " " + derivatives.length);
         }
      }

      public float apply(Object x) {
         float f = this.locationFunction.apply(x);
         int i = findRangeForLocation(this.locations, f);
         int j = this.locations.length - 1;
         if (i < 0) {
            return sampleOutsideRange(f, this.locations, ((Spline)this.values.get(0)).apply(x), this.derivatives, 0);
         } else if (i == j) {
            return sampleOutsideRange(f, this.locations, ((Spline)this.values.get(j)).apply(x), this.derivatives, j);
         } else {
            float g = this.locations[i];
            float h = this.locations[i + 1];
            float k = (f - g) / (h - g);
            ToFloatFunction toFloatFunction = (ToFloatFunction)this.values.get(i);
            ToFloatFunction toFloatFunction2 = (ToFloatFunction)this.values.get(i + 1);
            float l = this.derivatives[i];
            float m = this.derivatives[i + 1];
            float n = toFloatFunction.apply(x);
            float o = toFloatFunction2.apply(x);
            float p = l * (h - g) - (o - n);
            float q = -m * (h - g) + (o - n);
            float r = MathHelper.lerp(k, n, o) + k * (1.0F - k) * MathHelper.lerp(k, p, q);
            return r;
         }
      }

      private static int findRangeForLocation(float[] locations, float x) {
         return MathHelper.binarySearch(0, locations.length, (i) -> {
            return x < locations[i];
         }) - 1;
      }

      @VisibleForTesting
      public String getDebugString() {
         String var10000 = String.valueOf(this.locationFunction);
         return "Spline{coordinate=" + var10000 + ", locations=" + this.format(this.locations) + ", derivatives=" + this.format(this.derivatives) + ", values=" + (String)this.values.stream().map(Spline::getDebugString).collect(Collectors.joining(", ", "[", "]")) + "}";
      }

      private String format(float[] values) {
         Stream var10000 = IntStream.range(0, values.length).mapToDouble((index) -> {
            return (double)values[index];
         }).mapToObj((value) -> {
            return String.format(Locale.ROOT, "%.3f", value);
         });
         return "[" + (String)var10000.collect(Collectors.joining(", ")) + "]";
      }

      public Spline apply(Visitor visitor) {
         return build((ToFloatFunction)visitor.visit(this.locationFunction), this.locations, this.values().stream().map((value) -> {
            return value.apply(visitor);
         }).toList(), this.derivatives);
      }

      public ToFloatFunction locationFunction() {
         return this.locationFunction;
      }

      public float[] locations() {
         return this.locations;
      }

      public List values() {
         return this.values;
      }

      public float[] derivatives() {
         return this.derivatives;
      }

      public float min() {
         return this.min;
      }

      public float max() {
         return this.max;
      }
   }

   public interface Visitor {
      Object visit(Object value);
   }
}
