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
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jspecify.annotations.Nullable;

public record EffectsChangedCriterion.Conditions(Optional<LootContextPredicate> player, Optional<EntityEffectPredicate> effects, Optional<LootContextPredicate> source) implements AbstractCriterion.Conditions
{
    public static final Codec<EffectsChangedCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EffectsChangedCriterion.Conditions::player), (App)EntityEffectPredicate.CODEC.optionalFieldOf("effects").forGetter(EffectsChangedCriterion.Conditions::effects), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("source").forGetter(EffectsChangedCriterion.Conditions::source)).apply((Applicative)instance, EffectsChangedCriterion.Conditions::new));

    public static AdvancementCriterion<EffectsChangedCriterion.Conditions> create(EntityEffectPredicate.Builder effects) {
        return Criteria.EFFECTS_CHANGED.create(new EffectsChangedCriterion.Conditions(Optional.empty(), effects.build(), Optional.empty()));
    }

    public static AdvancementCriterion<EffectsChangedCriterion.Conditions> create(EntityPredicate.Builder source) {
        return Criteria.EFFECTS_CHANGED.create(new EffectsChangedCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.asLootContextPredicate(source.build()))));
    }

    public boolean matches(ServerPlayerEntity player, @Nullable LootContext context) {
        if (this.effects.isPresent() && !this.effects.get().test(player)) {
            return false;
        }
        return !this.source.isPresent() || context != null && this.source.get().test(context);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.source, "source");
    }
}
