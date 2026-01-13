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
import net.minecraft.potion.Potion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.entry.RegistryEntry;

public record BrewedPotionCriterion.Conditions(Optional<LootContextPredicate> player, Optional<RegistryEntry<Potion>> potion) implements AbstractCriterion.Conditions
{
    public static final Codec<BrewedPotionCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BrewedPotionCriterion.Conditions::player), (App)Potion.CODEC.optionalFieldOf("potion").forGetter(BrewedPotionCriterion.Conditions::potion)).apply((Applicative)instance, BrewedPotionCriterion.Conditions::new));

    public static AdvancementCriterion<BrewedPotionCriterion.Conditions> any() {
        return Criteria.BREWED_POTION.create(new BrewedPotionCriterion.Conditions(Optional.empty(), Optional.empty()));
    }

    public boolean matches(RegistryEntry<Potion> potion) {
        return !this.potion.isPresent() || this.potion.get().equals(potion);
    }
}
