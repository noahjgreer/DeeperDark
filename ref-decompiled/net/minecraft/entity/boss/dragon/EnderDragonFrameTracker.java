package net.minecraft.entity.boss.dragon;

import java.util.Arrays;
import net.minecraft.util.math.MathHelper;

public class EnderDragonFrameTracker {
   public static final int field_52489 = 64;
   private static final int field_52490 = 63;
   private final Frame[] frames = new Frame[64];
   private int currentIndex = -1;

   public EnderDragonFrameTracker() {
      Arrays.fill(this.frames, new Frame(0.0, 0.0F));
   }

   public void copyFrom(EnderDragonFrameTracker other) {
      System.arraycopy(other.frames, 0, this.frames, 0, 64);
      this.currentIndex = other.currentIndex;
   }

   public void tick(double y, float yaw) {
      Frame frame = new Frame(y, yaw);
      if (this.currentIndex < 0) {
         Arrays.fill(this.frames, frame);
      }

      if (++this.currentIndex == 64) {
         this.currentIndex = 0;
      }

      this.frames[this.currentIndex] = frame;
   }

   public Frame getFrame(int age) {
      return this.frames[this.currentIndex - age & 63];
   }

   public Frame getLerpedFrame(int age, float tickProgress) {
      Frame frame = this.getFrame(age);
      Frame frame2 = this.getFrame(age + 1);
      return new Frame(MathHelper.lerp((double)tickProgress, frame2.y, frame.y), MathHelper.lerpAngleDegrees(tickProgress, frame2.yRot, frame.yRot));
   }

   public static record Frame(double y, float yRot) {
      final double y;
      final float yRot;

      public Frame(double d, float f) {
         this.y = d;
         this.yRot = f;
      }

      public double y() {
         return this.y;
      }

      public float yRot() {
         return this.yRot;
      }
   }
}
