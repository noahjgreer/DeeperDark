package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TestInstanceBlock extends BlockWithEntity implements OperatorBlock {
   public static final MapCodec CODEC = createCodec(TestInstanceBlock::new);

   public TestInstanceBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new TestInstanceBlockEntity(pos, state);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof TestInstanceBlockEntity testInstanceBlockEntity) {
         if (!player.isCreativeLevelTwoOp()) {
            return ActionResult.PASS;
         } else {
            if (player.getWorld().isClient) {
               player.openTestInstanceBlockScreen(testInstanceBlockEntity);
            }

            return ActionResult.SUCCESS;
         }
      } else {
         return ActionResult.PASS;
      }
   }

   protected MapCodec getCodec() {
      return CODEC;
   }
}
