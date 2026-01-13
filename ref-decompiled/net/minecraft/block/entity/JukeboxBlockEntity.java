/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.JukeboxBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.JukeboxBlockEntity
 *  net.minecraft.block.jukebox.JukeboxManager
 *  net.minecraft.block.jukebox.JukeboxSong
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SingleStackInventory$SingleStackBlockEntityInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 */
package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.jukebox.JukeboxManager;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class JukeboxBlockEntity
extends BlockEntity
implements SingleStackInventory.SingleStackBlockEntityInventory {
    public static final String RECORD_ITEM_NBT_KEY = "RecordItem";
    public static final String TICKS_SINCE_SONG_STARTED_NBT_KEY = "ticks_since_song_started";
    private ItemStack recordStack = ItemStack.EMPTY;
    private final JukeboxManager manager = new JukeboxManager(() -> this.onManagerChange(), this.getPos());

    public JukeboxBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.JUKEBOX, pos, state);
    }

    public JukeboxManager getManager() {
        return this.manager;
    }

    public void onManagerChange() {
        this.world.updateNeighbors(this.getPos(), this.getCachedState().getBlock());
        this.markDirty();
    }

    private void onRecordStackChanged(boolean hasRecord) {
        if (this.world == null || this.world.getBlockState(this.getPos()) != this.getCachedState()) {
            return;
        }
        this.world.setBlockState(this.getPos(), (BlockState)this.getCachedState().with((Property)JukeboxBlock.HAS_RECORD, (Comparable)Boolean.valueOf(hasRecord)), 2);
        this.world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of((BlockState)this.getCachedState()));
    }

    public void dropRecord() {
        if (this.world == null || this.world.isClient()) {
            return;
        }
        BlockPos blockPos = this.getPos();
        ItemStack itemStack = this.getStack();
        if (itemStack.isEmpty()) {
            return;
        }
        this.emptyStack();
        Vec3d vec3d = Vec3d.add((Vec3i)blockPos, (double)0.5, (double)1.01, (double)0.5).addHorizontalRandom(this.world.random, 0.7f);
        ItemStack itemStack2 = itemStack.copy();
        ItemEntity itemEntity = new ItemEntity(this.world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), itemStack2);
        itemEntity.setToDefaultPickupDelay();
        this.world.spawnEntity((Entity)itemEntity);
        this.onManagerChange();
    }

    public static void tick(World world, BlockPos pos, BlockState state, JukeboxBlockEntity blockEntity) {
        blockEntity.manager.tick((WorldAccess)world, state);
    }

    public int getComparatorOutput() {
        return JukeboxSong.getSongEntryFromStack((RegistryWrapper.WrapperLookup)this.world.getRegistryManager(), (ItemStack)this.recordStack).map(RegistryEntry::value).map(JukeboxSong::comparatorOutput).orElse(0);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        ItemStack itemStack = view.read(RECORD_ITEM_NBT_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        if (!this.recordStack.isEmpty() && !ItemStack.areItemsAndComponentsEqual((ItemStack)itemStack, (ItemStack)this.recordStack)) {
            this.manager.stopPlaying((WorldAccess)this.world, this.getCachedState());
        }
        this.recordStack = itemStack;
        view.getOptionalLong(TICKS_SINCE_SONG_STARTED_NBT_KEY).ifPresent(ticksSinceSongStarted -> JukeboxSong.getSongEntryFromStack((RegistryWrapper.WrapperLookup)view.getRegistries(), (ItemStack)this.recordStack).ifPresent(song -> this.manager.setValues(song, ticksSinceSongStarted.longValue())));
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.getStack().isEmpty()) {
            view.put(RECORD_ITEM_NBT_KEY, ItemStack.CODEC, (Object)this.getStack());
        }
        if (this.manager.getSong() != null) {
            view.putLong(TICKS_SINCE_SONG_STARTED_NBT_KEY, this.manager.getTicksSinceSongStarted());
        }
    }

    public ItemStack getStack() {
        return this.recordStack;
    }

    public ItemStack decreaseStack(int count) {
        ItemStack itemStack = this.recordStack;
        this.setStack(ItemStack.EMPTY);
        return itemStack;
    }

    public void setStack(ItemStack stack) {
        this.recordStack = stack;
        boolean bl = !this.recordStack.isEmpty();
        Optional optional = JukeboxSong.getSongEntryFromStack((RegistryWrapper.WrapperLookup)this.world.getRegistryManager(), (ItemStack)this.recordStack);
        this.onRecordStackChanged(bl);
        if (bl && optional.isPresent()) {
            this.manager.startPlaying((WorldAccess)this.world, (RegistryEntry)optional.get());
        } else {
            this.manager.stopPlaying((WorldAccess)this.world, this.getCachedState());
        }
    }

    public void markRemoved() {
        super.markRemoved();
        this.world.emitGameEvent((RegistryEntry)GameEvent.JUKEBOX_STOP_PLAY, this.getPos(), GameEvent.Emitter.of((BlockState)this.getCachedState()));
        this.world.syncWorldEvent(1011, this.getPos(), 0);
    }

    public int getMaxCountPerStack() {
        return 1;
    }

    public BlockEntity asBlockEntity() {
        return this;
    }

    public boolean isValid(int slot, ItemStack stack) {
        return stack.contains(DataComponentTypes.JUKEBOX_PLAYABLE) && this.getStack(slot).isEmpty();
    }

    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return hopperInventory.containsAny(ItemStack::isEmpty);
    }

    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        this.dropRecord();
    }

    @VisibleForTesting
    public void setDisc(ItemStack stack) {
        this.recordStack = stack;
        JukeboxSong.getSongEntryFromStack((RegistryWrapper.WrapperLookup)this.world.getRegistryManager(), (ItemStack)stack).ifPresent(song -> this.manager.setValues(song, 0L));
        this.world.updateNeighbors(this.getPos(), this.getCachedState().getBlock());
        this.markDirty();
    }

    @VisibleForTesting
    public void reloadDisc() {
        JukeboxSong.getSongEntryFromStack((RegistryWrapper.WrapperLookup)this.world.getRegistryManager(), (ItemStack)this.getStack()).ifPresent(song -> this.manager.startPlaying((WorldAccess)this.world, song));
    }
}

