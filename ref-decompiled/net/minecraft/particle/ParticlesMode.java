package net.minecraft.particle;

import java.util.function.IntFunction;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public enum ParticlesMode implements TranslatableOption {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(ParticlesMode::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.WRAP);
   private final int id;
   private final String translationKey;

   private ParticlesMode(final int id, final String translationKey) {
      this.id = id;
      this.translationKey = translationKey;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public int getId() {
      return this.id;
   }

   public static ParticlesMode byId(int id) {
      return (ParticlesMode)BY_ID.apply(id);
   }

   // $FF: synthetic method
   private static ParticlesMode[] method_36865() {
      return new ParticlesMode[]{ALL, DECREASED, MINIMAL};
   }
}
