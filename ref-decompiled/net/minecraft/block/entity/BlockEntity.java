/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  net.fabricmc.fabric.api.attachment.v1.AttachmentTarget
 *  net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntity$ReporterContext
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.component.ComponentChanges
 *  net.minecraft.component.ComponentMap
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentType
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.MergedComponentMap
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.listener.ClientPlayPacketListener
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.storage.NbtReadView
 *  net.minecraft.storage.NbtWriteView
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextCodecs
 *  net.minecraft.util.ErrorReporter
 *  net.minecraft.util.ErrorReporter$Context
 *  net.minecraft.util.ErrorReporter$Logging
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.world.HeightLimitView
 *  net.minecraft.world.World
 *  net.minecraft.world.debug.DebugTrackable
 *  net.minecraft.world.debug.DebugTrackable$Tracker
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugTrackable;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class BlockEntity
implements DebugTrackable,
RenderDataBlockEntity,
AttachmentTarget {
    private static final Codec<BlockEntityType<?>> TYPE_CODEC = Registries.BLOCK_ENTITY_TYPE.getCodec();
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityType<?> type;
    protected @Nullable World world;
    protected final BlockPos pos;
    protected boolean removed;
    private BlockState cachedState;
    private ComponentMap components = ComponentMap.EMPTY;

    public BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this.type = type;
        this.pos = pos.toImmutable();
        this.validateSupports(state);
        this.cachedState = state;
    }

    private void validateSupports(BlockState state) {
        if (!this.supports(state)) {
            throw new IllegalStateException("Invalid block entity " + this.getNameForReport() + " state at " + String.valueOf(this.pos) + ", got " + String.valueOf(state));
        }
    }

    public boolean supports(BlockState state) {
        return this.type.supports(state);
    }

    public static BlockPos posFromNbt(ChunkPos chunkPos, NbtCompound nbt) {
        int i = nbt.getInt("x", 0);
        int j = nbt.getInt("y", 0);
        int k = nbt.getInt("z", 0);
        int l = ChunkSectionPos.getSectionCoord((int)i);
        int m = ChunkSectionPos.getSectionCoord((int)k);
        if (l != chunkPos.x || m != chunkPos.z) {
            LOGGER.warn("Block entity {} found in a wrong chunk, expected position from chunk {}", (Object)nbt, (Object)chunkPos);
            i = chunkPos.getOffsetX(ChunkSectionPos.getLocalCoord((int)i));
            k = chunkPos.getOffsetZ(ChunkSectionPos.getLocalCoord((int)k));
        }
        return new BlockPos(i, j, k);
    }

    public @Nullable World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean hasWorld() {
        return this.world != null;
    }

    protected void readData(ReadView view) {
    }

    public final void read(ReadView view) {
        this.readData(view);
        this.components = view.read("components", ComponentMap.CODEC).orElse(ComponentMap.EMPTY);
    }

    public final void readComponentlessData(ReadView view) {
        this.readData(view);
    }

    protected void writeData(WriteView view) {
    }

    public final NbtCompound createNbtWithIdentifyingData(RegistryWrapper.WrapperLookup registries) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)registries);
            this.writeFullData((WriteView)nbtWriteView);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            return nbtCompound;
        }
    }

    public void writeFullData(WriteView view) {
        this.writeDataWithoutId(view);
        this.writeIdentifyingData(view);
    }

    public void writeDataWithId(WriteView view) {
        this.writeDataWithoutId(view);
        this.writeId(view);
    }

    public final NbtCompound createNbt(RegistryWrapper.WrapperLookup registries) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)registries);
            this.writeDataWithoutId((WriteView)nbtWriteView);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            return nbtCompound;
        }
    }

    public void writeDataWithoutId(WriteView data) {
        this.writeData(data);
        data.put("components", ComponentMap.CODEC, (Object)this.components);
    }

    public final NbtCompound createComponentlessNbt(RegistryWrapper.WrapperLookup registries) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)registries);
            this.writeComponentlessData((WriteView)nbtWriteView);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            return nbtCompound;
        }
    }

    public void writeComponentlessData(WriteView view) {
        this.writeData(view);
    }

    private void writeId(WriteView view) {
        BlockEntity.writeId((WriteView)view, (BlockEntityType)this.getType());
    }

    public static void writeId(WriteView view, BlockEntityType<?> type) {
        view.put("id", TYPE_CODEC, type);
    }

    private void writeIdentifyingData(WriteView view) {
        this.writeId(view);
        view.putInt("x", this.pos.getX());
        view.putInt("y", this.pos.getY());
        view.putInt("z", this.pos.getZ());
    }

    public static @Nullable BlockEntity createFromNbt(BlockPos pos, BlockState state, NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        BlockEntity blockEntity;
        BlockEntity blockEntity2;
        BlockEntityType blockEntityType = nbt.get("id", TYPE_CODEC).orElse(null);
        if (blockEntityType == null) {
            LOGGER.error("Skipping block entity with invalid type: {}", (Object)nbt.get("id"));
            return null;
        }
        try {
            blockEntity2 = blockEntityType.instantiate(pos, state);
        }
        catch (Throwable throwable) {
            LOGGER.error("Failed to create block entity {} for block {} at position {} ", new Object[]{blockEntityType, pos, state, throwable});
            return null;
        }
        ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity2.getReporterContext(), LOGGER);
        try {
            blockEntity2.read(NbtReadView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)registries, (NbtCompound)nbt));
            blockEntity = blockEntity2;
        }
        catch (Throwable throwable) {
            try {
                try {
                    logging.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Throwable throwable3) {
                LOGGER.error("Failed to load data for block entity {} for block {} at position {}", new Object[]{blockEntityType, pos, state, throwable3});
                return null;
            }
        }
        logging.close();
        return blockEntity;
    }

    public void markDirty() {
        if (this.world != null) {
            BlockEntity.markDirty((World)this.world, (BlockPos)this.pos, (BlockState)this.cachedState);
        }
    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getCachedState() {
        return this.cachedState;
    }

    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return null;
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return new NbtCompound();
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public void markRemoved() {
        this.removed = true;
    }

    public void cancelRemoval() {
        this.removed = false;
    }

    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        BlockEntity blockEntity = this;
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory)blockEntity;
            if (this.world != null) {
                ItemScatterer.spawn((World)this.world, (BlockPos)pos, (Inventory)inventory);
            }
        }
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        return false;
    }

    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Name", () -> this.getNameForReport());
        crashReportSection.add("Cached block", () -> ((BlockState)this.getCachedState()).toString());
        if (this.world == null) {
            crashReportSection.add("Block location", () -> String.valueOf(this.pos) + " (world missing)");
        } else {
            crashReportSection.add("Actual block", () -> ((BlockState)this.world.getBlockState(this.pos)).toString());
            CrashReportSection.addBlockLocation((CrashReportSection)crashReportSection, (HeightLimitView)this.world, (BlockPos)this.pos);
        }
    }

    public String getNameForReport() {
        return String.valueOf(Registries.BLOCK_ENTITY_TYPE.getId((Object)this.getType())) + " // " + this.getClass().getCanonicalName();
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    @Deprecated
    public void setCachedState(BlockState state) {
        this.validateSupports(state);
        this.cachedState = state;
    }

    protected void readComponents(ComponentsAccess components) {
    }

    public final void readComponents(ItemStack stack) {
        this.readComponents(stack.getDefaultComponents(), stack.getComponentChanges());
    }

    public final void readComponents(ComponentMap defaultComponents, ComponentChanges components) {
        HashSet<ComponentType> set = new HashSet<ComponentType>();
        set.add(DataComponentTypes.BLOCK_ENTITY_DATA);
        set.add(DataComponentTypes.BLOCK_STATE);
        MergedComponentMap componentMap = MergedComponentMap.create((ComponentMap)defaultComponents, (ComponentChanges)components);
        this.readComponents((ComponentsAccess)new /* Unavailable Anonymous Inner Class!! */);
        ComponentChanges componentChanges = components.withRemovedIf(set::contains);
        this.components = componentChanges.toAddedRemovedPair().added();
    }

    protected void addComponents(ComponentMap.Builder builder) {
    }

    @Deprecated
    public void removeFromCopiedStackData(WriteView view) {
    }

    public final ComponentMap createComponentMap() {
        ComponentMap.Builder builder = ComponentMap.builder();
        builder.addAll(this.components);
        this.addComponents(builder);
        return builder.build();
    }

    public ComponentMap getComponents() {
        return this.components;
    }

    public void setComponents(ComponentMap components) {
        this.components = components;
    }

    public static @Nullable Text tryParseCustomName(ReadView view, String key) {
        return view.read(key, TextCodecs.CODEC).orElse(null);
    }

    public ErrorReporter.Context getReporterContext() {
        return new ReporterContext(this);
    }

    public void registerTracking(ServerWorld world, DebugTrackable.Tracker tracker) {
    }
}

