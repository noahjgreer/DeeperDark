package net.minecraft.particle;

public class ParticleGroup {
   private final int maxCount;
   public static final ParticleGroup SPORE_BLOSSOM_AIR = new ParticleGroup(1000);

   public ParticleGroup(int maxCount) {
      this.maxCount = maxCount;
   }

   public int getMaxCount() {
      return this.maxCount;
   }
}
