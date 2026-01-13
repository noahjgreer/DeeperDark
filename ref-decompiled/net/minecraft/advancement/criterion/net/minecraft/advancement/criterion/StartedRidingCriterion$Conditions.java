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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;

public record StartedRidingCriterion.Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions
{
    public static final Codec<StartedRidingCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(StartedRidingCriterion.Conditions::player)).apply((Applicative)instance, StartedRidingCriterion.Conditions::new));

    public static AdvancementCriterion<StartedRidingCriterion.Conditions> create(EntityPredicate.Builder player) {
        return Criteria.STARTED_RIDING.create(new StartedRidingCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(player))));
    }
}
