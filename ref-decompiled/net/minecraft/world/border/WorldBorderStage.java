package net.minecraft.world.border;

public enum WorldBorderStage {
   GROWING(4259712),
   SHRINKING(16724016),
   STATIONARY(2138367);

   private final int color;

   private WorldBorderStage(final int color) {
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   // $FF: synthetic method
   private static WorldBorderStage[] method_36740() {
      return new WorldBorderStage[]{GROWING, SHRINKING, STATIONARY};
   }
}
