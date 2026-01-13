/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.passive.BeeEntity;

class BeeEntity.EnterHiveGoal
extends BeeEntity.NotAngryGoal {
    BeeEntity.EnterHiveGoal() {
        super(BeeEntity.this);
    }

    @Override
    public boolean canBeeStart() {
        BeehiveBlockEntity beehiveBlockEntity;
        if (BeeEntity.this.hivePos != null && BeeEntity.this.canEnterHive() && BeeEntity.this.hivePos.isWithinDistance(BeeEntity.this.getEntityPos(), 2.0) && (beehiveBlockEntity = BeeEntity.this.getHive()) != null) {
            if (beehiveBlockEntity.isFullOfBees()) {
                BeeEntity.this.hivePos = null;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBeeContinue() {
        return false;
    }

    @Override
    public void start() {
        BeehiveBlockEntity beehiveBlockEntity = BeeEntity.this.getHive();
        if (beehiveBlockEntity != null) {
            beehiveBlockEntity.tryEnterHive(BeeEntity.this);
        }
    }
}
