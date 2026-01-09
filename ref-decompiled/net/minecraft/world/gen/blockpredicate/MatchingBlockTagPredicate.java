package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3i;

public class MatchingBlockTagPredicate extends OffsetPredicate {
   final TagKey tag;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return registerOffsetField(instance).and(TagKey.unprefixedCodec(RegistryKeys.BLOCK).fieldOf("tag").forGetter((predicate) -> {
         return predicate.tag;
      })).apply(instance, MatchingBlockTagPredicate::new);
   });

   protected MatchingBlockTagPredicate(Vec3i offset, TagKey tag) {
      super(offset);
      this.tag = tag;
   }

   protected boolean test(BlockState state) {
      return state.isIn(this.tag);
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.MATCHING_BLOCK_TAG;
   }
}
