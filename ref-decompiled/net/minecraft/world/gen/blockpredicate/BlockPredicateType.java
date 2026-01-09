package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface BlockPredicateType {
   BlockPredicateType MATCHING_BLOCKS = register("matching_blocks", MatchingBlocksBlockPredicate.CODEC);
   BlockPredicateType MATCHING_BLOCK_TAG = register("matching_block_tag", MatchingBlockTagPredicate.CODEC);
   BlockPredicateType MATCHING_FLUIDS = register("matching_fluids", MatchingFluidsBlockPredicate.CODEC);
   BlockPredicateType HAS_STURDY_FACE = register("has_sturdy_face", HasSturdyFacePredicate.CODEC);
   BlockPredicateType SOLID = register("solid", SolidBlockPredicate.CODEC);
   BlockPredicateType REPLACEABLE = register("replaceable", ReplaceableBlockPredicate.CODEC);
   BlockPredicateType WOULD_SURVIVE = register("would_survive", WouldSurviveBlockPredicate.CODEC);
   BlockPredicateType INSIDE_WORLD_BOUNDS = register("inside_world_bounds", InsideWorldBoundsBlockPredicate.CODEC);
   BlockPredicateType ANY_OF = register("any_of", AnyOfBlockPredicate.CODEC);
   BlockPredicateType ALL_OF = register("all_of", AllOfBlockPredicate.CODEC);
   BlockPredicateType NOT = register("not", NotBlockPredicate.CODEC);
   BlockPredicateType TRUE = register("true", AlwaysTrueBlockPredicate.CODEC);
   BlockPredicateType UNOBSTRUCTED = register("unobstructed", UnobstructedBlockPredicate.CODEC);

   MapCodec codec();

   private static BlockPredicateType register(String id, MapCodec codec) {
      return (BlockPredicateType)Registry.register(Registries.BLOCK_PREDICATE_TYPE, (String)id, () -> {
         return codec;
      });
   }
}
