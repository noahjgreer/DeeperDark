package net.minecraft.world.gen.heightprovider;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;
import org.slf4j.Logger;

public class UniformHeightProvider extends HeightProvider {
   public static final MapCodec UNIFORM_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(YOffset.OFFSET_CODEC.fieldOf("min_inclusive").forGetter((provider) -> {
         return provider.minOffset;
      }), YOffset.OFFSET_CODEC.fieldOf("max_inclusive").forGetter((provider) -> {
         return provider.maxOffset;
      })).apply(instance, UniformHeightProvider::new);
   });
   private static final Logger LOGGER = LogUtils.getLogger();
   private final YOffset minOffset;
   private final YOffset maxOffset;
   private final LongSet warnedEmptyHeightRanges = new LongOpenHashSet();

   private UniformHeightProvider(YOffset minOffset, YOffset maxOffset) {
      this.minOffset = minOffset;
      this.maxOffset = maxOffset;
   }

   public static UniformHeightProvider create(YOffset minOffset, YOffset maxOffset) {
      return new UniformHeightProvider(minOffset, maxOffset);
   }

   public int get(Random random, HeightContext context) {
      int i = this.minOffset.getY(context);
      int j = this.maxOffset.getY(context);
      if (i > j) {
         if (this.warnedEmptyHeightRanges.add((long)i << 32 | (long)j)) {
            LOGGER.warn("Empty height range: {}", this);
         }

         return i;
      } else {
         return MathHelper.nextBetween(random, i, j);
      }
   }

   public HeightProviderType getType() {
      return HeightProviderType.UNIFORM;
   }

   public String toString() {
      String var10000 = String.valueOf(this.minOffset);
      return "[" + var10000 + "-" + String.valueOf(this.maxOffset) + "]";
   }
}
