package net.minecraft.util.profiler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.ToDoubleFunction;
import org.jetbrains.annotations.Nullable;

public class Sampler {
   private final String name;
   private final SampleType type;
   private final DoubleSupplier retriever;
   private final ByteBuf ticksBuffer;
   private final ByteBuf valueBuffer;
   private volatile boolean active;
   @Nullable
   private final Runnable startAction;
   @Nullable
   final DeviationChecker deviationChecker;
   private double currentSample;

   protected Sampler(String name, SampleType type, DoubleSupplier retriever, @Nullable Runnable startAction, @Nullable DeviationChecker deviationChecker) {
      this.name = name;
      this.type = type;
      this.startAction = startAction;
      this.retriever = retriever;
      this.deviationChecker = deviationChecker;
      this.valueBuffer = ByteBufAllocator.DEFAULT.buffer();
      this.ticksBuffer = ByteBufAllocator.DEFAULT.buffer();
      this.active = true;
   }

   public static Sampler create(String name, SampleType type, DoubleSupplier retriever) {
      return new Sampler(name, type, retriever, (Runnable)null, (DeviationChecker)null);
   }

   public static Sampler create(String name, SampleType type, Object context, ToDoubleFunction retriever) {
      return builder(name, type, retriever, context).build();
   }

   public static Builder builder(String name, SampleType type, ToDoubleFunction retriever, Object context) {
      return new Builder(name, type, retriever, context);
   }

   public void start() {
      if (!this.active) {
         throw new IllegalStateException("Not running");
      } else {
         if (this.startAction != null) {
            this.startAction.run();
         }

      }
   }

   public void sample(int tick) {
      this.ensureActive();
      this.currentSample = this.retriever.getAsDouble();
      this.valueBuffer.writeDouble(this.currentSample);
      this.ticksBuffer.writeInt(tick);
   }

   public void stop() {
      this.ensureActive();
      this.valueBuffer.release();
      this.ticksBuffer.release();
      this.active = false;
   }

   private void ensureActive() {
      if (!this.active) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Sampler for metric %s not started!", this.name));
      }
   }

   DoubleSupplier getRetriever() {
      return this.retriever;
   }

   public String getName() {
      return this.name;
   }

   public SampleType getType() {
      return this.type;
   }

   public Data collectData() {
      Int2DoubleMap int2DoubleMap = new Int2DoubleOpenHashMap();
      int i = Integer.MIN_VALUE;

      int j;
      int k;
      for(j = Integer.MIN_VALUE; this.valueBuffer.isReadable(8); j = k) {
         k = this.ticksBuffer.readInt();
         if (i == Integer.MIN_VALUE) {
            i = k;
         }

         int2DoubleMap.put(k, this.valueBuffer.readDouble());
      }

      return new Data(i, j, int2DoubleMap);
   }

   public boolean hasDeviated() {
      return this.deviationChecker != null && this.deviationChecker.check(this.currentSample);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Sampler sampler = (Sampler)o;
         return this.name.equals(sampler.name) && this.type.equals(sampler.type);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public interface DeviationChecker {
      boolean check(double value);
   }

   public static class Builder {
      private final String name;
      private final SampleType type;
      private final DoubleSupplier timeGetter;
      private final Object context;
      @Nullable
      private Runnable startAction;
      @Nullable
      private DeviationChecker deviationChecker;

      public Builder(String name, SampleType type, ToDoubleFunction timeFunction, Object context) {
         this.name = name;
         this.type = type;
         this.timeGetter = () -> {
            return timeFunction.applyAsDouble(context);
         };
         this.context = context;
      }

      public Builder startAction(Consumer action) {
         this.startAction = () -> {
            action.accept(this.context);
         };
         return this;
      }

      public Builder deviationChecker(DeviationChecker deviationChecker) {
         this.deviationChecker = deviationChecker;
         return this;
      }

      public Sampler build() {
         return new Sampler(this.name, this.type, this.timeGetter, this.startAction, this.deviationChecker);
      }
   }

   public static class Data {
      private final Int2DoubleMap values;
      private final int startTick;
      private final int endTick;

      public Data(int startTick, int endTick, Int2DoubleMap values) {
         this.startTick = startTick;
         this.endTick = endTick;
         this.values = values;
      }

      public double getValue(int tick) {
         return this.values.get(tick);
      }

      public int getStartTick() {
         return this.startTick;
      }

      public int getEndTick() {
         return this.endTick;
      }
   }

   public static class RatioDeviationChecker implements DeviationChecker {
      private final float threshold;
      private double lastValue = Double.MIN_VALUE;

      public RatioDeviationChecker(float threshold) {
         this.threshold = threshold;
      }

      public boolean check(double value) {
         boolean bl;
         if (this.lastValue != Double.MIN_VALUE && !(value <= this.lastValue)) {
            bl = (value - this.lastValue) / this.lastValue >= (double)this.threshold;
         } else {
            bl = false;
         }

         this.lastValue = value;
         return bl;
      }
   }
}
