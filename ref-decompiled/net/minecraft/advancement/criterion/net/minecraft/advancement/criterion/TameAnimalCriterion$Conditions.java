/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
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

public record TameAnimalCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> entity) implements AbstractCriterion.Conditions
{
    public static final Codec<TameAnimalCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(TameAnimalCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(TameAnimalCriterion.Conditions::entity)).apply((Applicative)instance, TameAnimalCriterion.Conditions::new));

    public static AdvancementCriterion<TameAnimalCriterion.Conditions> any() {
        return Criteria.TAME_ANIMAL.create(new TameAnimalCriterion.Conditions(Optional.empty(), Optional.empty()));
    }

    public static AdvancementCriterion<TameAnimalCriterion.Conditions> create(EntityPredicate.Builder entity) {
        return Criteria.TAME_ANIMAL.create(new TameAnimalCriterion.Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity))));
    }

    public boolean matches(LootContext entity) {
        return this.entity.isEmpty() || this.entity.get().test(entity);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.entity, "entity");
    }
}
