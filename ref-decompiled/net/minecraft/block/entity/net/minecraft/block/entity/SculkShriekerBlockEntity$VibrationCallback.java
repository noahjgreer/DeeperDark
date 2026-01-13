/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import org.jspecify.annotations.Nullable;

class SculkShriekerBlockEntity.VibrationCallback
implements Vibrations.Callback {
    private static final int RANGE = 8;
    private final PositionSource positionSource;

    public SculkShriekerBlockEntity.VibrationCallback() {
        this.positionSource = new BlockPositionSource(SculkShriekerBlockEntity.this.pos);
    }

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public TagKey<GameEvent> getTag() {
        return GameEventTags.SHRIEKER_CAN_LISTEN;
    }

    @Override
    public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter) {
        return SculkShriekerBlockEntity.this.getCachedState().get(SculkShriekerBlock.SHRIEKING) == false && SculkShriekerBlockEntity.findResponsiblePlayerFromEntity(emitter.sourceEntity()) != null;
    }

    @Override
    public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
        SculkShriekerBlockEntity.this.shriek(world, SculkShriekerBlockEntity.findResponsiblePlayerFromEntity(entity != null ? entity : sourceEntity));
    }

    @Override
    public void onListen() {
        SculkShriekerBlockEntity.this.markDirty();
    }

    @Override
    public boolean requiresTickingChunksAround() {
        return true;
    }
}
