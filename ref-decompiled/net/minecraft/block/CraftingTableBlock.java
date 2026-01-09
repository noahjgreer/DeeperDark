package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableBlock extends Block {
   public static final MapCodec CODEC = createCodec(CraftingTableBlock::new);
   private static final Text TITLE = Text.translatable("container.crafting");

   public MapCodec getCodec() {
      return CODEC;
   }

   public CraftingTableBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!world.isClient) {
         player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
         player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
      }

      return ActionResult.SUCCESS;
   }

   protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
         return new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
      }, TITLE);
   }
}
