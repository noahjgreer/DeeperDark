package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.Vec3i;

class MatchingBlocksBlockPredicate extends OffsetPredicate {
   private final RegistryEntryList blocks;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return registerOffsetField(instance).and(RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("blocks").forGetter((predicate) -> {
         return predicate.blocks;
      })).apply(instance, MatchingBlocksBlockPredicate::new);
   });

   public MatchingBlocksBlockPredicate(Vec3i offset, RegistryEntryList blocks) {
      super(offset);
      this.blocks = blocks;
   }

   protected boolean test(BlockState state) {
      return state.isIn(this.blocks);
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.MATCHING_BLOCKS;
   }
}
