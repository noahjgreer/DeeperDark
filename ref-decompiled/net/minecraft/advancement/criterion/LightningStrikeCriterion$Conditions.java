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
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public record LightningStrikeCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> lightning, Optional<LootContextPredicate> bystander) implements AbstractCriterion.Conditions
{
    public static final Codec<LightningStrikeCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(LightningStrikeCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("lightning").forGetter(LightningStrikeCriterion.Conditions::lightning), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("bystander").forGetter(LightningStrikeCriterion.Conditions::bystander)).apply((Applicative)instance, LightningStrikeCriterion.Conditions::new));

    public static AdvancementCriterion<LightningStrikeCriterion.Conditions> create(Optional<EntityPredicate> lightning, Optional<EntityPredicate> bystander) {
        return Criteria.LIGHTNING_STRIKE.create(new LightningStrikeCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(lightning), EntityPredicate.contextPredicateFromEntityPredicate(bystander)));
    }

    public boolean test(LootContext lightning, List<LootContext> bystanders) {
        if (this.lightning.isPresent() && !this.lightning.get().test(lightning)) {
            return false;
        }
        if (this.bystander.isPresent()) {
            if (bystanders.stream().noneMatch(this.bystander.get()::test)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.lightning, "lightning");
        validator.validateEntityPredicate(this.bystander, "bystander");
    }
}
