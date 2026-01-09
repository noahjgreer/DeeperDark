package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

public class HasSturdyFacePredicate implements BlockPredicate {
   private final Vec3i offset;
   private final Direction face;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((predicate) -> {
         return predicate.offset;
      }), Direction.CODEC.fieldOf("direction").forGetter((predicate) -> {
         return predicate.face;
      })).apply(instance, HasSturdyFacePredicate::new);
   });

   public HasSturdyFacePredicate(Vec3i offset, Direction face) {
      this.offset = offset;
      this.face = face;
   }

   public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
      BlockPos blockPos2 = blockPos.add(this.offset);
      return structureWorldAccess.getBlockState(blockPos2).isSideSolidFullSquare(structureWorldAccess, blockPos2, this.face);
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.HAS_STURDY_FACE;
   }

   // $FF: synthetic method
   public boolean test(final Object world, final Object pos) {
      return this.test((StructureWorldAccess)world, (BlockPos)pos);
   }
}
