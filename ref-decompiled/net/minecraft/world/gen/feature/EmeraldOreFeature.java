package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EmeraldOreFeature extends Feature {
   public EmeraldOreFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      StructureWorldAccess structureWorldAccess = context.getWorld();
      BlockPos blockPos = context.getOrigin();
      EmeraldOreFeatureConfig emeraldOreFeatureConfig = (EmeraldOreFeatureConfig)context.getConfig();
      Iterator var5 = emeraldOreFeatureConfig.targets.iterator();

      while(var5.hasNext()) {
         OreFeatureConfig.Target target = (OreFeatureConfig.Target)var5.next();
         if (target.target.test(structureWorldAccess.getBlockState(blockPos), context.getRandom())) {
            structureWorldAccess.setBlockState(blockPos, target.state, 2);
            break;
         }
      }

      return true;
   }
}
