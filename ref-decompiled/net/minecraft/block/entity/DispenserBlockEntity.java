package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class DispenserBlockEntity extends LootableContainerBlockEntity {
   public static final int INVENTORY_SIZE = 9;
   private DefaultedList inventory;

   protected DispenserBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
      this.inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
   }

   public DispenserBlockEntity(BlockPos pos, BlockState state) {
      this(BlockEntityType.DISPENSER, pos, state);
   }

   public int size() {
      return 9;
   }

   public int chooseNonEmptySlot(Random random) {
      this.generateLoot((PlayerEntity)null);
      int i = -1;
      int j = 1;

      for(int k = 0; k < this.inventory.size(); ++k) {
         if (!((ItemStack)this.inventory.get(k)).isEmpty() && random.nextInt(j++) == 0) {
            i = k;
         }
      }

      return i;
   }

   public ItemStack addToFirstFreeSlot(ItemStack stack) {
      int i = this.getMaxCount(stack);

      for(int j = 0; j < this.inventory.size(); ++j) {
         ItemStack itemStack = (ItemStack)this.inventory.get(j);
         if (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
            int k = Math.min(stack.getCount(), i - itemStack.getCount());
            if (k > 0) {
               if (itemStack.isEmpty()) {
                  this.setStack(j, stack.split(k));
               } else {
                  stack.decrement(k);
                  itemStack.increment(k);
               }
            }

            if (stack.isEmpty()) {
               break;
            }
         }
      }

      return stack;
   }

   protected Text getContainerName() {
      return Text.translatable("container.dispenser");
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
      if (!this.readLootTable(view)) {
         Inventories.readData(view, this.inventory);
      }

   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      if (!this.writeLootTable(view)) {
         Inventories.writeData(view, this.inventory);
      }

   }

   protected DefaultedList getHeldStacks() {
      return this.inventory;
   }

   protected void setHeldStacks(DefaultedList inventory) {
      this.inventory = inventory;
   }

   protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
      return new Generic3x3ContainerScreenHandler(syncId, playerInventory, this);
   }
}
