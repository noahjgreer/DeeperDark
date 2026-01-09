package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class DecoratedPotBlockEntity extends BlockEntity implements LootableInventory, SingleStackInventory.SingleStackBlockEntityInventory {
   public static final String SHERDS_NBT_KEY = "sherds";
   public static final String ITEM_NBT_KEY = "item";
   public static final int field_46660 = 1;
   public long lastWobbleTime;
   @Nullable
   public WobbleType lastWobbleType;
   private Sherds sherds;
   private ItemStack stack;
   @Nullable
   protected RegistryKey lootTableId;
   protected long lootTableSeed;

   public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
      super(BlockEntityType.DECORATED_POT, pos, state);
      this.stack = ItemStack.EMPTY;
      this.sherds = Sherds.DEFAULT;
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      if (!this.sherds.equals(Sherds.DEFAULT)) {
         view.put("sherds", Sherds.CODEC, this.sherds);
      }

      if (!this.writeLootTable(view) && !this.stack.isEmpty()) {
         view.put("item", ItemStack.CODEC, this.stack);
      }

   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.sherds = (Sherds)view.read("sherds", Sherds.CODEC).orElse(Sherds.DEFAULT);
      if (!this.readLootTable(view)) {
         this.stack = (ItemStack)view.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
      } else {
         this.stack = ItemStack.EMPTY;
      }

   }

   public BlockEntityUpdateS2CPacket toUpdatePacket() {
      return BlockEntityUpdateS2CPacket.create(this);
   }

   public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
      return this.createComponentlessNbt(registries);
   }

   public Direction getHorizontalFacing() {
      return (Direction)this.getCachedState().get(Properties.HORIZONTAL_FACING);
   }

   public Sherds getSherds() {
      return this.sherds;
   }

   public static ItemStack getStackWith(Sherds sherds) {
      ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
      itemStack.set(DataComponentTypes.POT_DECORATIONS, sherds);
      return itemStack;
   }

   @Nullable
   public RegistryKey getLootTable() {
      return this.lootTableId;
   }

   public void setLootTable(@Nullable RegistryKey lootTable) {
      this.lootTableId = lootTable;
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setLootTableSeed(long lootTableSeed) {
      this.lootTableSeed = lootTableSeed;
   }

   protected void addComponents(ComponentMap.Builder builder) {
      super.addComponents(builder);
      builder.add(DataComponentTypes.POT_DECORATIONS, this.sherds);
      builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(List.of(this.stack)));
   }

   protected void readComponents(ComponentsAccess components) {
      super.readComponents(components);
      this.sherds = (Sherds)components.getOrDefault(DataComponentTypes.POT_DECORATIONS, Sherds.DEFAULT);
      this.stack = ((ContainerComponent)components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT)).copyFirstStack();
   }

   public void removeFromCopiedStackData(WriteView view) {
      super.removeFromCopiedStackData(view);
      view.remove("sherds");
      view.remove("item");
   }

   public ItemStack getStack() {
      this.generateLoot((PlayerEntity)null);
      return this.stack;
   }

   public ItemStack decreaseStack(int count) {
      this.generateLoot((PlayerEntity)null);
      ItemStack itemStack = this.stack.split(count);
      if (this.stack.isEmpty()) {
         this.stack = ItemStack.EMPTY;
      }

      return itemStack;
   }

   public void setStack(ItemStack stack) {
      this.generateLoot((PlayerEntity)null);
      this.stack = stack;
   }

   public BlockEntity asBlockEntity() {
      return this;
   }

   public void wobble(WobbleType wobbleType) {
      if (this.world != null && !this.world.isClient()) {
         this.world.addSyncedBlockEvent(this.getPos(), this.getCachedState().getBlock(), 1, wobbleType.ordinal());
      }
   }

   public boolean onSyncedBlockEvent(int type, int data) {
      if (this.world != null && type == 1 && data >= 0 && data < DecoratedPotBlockEntity.WobbleType.values().length) {
         this.lastWobbleTime = this.world.getTime();
         this.lastWobbleType = DecoratedPotBlockEntity.WobbleType.values()[data];
         return true;
      } else {
         return super.onSyncedBlockEvent(type, data);
      }
   }

   // $FF: synthetic method
   public Packet toUpdatePacket() {
      return this.toUpdatePacket();
   }

   public static enum WobbleType {
      POSITIVE(7),
      NEGATIVE(10);

      public final int lengthInTicks;

      private WobbleType(final int lengthInTicks) {
         this.lengthInTicks = lengthInTicks;
      }

      // $FF: synthetic method
      private static WobbleType[] method_54302() {
         return new WobbleType[]{POSITIVE, NEGATIVE};
      }
   }
}
