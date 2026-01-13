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
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;

public record AnyBlockUseCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> location) implements AbstractCriterion.Conditions
{
    public static final Codec<AnyBlockUseCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(AnyBlockUseCriterion.Conditions::player), (App)LootContextPredicate.CODEC.optionalFieldOf("location").forGetter(AnyBlockUseCriterion.Conditions::location)).apply((Applicative)instance, AnyBlockUseCriterion.Conditions::new));

    public boolean test(LootContext location) {
        return this.location.isEmpty() || this.location.get().test(location);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        this.location.ifPresent(location -> validator.validate((LootContextPredicate)location, LootContextTypes.ADVANCEMENT_LOCATION, "location"));
    }
}
