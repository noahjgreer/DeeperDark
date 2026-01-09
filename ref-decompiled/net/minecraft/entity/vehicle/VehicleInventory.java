package net.minecraft.entity.vehicle;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface VehicleInventory extends Inventory, NamedScreenHandlerFactory {
   Vec3d getPos();

   Box getBoundingBox();

   @Nullable
   RegistryKey getLootTable();

   void setLootTable(@Nullable RegistryKey lootTable);

   long getLootTableSeed();

   void setLootTableSeed(long lootTableSeed);

   DefaultedList getInventory();

   void resetInventory();

   World getWorld();

   boolean isRemoved();

   default boolean isEmpty() {
      return this.isInventoryEmpty();
   }

   default void writeInventoryToData(WriteView view) {
      if (this.getLootTable() != null) {
         view.putString("LootTable", this.getLootTable().getValue().toString());
         if (this.getLootTableSeed() != 0L) {
            view.putLong("LootTableSeed", this.getLootTableSeed());
         }
      } else {
         Inventories.writeData(view, this.getInventory());
      }

   }

   default void readInventoryFromData(ReadView view) {
      this.resetInventory();
      RegistryKey registryKey = (RegistryKey)view.read("LootTable", LootTable.TABLE_KEY).orElse((Object)null);
      this.setLootTable(registryKey);
      this.setLootTableSeed(view.getLong("LootTableSeed", 0L));
      if (registryKey == null) {
         Inventories.readData(view, this.getInventory());
      }

   }

   default void onBroken(DamageSource source, ServerWorld world, Entity vehicle) {
      if (world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         ItemScatterer.spawn(world, (Entity)vehicle, (Inventory)this);
         Entity entity = source.getSource();
         if (entity != null && entity.getType() == EntityType.PLAYER) {
            PiglinBrain.onGuardedBlockInteracted(world, (PlayerEntity)entity, true);
         }

      }
   }

   default ActionResult open(PlayerEntity player) {
      player.openHandledScreen(this);
      return ActionResult.SUCCESS;
   }

   default void generateInventoryLoot(@Nullable PlayerEntity player) {
      MinecraftServer minecraftServer = this.getWorld().getServer();
      if (this.getLootTable() != null && minecraftServer != null) {
         LootTable lootTable = minecraftServer.getReloadableRegistries().getLootTable(this.getLootTable());
         if (player != null) {
            Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, this.getLootTable());
         }

         this.setLootTable((RegistryKey)null);
         LootWorldContext.Builder builder = (new LootWorldContext.Builder((ServerWorld)this.getWorld())).add(LootContextParameters.ORIGIN, this.getPos());
         if (player != null) {
            builder.luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player);
         }

         lootTable.supplyInventory(this, builder.build(LootContextTypes.CHEST), this.getLootTableSeed());
      }

   }

   default void clearInventory() {
      this.generateInventoryLoot((PlayerEntity)null);
      this.getInventory().clear();
   }

   default boolean isInventoryEmpty() {
      java.util.Iterator var1 = this.getInventory().iterator();

      ItemStack itemStack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemStack = (ItemStack)var1.next();
      } while(itemStack.isEmpty());

      return false;
   }

   default ItemStack removeInventoryStack(int slot) {
      this.generateInventoryLoot((PlayerEntity)null);
      ItemStack itemStack = (ItemStack)this.getInventory().get(slot);
      if (itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.getInventory().set(slot, ItemStack.EMPTY);
         return itemStack;
      }
   }

   default ItemStack getInventoryStack(int slot) {
      this.generateInventoryLoot((PlayerEntity)null);
      return (ItemStack)this.getInventory().get(slot);
   }

   default ItemStack removeInventoryStack(int slot, int amount) {
      this.generateInventoryLoot((PlayerEntity)null);
      return Inventories.splitStack(this.getInventory(), slot, amount);
   }

   default void setInventoryStack(int slot, ItemStack stack) {
      this.generateInventoryLoot((PlayerEntity)null);
      this.getInventory().set(slot, stack);
      stack.capCount(this.getMaxCount(stack));
   }

   default StackReference getInventoryStackReference(final int slot) {
      return slot >= 0 && slot < this.size() ? new StackReference() {
         public ItemStack get() {
            return VehicleInventory.this.getInventoryStack(slot);
         }

         public boolean set(ItemStack stack) {
            VehicleInventory.this.setInventoryStack(slot, stack);
            return true;
         }
      } : StackReference.EMPTY;
   }

   default boolean canPlayerAccess(PlayerEntity player) {
      return !this.isRemoved() && player.canInteractWithEntityIn(this.getBoundingBox(), 4.0);
   }
}
