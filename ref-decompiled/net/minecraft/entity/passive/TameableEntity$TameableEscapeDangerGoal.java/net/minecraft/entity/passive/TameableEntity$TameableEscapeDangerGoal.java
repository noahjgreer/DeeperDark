/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.TagKey;

public class TameableEntity.TameableEscapeDangerGoal
extends EscapeDangerGoal {
    public TameableEntity.TameableEscapeDangerGoal(double speed, TagKey<DamageType> dangerousDamageTypes) {
        super((PathAwareEntity)TameableEntity.this, speed, dangerousDamageTypes);
    }

    public TameableEntity.TameableEscapeDangerGoal(double speed) {
        super(TameableEntity.this, speed);
    }

    @Override
    public void tick() {
        if (!TameableEntity.this.cannotFollowOwner() && TameableEntity.this.shouldTryTeleportToOwner()) {
            TameableEntity.this.tryTeleportToOwner();
        }
        super.tick();
    }
}
