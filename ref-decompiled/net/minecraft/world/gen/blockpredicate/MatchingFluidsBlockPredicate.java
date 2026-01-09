package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.Vec3i;

class MatchingFluidsBlockPredicate extends OffsetPredicate {
   private final RegistryEntryList fluids;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return registerOffsetField(instance).and(RegistryCodecs.entryList(RegistryKeys.FLUID).fieldOf("fluids").forGetter((predicate) -> {
         return predicate.fluids;
      })).apply(instance, MatchingFluidsBlockPredicate::new);
   });

   public MatchingFluidsBlockPredicate(Vec3i offset, RegistryEntryList fluids) {
      super(offset);
      this.fluids = fluids;
   }

   protected boolean test(BlockState state) {
      return state.getFluidState().isIn(this.fluids);
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.MATCHING_FLUIDS;
   }
}
