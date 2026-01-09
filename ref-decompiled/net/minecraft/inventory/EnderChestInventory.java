package net.minecraft.inventory;

import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class EnderChestInventory extends SimpleInventory {
   @Nullable
   private EnderChestBlockEntity activeBlockEntity;

   public EnderChestInventory() {
      super(27);
   }

   public void setActiveBlockEntity(EnderChestBlockEntity blockEntity) {
      this.activeBlockEntity = blockEntity;
   }

   public boolean isActiveBlockEntity(EnderChestBlockEntity blockEntity) {
      return this.activeBlockEntity == blockEntity;
   }

   public void readData(ReadView.TypedListReadView list) {
      for(int i = 0; i < this.size(); ++i) {
         this.setStack(i, ItemStack.EMPTY);
      }

      java.util.Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         StackWithSlot stackWithSlot = (StackWithSlot)var4.next();
         if (stackWithSlot.isValidSlot(this.size())) {
            this.setStack(stackWithSlot.slot(), stackWithSlot.stack());
         }
      }

   }

   public void writeData(WriteView.ListAppender list) {
      for(int i = 0; i < this.size(); ++i) {
         ItemStack itemStack = this.getStack(i);
         if (!itemStack.isEmpty()) {
            list.add(new StackWithSlot(i, itemStack));
         }
      }

   }

   public boolean canPlayerUse(PlayerEntity player) {
      return this.activeBlockEntity != null && !this.activeBlockEntity.canPlayerUse(player) ? false : super.canPlayerUse(player);
   }

   public void onOpen(PlayerEntity player) {
      if (this.activeBlockEntity != null) {
         this.activeBlockEntity.onOpen(player);
      }

      super.onOpen(player);
   }

   public void onClose(PlayerEntity player) {
      if (this.activeBlockEntity != null) {
         this.activeBlockEntity.onClose(player);
      }

      super.onClose(player);
      this.activeBlockEntity = null;
   }
}
