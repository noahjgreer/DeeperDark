package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory {
   public static final int field_31354 = 9;
   public static final int field_31355 = 3;
   public static final int INVENTORY_SIZE = 27;
   public static final int field_31357 = 1;
   public static final int field_31358 = 10;
   public static final float field_31359 = 0.5F;
   public static final float field_31360 = 270.0F;
   private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
   private DefaultedList inventory;
   private int viewerCount;
   private AnimationStage animationStage;
   private float animationProgress;
   private float lastAnimationProgress;
   @Nullable
   private final DyeColor cachedColor;

   public ShulkerBoxBlockEntity(@Nullable DyeColor color, BlockPos pos, BlockState state) {
      super(BlockEntityType.SHULKER_BOX, pos, state);
      this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
      this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
      this.cachedColor = color;
   }

   public ShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.SHULKER_BOX, pos, state);
      this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
      this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
      Block var4 = state.getBlock();
      DyeColor var10001;
      if (var4 instanceof ShulkerBoxBlock shulkerBoxBlock) {
         var10001 = shulkerBoxBlock.getColor();
      } else {
         var10001 = null;
      }

      this.cachedColor = var10001;
   }

   public static void tick(World world, BlockPos pos, BlockState state, ShulkerBoxBlockEntity blockEntity) {
      blockEntity.updateAnimation(world, pos, state);
   }

   private void updateAnimation(World world, BlockPos pos, BlockState state) {
      this.lastAnimationProgress = this.animationProgress;
      switch (this.animationStage.ordinal()) {
         case 0:
            this.animationProgress = 0.0F;
            break;
         case 1:
            this.animationProgress += 0.1F;
            if (this.lastAnimationProgress == 0.0F) {
               updateNeighborStates(world, pos, state);
            }

            if (this.animationProgress >= 1.0F) {
               this.animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENED;
               this.animationProgress = 1.0F;
               updateNeighborStates(world, pos, state);
            }

            this.pushEntities(world, pos, state);
            break;
         case 2:
            this.animationProgress = 1.0F;
            break;
         case 3:
            this.animationProgress -= 0.1F;
            if (this.lastAnimationProgress == 1.0F) {
               updateNeighborStates(world, pos, state);
            }

            if (this.animationProgress <= 0.0F) {
               this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
               this.animationProgress = 0.0F;
               updateNeighborStates(world, pos, state);
            }
      }

   }

   public AnimationStage getAnimationStage() {
      return this.animationStage;
   }

   public Box getBoundingBox(BlockState state) {
      Vec3d vec3d = new Vec3d(0.5, 0.0, 0.5);
      return ShulkerEntity.calculateBoundingBox(1.0F, (Direction)state.get(ShulkerBoxBlock.FACING), 0.5F * this.getAnimationProgress(1.0F), vec3d);
   }

   private void pushEntities(World world, BlockPos pos, BlockState state) {
      if (state.getBlock() instanceof ShulkerBoxBlock) {
         Direction direction = (Direction)state.get(ShulkerBoxBlock.FACING);
         Box box = ShulkerEntity.calculateBoundingBox(1.0F, direction, this.lastAnimationProgress, this.animationProgress, pos.toBottomCenterPos());
         List list = world.getOtherEntities((Entity)null, box);
         if (!list.isEmpty()) {
            java.util.Iterator var7 = list.iterator();

            while(var7.hasNext()) {
               Entity entity = (Entity)var7.next();
               if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
                  entity.move(MovementType.SHULKER_BOX, new Vec3d((box.getLengthX() + 0.01) * (double)direction.getOffsetX(), (box.getLengthY() + 0.01) * (double)direction.getOffsetY(), (box.getLengthZ() + 0.01) * (double)direction.getOffsetZ()));
               }
            }

         }
      }
   }

   public int size() {
      return this.inventory.size();
   }

   public boolean onSyncedBlockEvent(int type, int data) {
      if (type == 1) {
         this.viewerCount = data;
         if (data == 0) {
            this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSING;
         }

         if (data == 1) {
            this.animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENING;
         }

         return true;
      } else {
         return super.onSyncedBlockEvent(type, data);
      }
   }

   private static void updateNeighborStates(World world, BlockPos pos, BlockState state) {
      state.updateNeighbors(world, pos, 3);
      world.updateNeighbors(pos, state.getBlock());
   }

   public void onBlockReplaced(BlockPos pos, BlockState oldState) {
   }

   public void onOpen(PlayerEntity player) {
      if (!this.removed && !player.isSpectator()) {
         if (this.viewerCount < 0) {
            this.viewerCount = 0;
         }

         ++this.viewerCount;
         this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
         if (this.viewerCount == 1) {
            this.world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, this.pos);
            this.world.playSound((Entity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public void onClose(PlayerEntity player) {
      if (!this.removed && !player.isSpectator()) {
         --this.viewerCount;
         this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
         if (this.viewerCount <= 0) {
            this.world.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, this.pos);
            this.world.playSound((Entity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   protected Text getContainerName() {
      return Text.translatable("container.shulkerBox");
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.readInventoryNbt(view);
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      if (!this.writeLootTable(view)) {
         Inventories.writeData(view, this.inventory, false);
      }

   }

   public void readInventoryNbt(ReadView readView) {
      this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
      if (!this.readLootTable(readView)) {
         Inventories.readData(readView, this.inventory);
      }

   }

   protected DefaultedList getHeldStacks() {
      return this.inventory;
   }

   protected void setHeldStacks(DefaultedList inventory) {
      this.inventory = inventory;
   }

   public int[] getAvailableSlots(Direction side) {
      return AVAILABLE_SLOTS;
   }

   public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
      return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
   }

   public boolean canExtract(int slot, ItemStack stack, Direction dir) {
      return true;
   }

   public float getAnimationProgress(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastAnimationProgress, this.animationProgress);
   }

   @Nullable
   public DyeColor getColor() {
      return this.cachedColor;
   }

   protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
      return new ShulkerBoxScreenHandler(syncId, playerInventory, this);
   }

   public boolean suffocates() {
      return this.animationStage == ShulkerBoxBlockEntity.AnimationStage.CLOSED;
   }

   public static enum AnimationStage {
      CLOSED,
      OPENING,
      OPENED,
      CLOSING;

      // $FF: synthetic method
      private static AnimationStage[] method_36717() {
         return new AnimationStage[]{CLOSED, OPENING, OPENED, CLOSING};
      }
   }
}
