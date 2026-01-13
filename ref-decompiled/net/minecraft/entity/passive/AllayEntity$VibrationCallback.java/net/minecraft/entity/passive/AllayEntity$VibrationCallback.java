/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.AllayBrain;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import org.jspecify.annotations.Nullable;

class AllayEntity.VibrationCallback
implements Vibrations.Callback {
    private static final int RANGE = 16;
    private final PositionSource positionSource;

    AllayEntity.VibrationCallback() {
        this.positionSource = new EntityPositionSource(AllayEntity.this, AllayEntity.this.getStandingEyeHeight());
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
    public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter) {
        if (AllayEntity.this.isAiDisabled()) {
            return false;
        }
        Optional<GlobalPos> optional = AllayEntity.this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LIKED_NOTEBLOCK);
        if (optional.isEmpty()) {
            return true;
        }
        GlobalPos globalPos = optional.get();
        return globalPos.isWithinRange(world.getRegistryKey(), AllayEntity.this.getBlockPos(), 1024) && globalPos.pos().equals(pos);
    }

    @Override
    public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
        if (event.matches(GameEvent.NOTE_BLOCK_PLAY)) {
            AllayBrain.rememberNoteBlock(AllayEntity.this, new BlockPos(pos));
        }
    }

    @Override
    public TagKey<GameEvent> getTag() {
        return GameEventTags.ALLAY_CAN_LISTEN;
    }
}
