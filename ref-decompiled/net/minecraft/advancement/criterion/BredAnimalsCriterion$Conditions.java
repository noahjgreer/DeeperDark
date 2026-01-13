/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import org.jspecify.annotations.Nullable;

public record BredAnimalsCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> parent, Optional<LootContextPredicate> partner, Optional<LootContextPredicate> child) implements AbstractCriterion.Conditions
{
    public static final Codec<BredAnimalsCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BredAnimalsCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("parent").forGetter(BredAnimalsCriterion.Conditions::parent), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("partner").forGetter(BredAnimalsCriterion.Conditions::partner), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("child").forGetter(BredAnimalsCriterion.Conditions::child)).apply((Applicative)instance, BredAnimalsCriterion.Conditions::new));

    public static AdvancementCriterion<BredAnimalsCriterion.Conditions> any() {
        return Criteria.BRED_ANIMALS.create(new BredAnimalsCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    public static AdvancementCriterion<BredAnimalsCriterion.Conditions> create(EntityPredicate.Builder child) {
        return Criteria.BRED_ANIMALS.create(new BredAnimalsCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(child))));
    }

    public static AdvancementCriterion<BredAnimalsCriterion.Conditions> create(Optional<EntityPredicate> parent, Optional<EntityPredicate> partner, Optional<EntityPredicate> child) {
        return Criteria.BRED_ANIMALS.create(new BredAnimalsCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(parent), EntityPredicate.contextPredicateFromEntityPredicate(partner), EntityPredicate.contextPredicateFromEntityPredicate(child)));
    }

    public boolean matches(LootContext parentContext, LootContext partnerContext, @Nullable LootContext childContext) {
        if (this.child.isPresent() && (childContext == null || !this.child.get().test(childContext))) {
            return false;
        }
        return BredAnimalsCriterion.Conditions.parentMatches(this.parent, parentContext) && BredAnimalsCriterion.Conditions.parentMatches(this.partner, partnerContext) || BredAnimalsCriterion.Conditions.parentMatches(this.parent, partnerContext) && BredAnimalsCriterion.Conditions.parentMatches(this.partner, parentContext);
    }

    private static boolean parentMatches(Optional<LootContextPredicate> parent, LootContext parentContext) {
        return parent.isEmpty() || parent.get().test(parentContext);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.parent, "parent");
        validator.validateEntityPredicate(this.partner, "partner");
        validator.validateEntityPredicate(this.child, "child");
    }
}
