/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement.criterion;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.CriterionConditions;

public record Criterion.ConditionsContainer<T extends CriterionConditions>(T conditions, AdvancementEntry advancement, String id) {
    public void grant(PlayerAdvancementTracker tracker) {
        tracker.grantCriterion(this.advancement, this.id);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Criterion.ConditionsContainer.class, "trigger;advancement;criterion", "conditions", "advancement", "id"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Criterion.ConditionsContainer.class, "trigger;advancement;criterion", "conditions", "advancement", "id"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Criterion.ConditionsContainer.class, "trigger;advancement;criterion", "conditions", "advancement", "id"}, this, o);
    }
}
