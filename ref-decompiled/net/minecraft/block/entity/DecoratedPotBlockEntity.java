/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.DecoratedPotBlockEntity
 *  net.minecraft.block.entity.DecoratedPotBlockEntity$WobbleType
 *  net.minecraft.block.entity.Sherds
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.inventory.LootableInventory
 *  net.minecraft.inventory.SingleStackInventory$SingleStackBlockEntityInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.loot.LootTable
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class DecoratedPotBlockEntity
extends BlockEntity
implements LootableInventory,
SingleStackInventory.SingleStackBlockEntityInventory {
    public static final String SHERDS_NBT_KEY = "sherds";
    public static final String ITEM_NBT_KEY = "item";
    public static final int field_46660 = 1;
    public long lastWobbleTime;
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable DecoratedPotBlockEntity.WobbleType lastWobbleType;
    private Sherds sherds;
    private ItemStack stack = ItemStack.EMPTY;
    protected @Nullable RegistryKey<LootTable> lootTableId;
    protected long lootTableSeed;

    public DecoratedPotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.DECORATED_POT, pos, state);
        this.sherds = Sherds.DEFAULT;
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.sherds.equals((Object)Sherds.DEFAULT)) {
            view.put("sherds", Sherds.CODEC, (Object)this.sherds);
        }
        if (!this.writeLootTable(view) && !this.stack.isEmpty()) {
            view.put("item", ItemStack.CODEC, (Object)this.stack);
        }
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.sherds = view.read("sherds", Sherds.CODEC).orElse(Sherds.DEFAULT);
        this.stack = !this.readLootTable(view) ? view.read("item", ItemStack.CODEC).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    public Direction getHorizontalFacing() {
        return (Direction)this.getCachedState().get((Property)Properties.HORIZONTAL_FACING);
    }

    public Sherds getSherds() {
        return this.sherds;
    }

    public static ItemStack getStackWith(Sherds sherds) {
        ItemStack itemStack = Items.DECORATED_POT.getDefaultStack();
        itemStack.set(DataComponentTypes.POT_DECORATIONS, (Object)sherds);
        return itemStack;
    }

    public @Nullable RegistryKey<LootTable> getLootTable() {
        return this.lootTableId;
    }

    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
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
        builder.add(DataComponentTypes.POT_DECORATIONS, (Object)this.sherds);
        builder.add(DataComponentTypes.CONTAINER, (Object)ContainerComponent.fromStacks(List.of(this.stack)));
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.sherds = (Sherds)components.getOrDefault(DataComponentTypes.POT_DECORATIONS, (Object)Sherds.DEFAULT);
        this.stack = ((ContainerComponent)components.getOrDefault(DataComponentTypes.CONTAINER, (Object)ContainerComponent.DEFAULT)).copyFirstStack();
    }

    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove("sherds");
        view.remove("item");
    }

    public ItemStack getStack() {
        this.generateLoot(null);
        return this.stack;
    }

    public ItemStack decreaseStack(int count) {
        this.generateLoot(null);
        ItemStack itemStack = this.stack.split(count);
        if (this.stack.isEmpty()) {
            this.stack = ItemStack.EMPTY;
        }
        return itemStack;
    }

    public void setStack(ItemStack stack) {
        this.generateLoot(null);
        this.stack = stack;
    }

    public BlockEntity asBlockEntity() {
        return this;
    }

    public void wobble(WobbleType wobbleType) {
        if (this.world == null || this.world.isClient()) {
            return;
        }
        this.world.addSyncedBlockEvent(this.getPos(), this.getCachedState().getBlock(), 1, wobbleType.ordinal());
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (this.world != null && type == 1 && data >= 0 && data < WobbleType.values().length) {
            this.lastWobbleTime = this.world.getTime();
            this.lastWobbleType = WobbleType.values()[data];
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

