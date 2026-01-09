package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HopperMinecartEntity extends StorageMinecartEntity implements Hopper {
   private static final boolean DEFAULT_ENABLED = true;
   private boolean enabled = true;
   private boolean hopperTicked = false;

   public HopperMinecartEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public BlockState getDefaultContainedBlock() {
      return Blocks.HOPPER.getDefaultState();
   }

   public int getDefaultBlockOffset() {
      return 1;
   }

   public int size() {
      return 5;
   }

   public void onActivatorRail(int x, int y, int z, boolean powered) {
      boolean bl = !powered;
      if (bl != this.isEnabled()) {
         this.setEnabled(bl);
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public double getHopperX() {
      return this.getX();
   }

   public double getHopperY() {
      return this.getY() + 0.5;
   }

   public double getHopperZ() {
      return this.getZ();
   }

   public boolean canBlockFromAbove() {
      return false;
   }

   public void tick() {
      this.hopperTicked = false;
      super.tick();
      this.tickHopper();
   }

   protected double moveAlongTrack(BlockPos pos, RailShape shape, double remainingMovement) {
      double d = super.moveAlongTrack(pos, shape, remainingMovement);
      this.tickHopper();
      return d;
   }

   private void tickHopper() {
      if (!this.getWorld().isClient && this.isAlive() && this.isEnabled() && !this.hopperTicked && this.canOperate()) {
         this.hopperTicked = true;
         this.markDirty();
      }

   }

   public boolean canOperate() {
      if (HopperBlockEntity.extract((World)this.getWorld(), (Hopper)this)) {
         return true;
      } else {
         List list = this.getWorld().getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityPredicates.VALID_ENTITY);
         java.util.Iterator var2 = list.iterator();

         ItemEntity itemEntity;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            itemEntity = (ItemEntity)var2.next();
         } while(!HopperBlockEntity.extract((Inventory)this, (ItemEntity)itemEntity));

         return true;
      }
   }

   protected Item asItem() {
      return Items.HOPPER_MINECART;
   }

   public ItemStack getPickBlockStack() {
      return new ItemStack(Items.HOPPER_MINECART);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.putBoolean("Enabled", this.enabled);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.enabled = view.getBoolean("Enabled", true);
   }

   public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
      return new HopperScreenHandler(syncId, playerInventory, this);
   }
}
