/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

class BeeEntity.PollinateGoal
extends BeeEntity.NotAngryGoal {
    private static final int field_30300 = 400;
    private static final double field_30303 = 0.1;
    private static final int field_30304 = 25;
    private static final float field_30305 = 0.35f;
    private static final float field_30306 = 0.6f;
    private static final float field_30307 = 0.33333334f;
    private static final int field_52458 = 5;
    private int pollinationTicks;
    private int lastPollinationTick;
    private boolean running;
    private @Nullable Vec3d nextTarget;
    private int ticks;
    private static final int field_30308 = 600;
    private Long2LongOpenHashMap unreachableFlowerPosCache;

    BeeEntity.PollinateGoal() {
        super(BeeEntity.this);
        this.unreachableFlowerPosCache = new Long2LongOpenHashMap();
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canBeeStart() {
        if (BeeEntity.this.ticksUntilCanPollinate > 0) {
            return false;
        }
        if (BeeEntity.this.hasNectar()) {
            return false;
        }
        if (BeeEntity.this.getEntityWorld().isRaining()) {
            return false;
        }
        Optional<BlockPos> optional = this.getFlower();
        if (optional.isPresent()) {
            BeeEntity.this.flowerPos = optional.get();
            BeeEntity.this.navigation.startMovingTo((double)BeeEntity.this.flowerPos.getX() + 0.5, (double)BeeEntity.this.flowerPos.getY() + 0.5, (double)BeeEntity.this.flowerPos.getZ() + 0.5, 1.2f);
            return true;
        }
        BeeEntity.this.ticksUntilCanPollinate = MathHelper.nextInt(BeeEntity.this.random, 20, 60);
        return false;
    }

    @Override
    public boolean canBeeContinue() {
        if (!this.running) {
            return false;
        }
        if (!BeeEntity.this.hasFlower()) {
            return false;
        }
        if (BeeEntity.this.getEntityWorld().isRaining()) {
            return false;
        }
        if (this.completedPollination()) {
            return BeeEntity.this.random.nextFloat() < 0.2f;
        }
        return true;
    }

    private boolean completedPollination() {
        return this.pollinationTicks > 400;
    }

    boolean isRunning() {
        return this.running;
    }

    void cancel() {
        this.running = false;
    }

    @Override
    public void start() {
        this.pollinationTicks = 0;
        this.ticks = 0;
        this.lastPollinationTick = 0;
        this.running = true;
        BeeEntity.this.resetPollinationTicks();
    }

    @Override
    public void stop() {
        if (this.completedPollination()) {
            BeeEntity.this.setHasNectar(true);
        }
        this.running = false;
        BeeEntity.this.navigation.stop();
        BeeEntity.this.ticksUntilCanPollinate = 200;
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!BeeEntity.this.hasFlower()) {
            return;
        }
        ++this.ticks;
        if (this.ticks > 600) {
            BeeEntity.this.clearFlowerPos();
            this.running = false;
            BeeEntity.this.ticksUntilCanPollinate = 200;
            return;
        }
        Vec3d vec3d = Vec3d.ofBottomCenter(BeeEntity.this.flowerPos).add(0.0, 0.6f, 0.0);
        if (vec3d.distanceTo(BeeEntity.this.getEntityPos()) > 1.0) {
            this.nextTarget = vec3d;
            this.moveToNextTarget();
            return;
        }
        if (this.nextTarget == null) {
            this.nextTarget = vec3d;
        }
        boolean bl = BeeEntity.this.getEntityPos().distanceTo(this.nextTarget) <= 0.1;
        boolean bl2 = true;
        if (!bl && this.ticks > 600) {
            BeeEntity.this.clearFlowerPos();
            return;
        }
        if (bl) {
            boolean bl3;
            boolean bl4 = bl3 = BeeEntity.this.random.nextInt(25) == 0;
            if (bl3) {
                this.nextTarget = new Vec3d(vec3d.getX() + (double)this.getRandomOffset(), vec3d.getY(), vec3d.getZ() + (double)this.getRandomOffset());
                BeeEntity.this.navigation.stop();
            } else {
                bl2 = false;
            }
            BeeEntity.this.getLookControl().lookAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
        }
        if (bl2) {
            this.moveToNextTarget();
        }
        ++this.pollinationTicks;
        if (BeeEntity.this.random.nextFloat() < 0.05f && this.pollinationTicks > this.lastPollinationTick + 60) {
            this.lastPollinationTick = this.pollinationTicks;
            BeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0f, 1.0f);
        }
    }

    private void moveToNextTarget() {
        BeeEntity.this.getMoveControl().moveTo(this.nextTarget.getX(), this.nextTarget.getY(), this.nextTarget.getZ(), 0.35f);
    }

    private float getRandomOffset() {
        return (BeeEntity.this.random.nextFloat() * 2.0f - 1.0f) * 0.33333334f;
    }

    private Optional<BlockPos> getFlower() {
        Iterable<BlockPos> iterable = BlockPos.iterateOutwards(BeeEntity.this.getBlockPos(), 5, 5, 5);
        Long2LongOpenHashMap long2LongOpenHashMap = new Long2LongOpenHashMap();
        for (BlockPos blockPos : iterable) {
            long l = this.unreachableFlowerPosCache.getOrDefault(blockPos.asLong(), Long.MIN_VALUE);
            if (BeeEntity.this.getEntityWorld().getTime() < l) {
                long2LongOpenHashMap.put(blockPos.asLong(), l);
                continue;
            }
            if (!BeeEntity.isAttractive(BeeEntity.this.getEntityWorld().getBlockState(blockPos))) continue;
            Path path = BeeEntity.this.navigation.findPathTo(blockPos, 1);
            if (path != null && path.reachesTarget()) {
                return Optional.of(blockPos);
            }
            long2LongOpenHashMap.put(blockPos.asLong(), BeeEntity.this.getEntityWorld().getTime() + 600L);
        }
        this.unreachableFlowerPosCache = long2LongOpenHashMap;
        return Optional.empty();
    }
}
