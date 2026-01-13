/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

@Debug
public class BeeEntity.MoveToHiveGoal
extends BeeEntity.NotAngryGoal {
    public static final int field_30295 = 2400;
    int ticks;
    private static final int field_30296 = 3;
    final List<BlockPos> possibleHives;
    private @Nullable Path path;
    private static final int field_30297 = 60;
    private int ticksUntilLost;

    BeeEntity.MoveToHiveGoal() {
        super(BeeEntity.this);
        this.possibleHives = Lists.newArrayList();
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canBeeStart() {
        return BeeEntity.this.hivePos != null && !BeeEntity.this.isTooFar(BeeEntity.this.hivePos) && !BeeEntity.this.hasPositionTarget() && BeeEntity.this.canEnterHive() && !this.isCloseEnough(BeeEntity.this.hivePos) && BeeEntity.this.getEntityWorld().getBlockState(BeeEntity.this.hivePos).isIn(BlockTags.BEEHIVES);
    }

    @Override
    public boolean canBeeContinue() {
        return this.canBeeStart();
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.ticksUntilLost = 0;
        super.start();
    }

    @Override
    public void stop() {
        this.ticks = 0;
        this.ticksUntilLost = 0;
        BeeEntity.this.navigation.stop();
        BeeEntity.this.navigation.resetRangeMultiplier();
    }

    @Override
    public void tick() {
        if (BeeEntity.this.hivePos == null) {
            return;
        }
        ++this.ticks;
        if (this.ticks > this.getTickCount(2400)) {
            this.makeChosenHivePossibleHive();
            return;
        }
        if (BeeEntity.this.navigation.isFollowingPath()) {
            return;
        }
        if (BeeEntity.this.isWithinDistance(BeeEntity.this.hivePos, 16)) {
            boolean bl = this.startMovingToFar(BeeEntity.this.hivePos);
            if (!bl) {
                this.makeChosenHivePossibleHive();
            } else if (this.path != null && BeeEntity.this.navigation.getCurrentPath().equalsPath(this.path)) {
                ++this.ticksUntilLost;
                if (this.ticksUntilLost > 60) {
                    BeeEntity.this.clearHivePos();
                    this.ticksUntilLost = 0;
                }
            } else {
                this.path = BeeEntity.this.navigation.getCurrentPath();
            }
            return;
        }
        if (BeeEntity.this.isTooFar(BeeEntity.this.hivePos)) {
            BeeEntity.this.clearHivePos();
            return;
        }
        BeeEntity.this.startMovingTo(BeeEntity.this.hivePos);
    }

    private boolean startMovingToFar(BlockPos pos) {
        int i = BeeEntity.this.isWithinDistance(pos, 3) ? 1 : 2;
        BeeEntity.this.navigation.setRangeMultiplier(10.0f);
        BeeEntity.this.navigation.startMovingTo(pos.getX(), pos.getY(), pos.getZ(), i, 1.0);
        return BeeEntity.this.navigation.getCurrentPath() != null && BeeEntity.this.navigation.getCurrentPath().reachesTarget();
    }

    boolean isPossibleHive(BlockPos pos) {
        return this.possibleHives.contains(pos);
    }

    private void addPossibleHive(BlockPos pos) {
        this.possibleHives.add(pos);
        while (this.possibleHives.size() > 3) {
            this.possibleHives.remove(0);
        }
    }

    void clearPossibleHives() {
        this.possibleHives.clear();
    }

    private void makeChosenHivePossibleHive() {
        if (BeeEntity.this.hivePos != null) {
            this.addPossibleHive(BeeEntity.this.hivePos);
        }
        BeeEntity.this.clearHivePos();
    }

    private boolean isCloseEnough(BlockPos pos) {
        if (BeeEntity.this.isWithinDistance(pos, 2)) {
            return true;
        }
        Path path = BeeEntity.this.navigation.getCurrentPath();
        return path != null && path.getTarget().equals(pos) && path.reachesTarget() && path.isFinished();
    }
}
