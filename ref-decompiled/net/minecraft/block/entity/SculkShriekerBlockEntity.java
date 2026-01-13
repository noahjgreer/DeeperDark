/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.SculkShriekerBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.SculkShriekerBlockEntity
 *  net.minecraft.block.entity.SculkShriekerBlockEntity$VibrationCallback
 *  net.minecraft.block.entity.SculkShriekerWarningManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.LargeEntitySpawnHelper
 *  net.minecraft.entity.LargeEntitySpawnHelper$Requirements
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.mob.WardenEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  net.minecraft.world.event.Vibrations
 *  net.minecraft.world.event.Vibrations$Callback
 *  net.minecraft.world.event.Vibrations$ListenerData
 *  net.minecraft.world.event.Vibrations$VibrationListener
 *  net.minecraft.world.event.listener.GameEventListener
 *  net.minecraft.world.event.listener.GameEventListener$Holder
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.OptionalInt;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

public class SculkShriekerBlockEntity
extends BlockEntity
implements GameEventListener.Holder<Vibrations.VibrationListener>,
Vibrations {
    private static final int field_38750 = 10;
    private static final int WARDEN_SPAWN_TRIES = 20;
    private static final int WARDEN_SPAWN_HORIZONTAL_RANGE = 5;
    private static final int WARDEN_SPAWN_VERTICAL_RANGE = 6;
    private static final int DARKNESS_RANGE = 40;
    private static final int SHRIEK_DELAY = 90;
    private static final Int2ObjectMap<SoundEvent> WARNING_SOUNDS = (Int2ObjectMap)Util.make((Object)new Int2ObjectOpenHashMap(), warningSounds -> {
        warningSounds.put(1, (Object)SoundEvents.ENTITY_WARDEN_NEARBY_CLOSE);
        warningSounds.put(2, (Object)SoundEvents.ENTITY_WARDEN_NEARBY_CLOSER);
        warningSounds.put(3, (Object)SoundEvents.ENTITY_WARDEN_NEARBY_CLOSEST);
        warningSounds.put(4, (Object)SoundEvents.ENTITY_WARDEN_LISTENING_ANGRY);
    });
    private static final int DEFAULT_WARNING_LEVEL = 0;
    private int warningLevel = 0;
    private final Vibrations.Callback vibrationCallback = new VibrationCallback(this);
    private Vibrations.ListenerData vibrationListenerData = new Vibrations.ListenerData();
    private final Vibrations.VibrationListener vibrationListener = new Vibrations.VibrationListener((Vibrations)this);

    public SculkShriekerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SCULK_SHRIEKER, pos, state);
    }

    public Vibrations.ListenerData getVibrationListenerData() {
        return this.vibrationListenerData;
    }

    public Vibrations.Callback getVibrationCallback() {
        return this.vibrationCallback;
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.warningLevel = view.getInt("warning_level", 0);
        this.vibrationListenerData = view.read("listener", Vibrations.ListenerData.CODEC).orElseGet(Vibrations.ListenerData::new);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("warning_level", this.warningLevel);
        view.put("listener", Vibrations.ListenerData.CODEC, (Object)this.vibrationListenerData);
    }

    public static @Nullable ServerPlayerEntity findResponsiblePlayerFromEntity(@Nullable Entity entity) {
        ItemEntity itemEntity;
        ServerPlayerEntity serverPlayerEntity2;
        ProjectileEntity projectileEntity;
        Entity entity2;
        LivingEntity livingEntity;
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            return serverPlayerEntity;
        }
        if (entity != null && (livingEntity = entity.getControllingPassenger()) instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
            return serverPlayerEntity;
        }
        if (entity instanceof ProjectileEntity && (entity2 = (projectileEntity = (ProjectileEntity)entity).getOwner()) instanceof ServerPlayerEntity) {
            serverPlayerEntity2 = (ServerPlayerEntity)entity2;
            return serverPlayerEntity2;
        }
        if (entity instanceof ItemEntity && (entity2 = (itemEntity = (ItemEntity)entity).getOwner()) instanceof ServerPlayerEntity) {
            serverPlayerEntity2 = (ServerPlayerEntity)entity2;
            return serverPlayerEntity2;
        }
        return null;
    }

    public void shriek(ServerWorld world, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return;
        }
        BlockState blockState = this.getCachedState();
        if (((Boolean)blockState.get((Property)SculkShriekerBlock.SHRIEKING)).booleanValue()) {
            return;
        }
        this.warningLevel = 0;
        if (this.canWarn(world) && !this.trySyncWarningLevel(world, player)) {
            return;
        }
        this.shriek(world, (Entity)player);
    }

    private boolean trySyncWarningLevel(ServerWorld world, ServerPlayerEntity player) {
        OptionalInt optionalInt = SculkShriekerWarningManager.warnNearbyPlayers((ServerWorld)world, (BlockPos)this.getPos(), (ServerPlayerEntity)player);
        optionalInt.ifPresent(warningLevel -> {
            this.warningLevel = warningLevel;
        });
        return optionalInt.isPresent();
    }

    private void shriek(ServerWorld world, @Nullable Entity entity) {
        BlockPos blockPos = this.getPos();
        BlockState blockState = this.getCachedState();
        world.setBlockState(blockPos, (BlockState)blockState.with((Property)SculkShriekerBlock.SHRIEKING, (Comparable)Boolean.valueOf(true)), 2);
        world.scheduleBlockTick(blockPos, blockState.getBlock(), 90);
        world.syncWorldEvent(3007, blockPos, 0);
        world.emitGameEvent((RegistryEntry)GameEvent.SHRIEK, blockPos, GameEvent.Emitter.of((Entity)entity));
    }

    private boolean canWarn(ServerWorld world) {
        return (Boolean)this.getCachedState().get((Property)SculkShriekerBlock.CAN_SUMMON) != false && world.getDifficulty() != Difficulty.PEACEFUL && (Boolean)world.getGameRules().getValue(GameRules.SPAWN_WARDENS) != false;
    }

    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        World world;
        if (((Boolean)oldState.get((Property)SculkShriekerBlock.SHRIEKING)).booleanValue() && (world = this.world) instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            this.warn(serverWorld);
        }
    }

    public void warn(ServerWorld world) {
        if (this.canWarn(world) && this.warningLevel > 0) {
            if (!this.trySpawnWarden(world)) {
                this.playWarningSound((World)world);
            }
            WardenEntity.addDarknessToClosePlayers((ServerWorld)world, (Vec3d)Vec3d.ofCenter((Vec3i)this.getPos()), null, (int)40);
        }
    }

    private void playWarningSound(World world) {
        SoundEvent soundEvent = (SoundEvent)WARNING_SOUNDS.get(this.warningLevel);
        if (soundEvent != null) {
            BlockPos blockPos = this.getPos();
            int i = blockPos.getX() + MathHelper.nextBetween((Random)world.random, (int)-10, (int)10);
            int j = blockPos.getY() + MathHelper.nextBetween((Random)world.random, (int)-10, (int)10);
            int k = blockPos.getZ() + MathHelper.nextBetween((Random)world.random, (int)-10, (int)10);
            world.playSound(null, (double)i, (double)j, (double)k, soundEvent, SoundCategory.HOSTILE, 5.0f, 1.0f);
        }
    }

    private boolean trySpawnWarden(ServerWorld world) {
        if (this.warningLevel < 4) {
            return false;
        }
        return LargeEntitySpawnHelper.trySpawnAt((EntityType)EntityType.WARDEN, (SpawnReason)SpawnReason.TRIGGERED, (ServerWorld)world, (BlockPos)this.getPos(), (int)20, (int)5, (int)6, (LargeEntitySpawnHelper.Requirements)LargeEntitySpawnHelper.Requirements.WARDEN, (boolean)false).isPresent();
    }

    public Vibrations.VibrationListener getEventListener() {
        return this.vibrationListener;
    }

    public /* synthetic */ GameEventListener getEventListener() {
        return this.getEventListener();
    }
}

