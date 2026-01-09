package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

class NotBlockPredicate implements BlockPredicate {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockPredicate.BASE_CODEC.fieldOf("predicate").forGetter((predicate) -> {
         return predicate.predicate;
      })).apply(instance, NotBlockPredicate::new);
   });
   private final BlockPredicate predicate;

   public NotBlockPredicate(BlockPredicate predicate) {
      this.predicate = predicate;
   }

   public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
      return !this.predicate.test(structureWorldAccess, blockPos);
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.NOT;
   }

   // $FF: synthetic method
   public boolean test(final Object world, final Object pos) {
      return this.test((StructureWorldAccess)world, (BlockPos)pos);
   }
}
