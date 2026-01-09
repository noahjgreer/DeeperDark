package net.minecraft.world.gen.heightprovider;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import org.slf4j.Logger;

public class TrapezoidHeightProvider extends HeightProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter((provider) -> {
         return provider.minOffset;
      }), YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter((provider) -> {
         return provider.maxOffset;
      }), Codec.INT.optionalFieldOf("plateau", 0).forGetter((trapezoidHeightProvider) -> {
         return trapezoidHeightProvider.plateau;
      })).apply(instance, TrapezoidHeightProvider::new);
   });
   private static final Logger LOGGER = LogUtils.getLogger();
   private final YOffset minOffset;
   private final YOffset maxOffset;
   private final int plateau;

   private TrapezoidHeightProvider(YOffset minOffset, YOffset maxOffset, int plateau) {
      this.minOffset = minOffset;
      this.maxOffset = maxOffset;
      this.plateau = plateau;
   }

   public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset, int plateau) {
      return new TrapezoidHeightProvider(minOffset, maxOffset, plateau);
   }

   public static TrapezoidHeightProvider create(YOffset minOffset, YOffset maxOffset) {
      return create(minOffset, maxOffset, 0);
   }

   public int get(Random random, HeightContext context) {
      int i = this.minOffset.getY(context);
      int j = this.maxOffset.getY(context);
      if (i > j) {
         LOGGER.warn("Empty height range: {}", this);
         return i;
      } else {
         int k = j - i;
         if (this.plateau >= k) {
            return MathHelper.nextBetween(random, i, j);
         } else {
            int l = (k - this.plateau) / 2;
            int m = k - l;
            return i + MathHelper.nextBetween(random, 0, m) + MathHelper.nextBetween(random, 0, l);
         }
      }
   }

   public HeightProviderType getType() {
      return HeightProviderType.TRAPEZOID;
   }

   public String toString() {
      if (this.plateau == 0) {
         String var1 = String.valueOf(this.minOffset);
         return "triangle (" + var1 + "-" + String.valueOf(this.maxOffset) + ")";
      } else {
         int var10000 = this.plateau;
         return "trapezoid(" + var10000 + ") in [" + String.valueOf(this.minOffset) + "-" + String.valueOf(this.maxOffset) + "]";
      }
   }
}
