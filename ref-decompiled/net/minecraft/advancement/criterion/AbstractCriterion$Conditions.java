/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.advancement.criterion;

import java.util.Optional;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public static interface AbstractCriterion.Conditions
extends CriterionConditions {
    @Override
    default public void validate(LootContextPredicateValidator validator) {
        validator.validateEntityPredicate(this.player(), "player");
    }

    public Optional<LootContextPredicate> player();
}
