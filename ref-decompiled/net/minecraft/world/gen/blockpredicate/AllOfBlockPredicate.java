package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class AllOfBlockPredicate extends CombinedBlockPredicate {
   public static final MapCodec CODEC = buildCodec(AllOfBlockPredicate::new);

   public AllOfBlockPredicate(List list) {
      super(list);
   }

   public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
      Iterator var3 = this.predicates.iterator();

      BlockPredicate blockPredicate;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         blockPredicate = (BlockPredicate)var3.next();
      } while(blockPredicate.test(structureWorldAccess, blockPos));

      return false;
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.ALL_OF;
   }

   // $FF: synthetic method
   public boolean test(final Object world, final Object pos) {
      return this.test((StructureWorldAccess)world, (BlockPos)pos);
   }
}
