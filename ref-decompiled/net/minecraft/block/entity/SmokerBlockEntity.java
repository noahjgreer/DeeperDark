package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmokerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class SmokerBlockEntity extends AbstractFurnaceBlockEntity {
   public SmokerBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.SMOKER, pos, state, RecipeType.SMOKING);
   }

   protected Text getContainerName() {
      return Text.translatable("container.smoker");
   }

   protected int getFuelTime(FuelRegistry fuelRegistry, ItemStack stack) {
      return super.getFuelTime(fuelRegistry, stack) / 2;
   }

   protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
      return new SmokerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
   }
}
