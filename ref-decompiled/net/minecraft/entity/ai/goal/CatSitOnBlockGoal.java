package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class CatSitOnBlockGoal extends MoveToTargetPosGoal {
   private final CatEntity cat;

   public CatSitOnBlockGoal(CatEntity cat, double speed) {
      super(cat, speed, 8);
      this.cat = cat;
   }

   public boolean canStart() {
      return this.cat.isTamed() && !this.cat.isSitting() && super.canStart();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   public void stop() {
      super.stop();
      this.cat.setInSittingPose(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(this.hasReached());
   }

   protected boolean isTargetPos(WorldView world, BlockPos pos) {
      if (!world.isAir(pos.up())) {
         return false;
      } else {
         BlockState blockState = world.getBlockState(pos);
         if (blockState.isOf(Blocks.CHEST)) {
            return ChestBlockEntity.getPlayersLookingInChestCount(world, pos) < 1;
         } else {
            return blockState.isOf(Blocks.FURNACE) && (Boolean)blockState.get(FurnaceBlock.LIT) ? true : blockState.isIn(BlockTags.BEDS, (state) -> {
               return (Boolean)state.getOrEmpty(BedBlock.PART).map((part) -> {
                  return part != BedPart.HEAD;
               }).orElse(true);
            });
         }
      }
   }
}
