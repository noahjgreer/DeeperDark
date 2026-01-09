package net.minecraft.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class StorageMinecartEntity extends AbstractMinecartEntity implements VehicleInventory {
   private DefaultedList inventory;
   @Nullable
   private RegistryKey lootTable;
   private long lootTableSeed;

   protected StorageMinecartEntity(EntityType entityType, World world) {
      super(entityType, world);
      this.inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
   }

   public void killAndDropSelf(ServerWorld world, DamageSource damageSource) {
      super.killAndDropSelf(world, damageSource);
      this.onBroken(damageSource, world, this);
   }

   public ItemStack getStack(int slot) {
      return this.getInventoryStack(slot);
   }

   public ItemStack removeStack(int slot, int amount) {
      return this.removeInventoryStack(slot, amount);
   }

   public ItemStack removeStack(int slot) {
      return this.removeInventoryStack(slot);
   }

   public void setStack(int slot, ItemStack stack) {
      this.setInventoryStack(slot, stack);
   }

   public StackReference getStackReference(int mappedIndex) {
      return this.getInventoryStackReference(mappedIndex);
   }

   public void markDirty() {
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return this.canPlayerAccess(player);
   }

   public void remove(Entity.RemovalReason reason) {
      if (!this.getWorld().isClient && reason.shouldDestroy()) {
         ItemScatterer.spawn(this.getWorld(), (Entity)this, (Inventory)this);
      }

      super.remove(reason);
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      this.writeInventoryToData(view);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.readInventoryFromData(view);
   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      return this.open(player);
   }

   protected Vec3d applySlowdown(Vec3d velocity) {
      float f = 0.98F;
      if (this.lootTable == null) {
         int i = 15 - ScreenHandler.calculateComparatorOutput((Inventory)this);
         f += (float)i * 0.001F;
      }

      if (this.isTouchingWater()) {
         f *= 0.95F;
      }

      return velocity.multiply((double)f, 0.0, (double)f);
   }

   public void clear() {
      this.clearInventory();
   }

   public void setLootTable(RegistryKey lootTable, long lootSeed) {
      this.lootTable = lootTable;
      this.lootTableSeed = lootSeed;
   }

   @Nullable
   public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      if (this.lootTable != null && playerEntity.isSpectator()) {
         return null;
      } else {
         this.generateInventoryLoot(playerInventory.player);
         return this.getScreenHandler(i, playerInventory);
      }
   }

   protected abstract ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory);

   @Nullable
   public RegistryKey getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(@Nullable RegistryKey lootTable) {
      this.lootTable = lootTable;
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setLootTableSeed(long lootTableSeed) {
      this.lootTableSeed = lootTableSeed;
   }

   public DefaultedList getInventory() {
      return this.inventory;
   }

   public void resetInventory() {
      this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
   }
}
