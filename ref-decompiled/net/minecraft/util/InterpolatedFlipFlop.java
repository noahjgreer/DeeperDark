package net.minecraft.util;

import net.minecraft.util.math.MathHelper;

public class InterpolatedFlipFlop {
   private final int frames;
   private final SmoothingFunction smoothingFunction;
   private int current;
   private int previous;

   public InterpolatedFlipFlop(int frames, SmoothingFunction smoothingFunction) {
      this.frames = frames;
      this.smoothingFunction = smoothingFunction;
   }

   public InterpolatedFlipFlop(int frames) {
      this(frames, (tickProgress) -> {
         return tickProgress;
      });
   }

   public void tick(boolean active) {
      this.previous = this.current;
      if (active) {
         if (this.current < this.frames) {
            ++this.current;
         }
      } else if (this.current > 0) {
         --this.current;
      }

   }

   public float getValue(float tickProgress) {
      float f = MathHelper.lerp(tickProgress, (float)this.previous, (float)this.current) / (float)this.frames;
      return this.smoothingFunction.apply(f);
   }

   public interface SmoothingFunction {
      float apply(float tickProgress);
   }
}
