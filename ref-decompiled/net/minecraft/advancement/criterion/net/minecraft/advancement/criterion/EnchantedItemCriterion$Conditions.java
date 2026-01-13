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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;

public record EnchantedItemCriterion.Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item, NumberRange.IntRange levels) implements AbstractCriterion.Conditions
{
    public static final Codec<EnchantedItemCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EnchantedItemCriterion.Conditions::player), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(EnchantedItemCriterion.Conditions::item), (App)NumberRange.IntRange.CODEC.optionalFieldOf("levels", (Object)NumberRange.IntRange.ANY).forGetter(EnchantedItemCriterion.Conditions::levels)).apply((Applicative)instance, EnchantedItemCriterion.Conditions::new));

    public static AdvancementCriterion<EnchantedItemCriterion.Conditions> any() {
        return Criteria.ENCHANTED_ITEM.create(new EnchantedItemCriterion.Conditions(Optional.empty(), Optional.empty(), NumberRange.IntRange.ANY));
    }

    public boolean matches(ItemStack stack, int levels) {
        if (this.item.isPresent() && !this.item.get().test(stack)) {
            return false;
        }
        return this.levels.test(levels);
    }
}
