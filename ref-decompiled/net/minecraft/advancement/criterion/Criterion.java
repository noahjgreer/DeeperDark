/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.AdvancementCriterion
 *  net.minecraft.advancement.PlayerAdvancementTracker
 *  net.minecraft.advancement.criterion.Criterion
 *  net.minecraft.advancement.criterion.Criterion$ConditionsContainer
 *  net.minecraft.advancement.criterion.CriterionConditions
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;

public interface Criterion<T extends CriterionConditions> {
    public void beginTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTrackingCondition(PlayerAdvancementTracker var1, ConditionsContainer<T> var2);

    public void endTracking(PlayerAdvancementTracker var1);

    public Codec<T> getConditionsCodec();

    default public AdvancementCriterion<T> create(T conditions) {
        return new AdvancementCriterion(this, conditions);
    }
}

