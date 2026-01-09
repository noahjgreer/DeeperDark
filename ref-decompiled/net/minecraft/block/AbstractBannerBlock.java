package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public abstract class AbstractBannerBlock extends BlockWithEntity {
   private final DyeColor color;

   protected AbstractBannerBlock(DyeColor color, AbstractBlock.Settings settings) {
      super(settings);
      this.color = color;
   }

   protected abstract MapCodec getCodec();

   public boolean canMobSpawnInside(BlockState state) {
      return true;
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new BannerBlockEntity(pos, state, this.color);
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      BlockEntity var6 = world.getBlockEntity(pos);
      if (var6 instanceof BannerBlockEntity bannerBlockEntity) {
         return bannerBlockEntity.getPickStack();
      } else {
         return super.getPickStack(world, pos, state, includeData);
      }
   }

   public DyeColor getColor() {
      return this.color;
   }
}
