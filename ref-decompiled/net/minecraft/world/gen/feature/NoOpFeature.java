package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class NoOpFeature extends Feature {
   public NoOpFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      return true;
   }
}
