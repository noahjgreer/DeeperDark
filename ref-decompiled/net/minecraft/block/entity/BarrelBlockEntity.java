package net.minecraft.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BarrelBlockEntity extends LootableContainerBlockEntity {
   private DefaultedList inventory;
   private final ViewerCountManager stateManager;

   public BarrelBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.BARREL, pos, state);
      this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
      this.stateManager = new ViewerCountManager() {
         protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            BarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
            BarrelBlockEntity.this.setOpen(state, true);
         }

         protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            BarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
            BarrelBlockEntity.this.setOpen(state, false);
         }

         protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
         }

         protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
               Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
               return inventory == BarrelBlockEntity.this;
            } else {
               return false;
            }
         }
      };
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      if (!this.writeLootTable(view)) {
         Inventories.writeData(view, this.inventory);
      }

   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
      if (!this.readLootTable(view)) {
         Inventories.readData(view, this.inventory);
      }

   }

   public int size() {
      return 27;
   }

   protected DefaultedList getHeldStacks() {
      return this.inventory;
   }

   protected void setHeldStacks(DefaultedList inventory) {
      this.inventory = inventory;
   }

   protected Text getContainerName() {
      return Text.translatable("container.barrel");
   }

   protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
      return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
   }

   public void onOpen(PlayerEntity player) {
      if (!this.removed && !player.isSpectator()) {
         this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
      }

   }

   public void onClose(PlayerEntity player) {
      if (!this.removed && !player.isSpectator()) {
         this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
      }

   }

   public void tick() {
      if (!this.removed) {
         this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
      }

   }

   void setOpen(BlockState state, boolean open) {
      this.world.setBlockState(this.getPos(), (BlockState)state.with(BarrelBlock.OPEN, open), 3);
   }

   void playSound(BlockState state, SoundEvent soundEvent) {
      Vec3i vec3i = ((Direction)state.get(BarrelBlock.FACING)).getVector();
      double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
      double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
      double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
      this.world.playSound((Entity)null, d, e, f, (SoundEvent)soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
   }
}
