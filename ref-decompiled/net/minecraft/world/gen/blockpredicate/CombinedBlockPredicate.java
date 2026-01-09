package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;

abstract class CombinedBlockPredicate implements BlockPredicate {
   protected final List predicates;

   protected CombinedBlockPredicate(List predicates) {
      this.predicates = predicates;
   }

   public static MapCodec buildCodec(Function combiner) {
      return RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(BlockPredicate.BASE_CODEC.listOf().fieldOf("predicates").forGetter((predicate) -> {
            return predicate.predicates;
         })).apply(instance, combiner);
      });
   }
}
