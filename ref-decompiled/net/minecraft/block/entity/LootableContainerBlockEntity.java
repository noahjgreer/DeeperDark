package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class LootableContainerBlockEntity extends LockableContainerBlockEntity implements LootableInventory {
   @Nullable
   protected RegistryKey lootTable;
   protected long lootTableSeed = 0L;

   protected LootableContainerBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
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

   public boolean isEmpty() {
      this.generateLoot((PlayerEntity)null);
      return super.isEmpty();
   }

   public ItemStack getStack(int slot) {
      this.generateLoot((PlayerEntity)null);
      return super.getStack(slot);
   }

   public ItemStack removeStack(int slot, int amount) {
      this.generateLoot((PlayerEntity)null);
      return super.removeStack(slot, amount);
   }

   public ItemStack removeStack(int slot) {
      this.generateLoot((PlayerEntity)null);
      return super.removeStack(slot);
   }

   public void setStack(int slot, ItemStack stack) {
      this.generateLoot((PlayerEntity)null);
      super.setStack(slot, stack);
   }

   public boolean checkUnlocked(PlayerEntity player) {
      return super.checkUnlocked(player) && (this.lootTable == null || !player.isSpectator());
   }

   @Nullable
   public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      if (this.checkUnlocked(playerEntity)) {
         this.generateLoot(playerInventory.player);
         return this.createScreenHandler(i, playerInventory);
      } else {
         return null;
      }
   }

   protected void readComponents(ComponentsAccess components) {
      super.readComponents(components);
      ContainerLootComponent containerLootComponent = (ContainerLootComponent)components.get(DataComponentTypes.CONTAINER_LOOT);
      if (containerLootComponent != null) {
         this.lootTable = containerLootComponent.lootTable();
         this.lootTableSeed = containerLootComponent.seed();
      }

   }

   protected void addComponents(ComponentMap.Builder builder) {
      super.addComponents(builder);
      if (this.lootTable != null) {
         builder.add(DataComponentTypes.CONTAINER_LOOT, new ContainerLootComponent(this.lootTable, this.lootTableSeed));
      }

   }

   public void removeFromCopiedStackData(WriteView view) {
      super.removeFromCopiedStackData(view);
      view.remove("LootTable");
      view.remove("LootTableSeed");
   }
}
