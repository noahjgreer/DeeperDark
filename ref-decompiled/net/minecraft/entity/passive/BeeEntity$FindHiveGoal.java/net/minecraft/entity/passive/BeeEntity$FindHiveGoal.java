/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

class BeeEntity.FindHiveGoal
extends BeeEntity.NotAngryGoal {
    BeeEntity.FindHiveGoal() {
        super(BeeEntity.this);
    }

    @Override
    public boolean canBeeStart() {
        return BeeEntity.this.ticksLeftToFindHive == 0 && !BeeEntity.this.hasHivePos() && BeeEntity.this.canEnterHive();
    }

    @Override
    public boolean canBeeContinue() {
        return false;
    }

    @Override
    public void start() {
        BeeEntity.this.ticksLeftToFindHive = 200;
        List<BlockPos> list = this.getNearbyFreeHives();
        if (list.isEmpty()) {
            return;
        }
        for (BlockPos blockPos : list) {
            if (BeeEntity.this.moveToHiveGoal.isPossibleHive(blockPos)) continue;
            BeeEntity.this.hivePos = blockPos;
            return;
        }
        BeeEntity.this.moveToHiveGoal.clearPossibleHives();
        BeeEntity.this.hivePos = list.get(0);
    }

    private List<BlockPos> getNearbyFreeHives() {
        BlockPos blockPos = BeeEntity.this.getBlockPos();
        PointOfInterestStorage pointOfInterestStorage = ((ServerWorld)BeeEntity.this.getEntityWorld()).getPointOfInterestStorage();
        Stream<PointOfInterest> stream = pointOfInterestStorage.getInCircle(poiType -> poiType.isIn(PointOfInterestTypeTags.BEE_HOME), blockPos, 20, PointOfInterestStorage.OccupationStatus.ANY);
        return stream.map(PointOfInterest::getPos).filter(BeeEntity.this::doesHiveHaveSpace).sorted(Comparator.comparingDouble(blockPos2 -> blockPos2.getSquaredDistance(blockPos))).collect(Collectors.toList());
    }
}
