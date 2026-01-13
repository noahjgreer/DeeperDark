/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.List;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.player.PlayerEntity;

class PolarBearEntity.ProtectBabiesGoal
extends ActiveTargetGoal<PlayerEntity> {
    public PolarBearEntity.ProtectBabiesGoal() {
        super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, null);
    }

    @Override
    public boolean canStart() {
        if (PolarBearEntity.this.isBaby()) {
            return false;
        }
        if (super.canStart()) {
            List<PolarBearEntity> list = PolarBearEntity.this.getEntityWorld().getNonSpectatingEntities(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().expand(8.0, 4.0, 8.0));
            for (PolarBearEntity polarBearEntity : list) {
                if (!polarBearEntity.isBaby()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected double getFollowRange() {
        return super.getFollowRange() * 0.5;
    }
}
