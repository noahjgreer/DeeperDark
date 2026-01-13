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

public record SummonedEntityCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> entity) implements AbstractCriterion.Conditions
{
    public static final Codec<SummonedEntityCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SummonedEntityCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(SummonedEntityCriterion.Conditions::entity)).apply((Applicative)instance, SummonedEntityCriterion.Conditions::new));

    public static AdvancementCriterion<SummonedEntityCriterion.Conditions> create(EntityPredicate.Builder summonedEntityPredicateBuilder) {
        return Criteria.SUMMONED_ENTITY.create(new SummonedEntityCriterion.Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(summonedEntityPredicateBuilder))));
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
