package net.minecraft.world.gen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.StructureWorldAccess;

record UnobstructedBlockPredicate(Vec3i offset) implements BlockPredicate {
   public static MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(UnobstructedBlockPredicate::offset)).apply(instance, UnobstructedBlockPredicate::new);
   });

   UnobstructedBlockPredicate(Vec3i vec3i) {
      this.offset = vec3i;
   }

   public BlockPredicateType getType() {
      return BlockPredicateType.UNOBSTRUCTED;
   }

   public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
      return structureWorldAccess.doesNotIntersectEntities((Entity)null, VoxelShapes.fullCube().offset((Vec3i)blockPos));
   }

   public Vec3i offset() {
      return this.offset;
   }

   // $FF: synthetic method
   public boolean test(final Object world, final Object pos) {
      return this.test((StructureWorldAccess)world, (BlockPos)pos);
   }
}
