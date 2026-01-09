package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.YOffset;

public class ConstantHeightProvider extends HeightProvider {
   public static final ConstantHeightProvider ZERO = new ConstantHeightProvider(YOffset.fixed(0));
   public static final MapCodec CONSTANT_CODEC;
   private final YOffset offset;

   public static ConstantHeightProvider create(YOffset offset) {
      return new ConstantHeightProvider(offset);
   }

   private ConstantHeightProvider(YOffset offset) {
      this.offset = offset;
   }

   public YOffset getOffset() {
      return this.offset;
   }

   public int get(Random random, HeightContext context) {
      return this.offset.getY(context);
   }

   public HeightProviderType getType() {
      return HeightProviderType.CONSTANT;
   }

   public String toString() {
      return this.offset.toString();
   }

   static {
      CONSTANT_CODEC = YOffset.OFFSET_CODEC.fieldOf("value").xmap(ConstantHeightProvider::new, ConstantHeightProvider::getOffset);
   }
}
