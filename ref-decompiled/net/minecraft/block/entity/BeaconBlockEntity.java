/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.minecraft.advancement.criterion.Criteria
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Stainable
 *  net.minecraft.block.entity.BeaconBlockEntity
 *  net.minecraft.block.entity.BeamEmitter
 *  net.minecraft.block.entity.BeamEmitter$BeamSegment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.LockableContainerBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.ContainerLock
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.screen.BeaconScreenHandler
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.PropertyDelegate
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ScreenHandlerContext
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextCodecs
 *  net.minecraft.util.Nameable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BeamEmitter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BeaconBlockEntity
extends BlockEntity
implements NamedScreenHandlerFactory,
Nameable,
BeamEmitter {
    private static final int MAX_LEVEL = 4;
    public static final List<List<RegistryEntry<StatusEffect>>> EFFECTS_BY_LEVEL = List.of(List.of(StatusEffects.SPEED, StatusEffects.HASTE), List.of(StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST), List.of(StatusEffects.STRENGTH), List.of(StatusEffects.REGENERATION));
    private static final Set<RegistryEntry<StatusEffect>> EFFECTS = EFFECTS_BY_LEVEL.stream().flatMap(Collection::stream).collect(Collectors.toSet());
    public static final int LEVEL_PROPERTY_INDEX = 0;
    public static final int PRIMARY_PROPERTY_INDEX = 1;
    public static final int SECONDARY_PROPERTY_INDEX = 2;
    public static final int PROPERTY_COUNT = 3;
    private static final int field_31305 = 10;
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.beacon");
    private static final String PRIMARY_EFFECT_NBT_KEY = "primary_effect";
    private static final String SECONDARY_EFFECT_NBT_KEY = "secondary_effect";
    List<BeamEmitter.BeamSegment> beamSegments = new ArrayList();
    private List<BeamEmitter.BeamSegment> field_19178 = new ArrayList();
    int level;
    private int minY;
    @Nullable RegistryEntry<StatusEffect> primary;
    @Nullable RegistryEntry<StatusEffect> secondary;
    private @Nullable Text customName;
    private ContainerLock lock = ContainerLock.EMPTY;
    private final PropertyDelegate propertyDelegate = new /* Unavailable Anonymous Inner Class!! */;

    static @Nullable RegistryEntry<StatusEffect> getEffectOrNull(@Nullable RegistryEntry<StatusEffect> effect) {
        return EFFECTS.contains(effect) ? effect : null;
    }

    public BeaconBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BEACON, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity) {
        int m;
        BlockPos blockPos;
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (blockEntity.minY < j) {
            blockPos = pos;
            blockEntity.field_19178 = Lists.newArrayList();
            blockEntity.minY = blockPos.getY() - 1;
        } else {
            blockPos = new BlockPos(i, blockEntity.minY + 1, k);
        }
        BeamEmitter.BeamSegment beamSegment = blockEntity.field_19178.isEmpty() ? null : (BeamEmitter.BeamSegment)blockEntity.field_19178.get(blockEntity.field_19178.size() - 1);
        int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);
        for (m = 0; m < 10 && blockPos.getY() <= l; ++m) {
            block18: {
                BlockState blockState;
                block16: {
                    int n;
                    block17: {
                        blockState = world.getBlockState(blockPos);
                        Block block = blockState.getBlock();
                        if (!(block instanceof Stainable)) break block16;
                        Stainable stainable = (Stainable)block;
                        n = stainable.getColor().getEntityColor();
                        if (blockEntity.field_19178.size() > 1) break block17;
                        beamSegment = new BeamEmitter.BeamSegment(n);
                        blockEntity.field_19178.add(beamSegment);
                        break block18;
                    }
                    if (beamSegment == null) break block18;
                    if (n == beamSegment.getColor()) {
                        beamSegment.increaseHeight();
                    } else {
                        beamSegment = new BeamEmitter.BeamSegment(ColorHelper.average((int)beamSegment.getColor(), (int)n));
                        blockEntity.field_19178.add(beamSegment);
                    }
                    break block18;
                }
                if (beamSegment != null && (blockState.getOpacity() < 15 || blockState.isOf(Blocks.BEDROCK))) {
                    beamSegment.increaseHeight();
                } else {
                    blockEntity.field_19178.clear();
                    blockEntity.minY = l;
                    break;
                }
            }
            blockPos = blockPos.up();
            ++blockEntity.minY;
        }
        m = blockEntity.level;
        if (world.getTime() % 80L == 0L) {
            if (!blockEntity.beamSegments.isEmpty()) {
                blockEntity.level = BeaconBlockEntity.updateLevel((World)world, (int)i, (int)j, (int)k);
            }
            if (blockEntity.level > 0 && !blockEntity.beamSegments.isEmpty()) {
                BeaconBlockEntity.applyPlayerEffects((World)world, (BlockPos)pos, (int)blockEntity.level, (RegistryEntry)blockEntity.primary, (RegistryEntry)blockEntity.secondary);
                BeaconBlockEntity.playSound((World)world, (BlockPos)pos, (SoundEvent)SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }
        if (blockEntity.minY >= l) {
            blockEntity.minY = world.getBottomY() - 1;
            boolean bl = m > 0;
            blockEntity.beamSegments = blockEntity.field_19178;
            if (!world.isClient()) {
                boolean bl2;
                boolean bl3 = bl2 = blockEntity.level > 0;
                if (!bl && bl2) {
                    BeaconBlockEntity.playSound((World)world, (BlockPos)pos, (SoundEvent)SoundEvents.BLOCK_BEACON_ACTIVATE);
                    for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, new Box((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).expand(10.0, 5.0, 10.0))) {
                        Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, blockEntity.level);
                    }
                } else if (bl && !bl2) {
                    BeaconBlockEntity.playSound((World)world, (BlockPos)pos, (SoundEvent)SoundEvents.BLOCK_BEACON_DEACTIVATE);
                }
            }
        }
    }

    private static int updateLevel(World world, int x, int y, int z) {
        int k;
        int i = 0;
        int j = 1;
        while (j <= 4 && (k = y - j) >= world.getBottomY()) {
            boolean bl = true;
            block1: for (int l = x - j; l <= x + j && bl; ++l) {
                for (int m = z - j; m <= z + j; ++m) {
                    if (world.getBlockState(new BlockPos(l, k, m)).isIn(BlockTags.BEACON_BASE_BLOCKS)) continue;
                    bl = false;
                    continue block1;
                }
            }
            if (!bl) break;
            i = j++;
        }
        return i;
    }

    public void markRemoved() {
        BeaconBlockEntity.playSound((World)this.world, (BlockPos)this.pos, (SoundEvent)SoundEvents.BLOCK_BEACON_DEACTIVATE);
        super.markRemoved();
    }

    private static void applyPlayerEffects(World world, BlockPos pos, int beaconLevel, @Nullable RegistryEntry<StatusEffect> primaryEffect, @Nullable RegistryEntry<StatusEffect> secondaryEffect) {
        if (world.isClient() || primaryEffect == null) {
            return;
        }
        double d = beaconLevel * 10 + 10;
        int i = 0;
        if (beaconLevel >= 4 && Objects.equals(primaryEffect, secondaryEffect)) {
            i = 1;
        }
        int j = (9 + beaconLevel * 2) * 20;
        Box box = new Box(pos).expand(d).stretch(0.0, (double)world.getHeight(), 0.0);
        List list = world.getNonSpectatingEntities(PlayerEntity.class, box);
        for (PlayerEntity playerEntity : list) {
            playerEntity.addStatusEffect(new StatusEffectInstance(primaryEffect, j, i, true, true));
        }
        if (beaconLevel >= 4 && !Objects.equals(primaryEffect, secondaryEffect) && secondaryEffect != null) {
            for (PlayerEntity playerEntity : list) {
                playerEntity.addStatusEffect(new StatusEffectInstance(secondaryEffect, j, 0, true, true));
            }
        }
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    public List<BeamEmitter.BeamSegment> getBeamSegments() {
        return this.level == 0 ? ImmutableList.of() : this.beamSegments;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createComponentlessNbt(registries);
    }

    private static void writeStatusEffect(WriteView view, String key, @Nullable RegistryEntry<StatusEffect> effect) {
        if (effect != null) {
            effect.getKey().ifPresent(entryKey -> view.putString(key, entryKey.getValue().toString()));
        }
    }

    private static @Nullable RegistryEntry<StatusEffect> readStatusEffect(ReadView view, String key) {
        return view.read(key, Registries.STATUS_EFFECT.getEntryCodec()).filter(EFFECTS::contains).orElse(null);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.primary = BeaconBlockEntity.readStatusEffect((ReadView)view, (String)"primary_effect");
        this.secondary = BeaconBlockEntity.readStatusEffect((ReadView)view, (String)"secondary_effect");
        this.customName = BeaconBlockEntity.tryParseCustomName((ReadView)view, (String)"CustomName");
        this.lock = ContainerLock.read((ReadView)view);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        BeaconBlockEntity.writeStatusEffect((WriteView)view, (String)"primary_effect", (RegistryEntry)this.primary);
        BeaconBlockEntity.writeStatusEffect((WriteView)view, (String)"secondary_effect", (RegistryEntry)this.secondary);
        view.putInt("Levels", this.level);
        view.putNullable("CustomName", TextCodecs.CODEC, (Object)this.customName);
        this.lock.write(view);
    }

    public void setCustomName(@Nullable Text customName) {
        this.customName = customName;
    }

    public @Nullable Text getCustomName() {
        return this.customName;
    }

    public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.lock.checkUnlocked(playerEntity)) {
            return new BeaconScreenHandler(i, (Inventory)playerInventory, this.propertyDelegate, ScreenHandlerContext.create((World)this.world, (BlockPos)this.getPos()));
        }
        LockableContainerBlockEntity.handleLocked((Vec3d)this.getPos().toCenterPos(), (PlayerEntity)playerEntity, (Text)this.getDisplayName());
        return null;
    }

    public Text getDisplayName() {
        return this.getName();
    }

    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return CONTAINER_NAME_TEXT;
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
        this.lock = (ContainerLock)components.getOrDefault(DataComponentTypes.LOCK, (Object)ContainerLock.EMPTY);
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CUSTOM_NAME, (Object)this.customName);
        if (!this.lock.equals((Object)ContainerLock.EMPTY)) {
            builder.add(DataComponentTypes.LOCK, (Object)this.lock);
        }
    }

    public void removeFromCopiedStackData(WriteView view) {
        view.remove("CustomName");
        view.remove("lock");
    }

    public void setWorld(World world) {
        super.setWorld(world);
        this.minY = world.getBottomY() - 1;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

