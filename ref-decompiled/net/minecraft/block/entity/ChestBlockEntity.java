package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChestBlockEntity extends LootableContainerBlockEntity implements LidOpenable {
   private static final int VIEWER_COUNT_UPDATE_EVENT_TYPE = 1;
   private DefaultedList inventory;
   private final ViewerCountManager stateManager;
   private final ChestLidAnimator lidAnimator;

   protected ChestBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
      this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
      this.stateManager = new ViewerCountManager() {
         protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            ChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_OPEN);
         }

         protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            ChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_CLOSE);
         }

         protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            ChestBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
         }

         protected boolean isPlayerViewing(PlayerEntity player) {
            if (!(player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
               return false;
            } else {
               Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
               return inventory == ChestBlockEntity.this || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(ChestBlockEntity.this);
            }
         }
      };
      this.lidAnimator = new ChestLidAnimator();
   }

   public ChestBlockEntity(BlockPos pos, BlockState state) {
      this(BlockEntityType.CHEST, pos, state);
   }

   public int size() {
      return 27;
   }

   protected Text getContainerName() {
      return Text.translatable("container.chest");
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

   public static void clientTick(World world, BlockPos pos, BlockState state, ChestBlockEntity blockEntity) {
      blockEntity.lidAnimator.step();
   }

   static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
      ChestType chestType = (ChestType)state.get(ChestBlock.CHEST_TYPE);
      if (chestType != ChestType.LEFT) {
         double d = (double)pos.getX() + 0.5;
         double e = (double)pos.getY() + 0.5;
         double f = (double)pos.getZ() + 0.5;
         if (chestType == ChestType.RIGHT) {
            Direction direction = ChestBlock.getFacing(state);
            d += (double)direction.getOffsetX() * 0.5;
            f += (double)direction.getOffsetZ() * 0.5;
         }

         world.playSound((Entity)null, d, e, f, (SoundEvent)soundEvent, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
      }
   }

   public boolean onSyncedBlockEvent(int type, int data) {
      if (type == 1) {
         this.lidAnimator.setOpen(data > 0);
         return true;
      } else {
         return super.onSyncedBlockEvent(type, data);
      }
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

   protected DefaultedList getHeldStacks() {
      return this.inventory;
   }

   protected void setHeldStacks(DefaultedList inventory) {
      this.inventory = inventory;
   }

   public float getAnimationProgress(float tickProgress) {
      return this.lidAnimator.getProgress(tickProgress);
   }

   public static int getPlayersLookingInChestCount(BlockView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      if (blockState.hasBlockEntity()) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof ChestBlockEntity) {
            return ((ChestBlockEntity)blockEntity).stateManager.getViewerCount();
         }
      }

      return 0;
   }

   public static void copyInventory(ChestBlockEntity from, ChestBlockEntity to) {
      DefaultedList defaultedList = from.getHeldStacks();
      from.setHeldStacks(to.getHeldStacks());
      to.setHeldStacks(defaultedList);
   }

   protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
      return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
   }

   public void onScheduledTick() {
      if (!this.removed) {
         this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
      }

   }

   protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
      Block block = state.getBlock();
      world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
   }
}
