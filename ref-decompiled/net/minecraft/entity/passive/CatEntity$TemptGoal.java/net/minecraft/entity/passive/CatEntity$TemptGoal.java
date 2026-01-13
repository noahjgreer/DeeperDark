/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jspecify.annotations.Nullable;

static class CatEntity.TemptGoal
extends TemptGoal {
    private @Nullable PlayerEntity player;
    private final CatEntity cat;

    public CatEntity.TemptGoal(CatEntity cat, double speed, Predicate<ItemStack> foodPredicate, boolean canBeScared) {
        super(cat, speed, foodPredicate, canBeScared);
        this.cat = cat;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.player == null && this.mob.getRandom().nextInt(this.getTickCount(600)) == 0) {
            this.player = this.closestPlayer;
        } else if (this.mob.getRandom().nextInt(this.getTickCount(500)) == 0) {
            this.player = null;
        }
    }

    @Override
    protected boolean canBeScared() {
        if (this.player != null && this.player.equals(this.closestPlayer)) {
            return false;
        }
        return super.canBeScared();
    }

    @Override
    public boolean canStart() {
        return super.canStart() && !this.cat.isTamed();
    }
}
