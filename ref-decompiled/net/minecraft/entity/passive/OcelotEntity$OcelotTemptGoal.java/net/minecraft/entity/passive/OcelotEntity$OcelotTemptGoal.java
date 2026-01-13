/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.item.ItemStack;

static class OcelotEntity.OcelotTemptGoal
extends TemptGoal {
    private final OcelotEntity ocelot;

    public OcelotEntity.OcelotTemptGoal(OcelotEntity ocelot, double speed, Predicate<ItemStack> foodPredicate, boolean canBeScared) {
        super(ocelot, speed, foodPredicate, canBeScared);
        this.ocelot = ocelot;
    }

    @Override
    protected boolean canBeScared() {
        return super.canBeScared() && !this.ocelot.isTrusting();
    }
}
