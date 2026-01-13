/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.PlayerAdvancementTracker
 *  net.minecraft.advancement.criterion.Criterion
 *  net.minecraft.advancement.criterion.Criterion$ConditionsContainer
 *  net.minecraft.advancement.criterion.ImpossibleCriterion
 *  net.minecraft.advancement.criterion.ImpossibleCriterion$Conditions
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;

public class ImpossibleCriterion
implements Criterion<Conditions> {
    public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<Conditions> conditions) {
    }

    public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<Conditions> conditions) {
    }

    public void endTracking(PlayerAdvancementTracker tracker) {
    }

    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }
}

