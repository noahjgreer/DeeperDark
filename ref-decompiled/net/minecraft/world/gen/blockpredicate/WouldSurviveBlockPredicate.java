package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

public class WouldSurviveBlockPredicate implements BlockPredicate {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((predicate) -> {
         return predicate.offset;
      }), BlockState.CODEC.fieldOf("state").forGetter((predicate) -> {
         return predicate.state;
      })).apply(instance, WouldSurviveBlockPredicate::new);
   });
   private final Vec3i offset;
   private final BlockState state;

   protected WouldSurviveBlockPredicate(Vec3i offset, BlockState state) {
      this.offset = offset;
      this.state = state;
   }

   public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
      return this.state.canPlaceAt(structureWorldAccess, blockPos.add(this.offset));
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.WOULD_SURVIVE;
   }

   // $FF: synthetic method
   public boolean test(final Object world, final Object pos) {
      return this.test((StructureWorldAccess)world, (BlockPos)pos);
   }
}
