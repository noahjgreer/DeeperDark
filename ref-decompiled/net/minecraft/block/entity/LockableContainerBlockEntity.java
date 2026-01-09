package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class LockableContainerBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory, Nameable {
   private ContainerLock lock;
   @Nullable
   private Text customName;

   protected LockableContainerBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
      this.lock = ContainerLock.EMPTY;
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.lock = ContainerLock.read(view);
      this.customName = tryParseCustomName(view, "CustomName");
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      this.lock.write(view);
      view.putNullable("CustomName", TextCodecs.CODEC, this.customName);
   }

   public Text getName() {
      return this.customName != null ? this.customName : this.getContainerName();
   }

   public Text getDisplayName() {
      return this.getName();
   }

   @Nullable
   public Text getCustomName() {
      return this.customName;
   }

   protected abstract Text getContainerName();

   public boolean checkUnlocked(PlayerEntity player) {
      return checkUnlocked(player, this.lock, this.getDisplayName());
   }

   public static boolean checkUnlocked(PlayerEntity player, ContainerLock lock, Text containerName) {
      if (!player.isSpectator() && !lock.canOpen(player.getMainHandStack())) {
         player.sendMessage(Text.translatable("container.isLocked", containerName), true);
         player.playSoundToPlayer(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   protected abstract DefaultedList getHeldStacks();

   protected abstract void setHeldStacks(DefaultedList inventory);

   public boolean isEmpty() {
      java.util.Iterator var1 = this.getHeldStacks().iterator();

      ItemStack itemStack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemStack = (ItemStack)var1.next();
      } while(itemStack.isEmpty());

      return false;
   }

   public ItemStack getStack(int slot) {
      return (ItemStack)this.getHeldStacks().get(slot);
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack itemStack = Inventories.splitStack(this.getHeldStacks(), slot, amount);
      if (!itemStack.isEmpty()) {
         this.markDirty();
      }

      return itemStack;
   }

   public ItemStack removeStack(int slot) {
      return Inventories.removeStack(this.getHeldStacks(), slot);
   }

   public void setStack(int slot, ItemStack stack) {
      this.getHeldStacks().set(slot, stack);
      stack.capCount(this.getMaxCount(stack));
      this.markDirty();
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return Inventory.canPlayerUse(this, player);
   }

   public void clear() {
      this.getHeldStacks().clear();
   }

   @Nullable
   public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      return this.checkUnlocked(playerEntity) ? this.createScreenHandler(i, playerInventory) : null;
   }

   protected abstract ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory);

   protected void readComponents(ComponentsAccess components) {
      super.readComponents(components);
      this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
      this.lock = (ContainerLock)components.getOrDefault(DataComponentTypes.LOCK, ContainerLock.EMPTY);
      ((ContainerComponent)components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)).copyTo(this.getHeldStacks());
   }

   protected void addComponents(ComponentMap.Builder builder) {
      super.addComponents(builder);
      builder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
      if (!this.lock.equals(ContainerLock.EMPTY)) {
         builder.add(DataComponentTypes.LOCK, this.lock);
      }

      builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getHeldStacks()));
   }

   public void removeFromCopiedStackData(WriteView view) {
      view.remove("CustomName");
      view.remove("lock");
      view.remove("Items");
   }
}
