package net.minecraft.entity.vehicle;

import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractChestBoatEntity extends AbstractBoatEntity implements RideableInventory, VehicleInventory {
   private static final int INVENTORY_SIZE = 27;
   private DefaultedList inventory;
   @Nullable
   private RegistryKey lootTable;
   private long lootTableSeed;

   public AbstractChestBoatEntity(EntityType entityType, World world, Supplier supplier) {
      super(entityType, world, supplier);
      this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
   }

   protected float getPassengerHorizontalOffset() {
      return 0.15F;
   }

   protected int getMaxPassengers() {
      return 1;
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      this.writeInventoryToData(view);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.readInventoryFromData(view);
   }

   public void killAndDropSelf(ServerWorld world, DamageSource damageSource) {
      this.killAndDropItem(world, this.asItem());
      this.onBroken(damageSource, world, this);
   }

   public void remove(Entity.RemovalReason reason) {
      if (!this.getWorld().isClient && reason.shouldDestroy()) {
         ItemScatterer.spawn(this.getWorld(), (Entity)this, (Inventory)this);
      }

      super.remove(reason);
   }

   public ActionResult interact(PlayerEntity player, Hand hand) {
      ActionResult actionResult = super.interact(player, hand);
      if (actionResult != ActionResult.PASS) {
         return actionResult;
      } else if (this.canAddPassenger(player) && !player.shouldCancelInteraction()) {
         return ActionResult.PASS;
      } else {
         ActionResult actionResult2 = this.open(player);
         if (actionResult2.isAccepted()) {
            World var6 = player.getWorld();
            if (var6 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var6;
               this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
               PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
            }
         }

         return actionResult2;
      }
   }

   public void openInventory(PlayerEntity player) {
      player.openHandledScreen(this);
      World var3 = player.getWorld();
      if (var3 instanceof ServerWorld serverWorld) {
         this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
         PiglinBrain.onGuardedBlockInteracted(serverWorld, player, true);
      }

   }

   public void clear() {
      this.clearInventory();
   }

   public int size() {
      return 27;
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

   @Nullable
   public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      if (this.lootTable != null && playerEntity.isSpectator()) {
         return null;
      } else {
         this.generateLoot(playerInventory.player);
         return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, this);
      }
   }

   public void generateLoot(@Nullable PlayerEntity player) {
      this.generateInventoryLoot(player);
   }

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

   public void onClose(PlayerEntity player) {
      this.getWorld().emitGameEvent(GameEvent.CONTAINER_CLOSE, this.getPos(), GameEvent.Emitter.of((Entity)player));
   }
}
