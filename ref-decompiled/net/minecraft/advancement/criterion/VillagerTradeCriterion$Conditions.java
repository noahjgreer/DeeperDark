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
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;

public record VillagerTradeCriterion.Conditions(Optional<LootContextPredicate> player, Optional<LootContextPredicate> villager, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions
{
    public static final Codec<VillagerTradeCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(VillagerTradeCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("villager").forGetter(VillagerTradeCriterion.Conditions::villager), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(VillagerTradeCriterion.Conditions::item)).apply((Applicative)instance, VillagerTradeCriterion.Conditions::new));

    public static AdvancementCriterion<VillagerTradeCriterion.Conditions> any() {
        return Criteria.VILLAGER_TRADE.create(new VillagerTradeCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
    }

    public static AdvancementCriterion<VillagerTradeCriterion.Conditions> create(EntityPredicate.Builder playerPredicate) {
        return Criteria.VILLAGER_TRADE.create(new VillagerTradeCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(playerPredicate)), Optional.empty(), Optional.empty()));
    }

    public boolean matches(LootContext villager, ItemStack stack) {
        if (this.villager.isPresent() && !this.villager.get().test(villager)) {
            return false;
        }
        return !this.item.isPresent() || this.item.get().test(stack);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.villager, "villager");
    }
}
