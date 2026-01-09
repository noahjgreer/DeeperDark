package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BeaconBlock extends BlockWithEntity implements Stainable {
   public static final MapCodec CODEC = createCodec(BeaconBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public BeaconBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public DyeColor getColor() {
      return DyeColor.WHITE;
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new BeaconBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return validateTicker(type, BlockEntityType.BEACON, BeaconBlockEntity::tick);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         BlockEntity var7 = world.getBlockEntity(pos);
         if (var7 instanceof BeaconBlockEntity) {
            BeaconBlockEntity beaconBlockEntity = (BeaconBlockEntity)var7;
            player.openHandledScreen(beaconBlockEntity);
            player.incrementStat(Stats.INTERACT_WITH_BEACON);
         }
      }

      return ActionResult.SUCCESS;
   }
}
