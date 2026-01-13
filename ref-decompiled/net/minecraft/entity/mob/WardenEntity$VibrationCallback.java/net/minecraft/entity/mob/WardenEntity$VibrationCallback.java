/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.WardenBrain;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import org.jspecify.annotations.Nullable;

class WardenEntity.VibrationCallback
implements Vibrations.Callback {
    private static final int RANGE = 16;
    private final PositionSource positionSource;

    WardenEntity.VibrationCallback() {
        this.positionSource = new EntityPositionSource(WardenEntity.this, WardenEntity.this.getStandingEyeHeight());
    }

    @Override
    public int getRange() {
        return 16;
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public TagKey<GameEvent> getTag() {
        return GameEventTags.WARDEN_CAN_LISTEN;
    }

    @Override
    public boolean triggersAvoidCriterion() {
        return true;
    }

    @Override
    public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter) {
        LivingEntity livingEntity;
        if (WardenEntity.this.isAiDisabled() || WardenEntity.this.isDead() || WardenEntity.this.getBrain().hasMemoryModule(MemoryModuleType.VIBRATION_COOLDOWN) || WardenEntity.this.isDiggingOrEmerging() || !world.getWorldBorder().contains(pos)) {
            return false;
        }
        Entity entity = emitter.sourceEntity();
        return !(entity instanceof LivingEntity) || WardenEntity.this.isValidTarget(livingEntity = (LivingEntity)entity);
    }

    @Override
    public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
        if (WardenEntity.this.isDead()) {
            return;
        }
        WardenEntity.this.brain.remember(MemoryModuleType.VIBRATION_COOLDOWN, Unit.INSTANCE, 40L);
        world.sendEntityStatus(WardenEntity.this, (byte)61);
        WardenEntity.this.playSound(SoundEvents.ENTITY_WARDEN_TENDRIL_CLICKS, 5.0f, WardenEntity.this.getSoundPitch());
        BlockPos blockPos = pos;
        if (entity != null) {
            if (WardenEntity.this.isInRange(entity, 30.0)) {
                if (WardenEntity.this.getBrain().hasMemoryModule(MemoryModuleType.RECENT_PROJECTILE)) {
                    if (WardenEntity.this.isValidTarget(entity)) {
                        blockPos = entity.getBlockPos();
                    }
                    WardenEntity.this.increaseAngerAt(entity);
                } else {
                    WardenEntity.this.increaseAngerAt(entity, 10, true);
                }
            }
            WardenEntity.this.getBrain().remember(MemoryModuleType.RECENT_PROJECTILE, Unit.INSTANCE, 100L);
        } else {
            WardenEntity.this.increaseAngerAt(sourceEntity);
        }
        if (!WardenEntity.this.getAngriness().isAngry()) {
            Optional<LivingEntity> optional = WardenEntity.this.angerManager.getPrimeSuspect();
            if (entity != null || optional.isEmpty() || optional.get() == sourceEntity) {
                WardenBrain.lookAtDisturbance(WardenEntity.this, blockPos);
            }
        }
    }
}
