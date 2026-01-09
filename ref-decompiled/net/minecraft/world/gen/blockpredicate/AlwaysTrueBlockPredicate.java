package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class AlwaysTrueBlockPredicate implements BlockPredicate {
   public static AlwaysTrueBlockPredicate instance = new AlwaysTrueBlockPredicate();
   public static final MapCodec CODEC = MapCodec.unit(() -> {
      return instance;
   });

   private AlwaysTrueBlockPredicate() {
   }

   public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
      return true;
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.TRUE;
   }

   // $FF: synthetic method
   public boolean test(final Object world, final Object pos) {
      return this.test((StructureWorldAccess)world, (BlockPos)pos);
   }
}
