package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public class ChiseledBookshelfBlockEntity extends BlockEntity implements Inventory {
   public static final int MAX_BOOKS = 6;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_LAST_INTERACTED_SLOT = -1;
   private final DefaultedList inventory;
   private int lastInteractedSlot;

   public ChiseledBookshelfBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.CHISELED_BOOKSHELF, pos, state);
      this.inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
      this.lastInteractedSlot = -1;
   }

   private void updateState(int interactedSlot) {
      if (interactedSlot >= 0 && interactedSlot < 6) {
         this.lastInteractedSlot = interactedSlot;
         BlockState blockState = this.getCachedState();

         for(int i = 0; i < ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); ++i) {
            boolean bl = !this.getStack(i).isEmpty();
            BooleanProperty booleanProperty = (BooleanProperty)ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i);
            blockState = (BlockState)blockState.with(booleanProperty, bl);
         }

         ((World)Objects.requireNonNull(this.world)).setBlockState(this.pos, blockState, 3);
         this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(blockState));
      } else {
         LOGGER.error("Expected slot 0-5, got {}", interactedSlot);
      }
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.inventory.clear();
      Inventories.readData(view, this.inventory);
      this.lastInteractedSlot = view.getInt("last_interacted_slot", -1);
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      Inventories.writeData(view, this.inventory, true);
      view.putInt("last_interacted_slot", this.lastInteractedSlot);
   }

   public int getFilledSlotCount() {
      return (int)this.inventory.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
   }

   public void clear() {
      this.inventory.clear();
   }

   public int size() {
      return 6;
   }

   public boolean isEmpty() {
      return this.inventory.stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getStack(int slot) {
      return (ItemStack)this.inventory.get(slot);
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack itemStack = (ItemStack)Objects.requireNonNullElse((ItemStack)this.inventory.get(slot), ItemStack.EMPTY);
      this.inventory.set(slot, ItemStack.EMPTY);
      if (!itemStack.isEmpty()) {
         this.updateState(slot);
      }

      return itemStack;
   }

   public ItemStack removeStack(int slot) {
      return this.removeStack(slot, 1);
   }

   public void setStack(int slot, ItemStack stack) {
      if (stack.isIn(ItemTags.BOOKSHELF_BOOKS)) {
         this.inventory.set(slot, stack);
         this.updateState(slot);
      } else if (stack.isEmpty()) {
         this.removeStack(slot, 1);
      }

   }

   public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
      return hopperInventory.containsAny((stack2) -> {
         if (stack2.isEmpty()) {
            return true;
         } else {
            return ItemStack.areItemsAndComponentsEqual(stack, stack2) && stack2.getCount() + stack.getCount() <= hopperInventory.getMaxCount(stack2);
         }
      });
   }

   public int getMaxCountPerStack() {
      return 1;
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return Inventory.canPlayerUse(this, player);
   }

   public boolean isValid(int slot, ItemStack stack) {
      return stack.isIn(ItemTags.BOOKSHELF_BOOKS) && this.getStack(slot).isEmpty() && stack.getCount() == this.getMaxCountPerStack();
   }

   public int getLastInteractedSlot() {
      return this.lastInteractedSlot;
   }

   protected void readComponents(ComponentsAccess components) {
      super.readComponents(components);
      ((ContainerComponent)components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)).copyTo(this.inventory);
   }

   protected void addComponents(ComponentMap.Builder builder) {
      super.addComponents(builder);
      builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.inventory));
   }

   public void removeFromCopiedStackData(WriteView view) {
      view.remove("Items");
   }
}
