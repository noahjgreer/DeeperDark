package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

public class InsideWorldBoundsBlockPredicate implements BlockPredicate {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("offset", BlockPos.ORIGIN).forGetter((predicate) -> {
         return predicate.offset;
      })).apply(instance, InsideWorldBoundsBlockPredicate::new);
   });
   private final Vec3i offset;

   public InsideWorldBoundsBlockPredicate(Vec3i offset) {
      this.offset = offset;
   }

   public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
      return !structureWorldAccess.isOutOfHeightLimit(blockPos.add(this.offset));
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.INSIDE_WORLD_BOUNDS;
   }

   // $FF: synthetic method
   public boolean test(final Object world, final Object pos) {
      return this.test((StructureWorldAccess)world, (BlockPos)pos);
   }
}
