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
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;

public record UsingItemCriterion.Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions
{
    public static final Codec<UsingItemCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsingItemCriterion.Conditions::player), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsingItemCriterion.Conditions::item)).apply((Applicative)instance, UsingItemCriterion.Conditions::new));

    public static AdvancementCriterion<UsingItemCriterion.Conditions> create(EntityPredicate.Builder player, ItemPredicate.Builder item) {
        return Criteria.USING_ITEM.create(new UsingItemCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(player)), Optional.of(item.build())));
    }

    public boolean test(ItemStack stack) {
        return !this.item.isPresent() || this.item.get().test(stack);
    }
}
