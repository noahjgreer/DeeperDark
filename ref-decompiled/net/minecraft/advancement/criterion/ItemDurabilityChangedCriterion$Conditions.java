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

public record ItemDurabilityChangedCriterion.Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item, NumberRange.IntRange durability, NumberRange.IntRange delta) implements AbstractCriterion.Conditions
{
    public static final Codec<ItemDurabilityChangedCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ItemDurabilityChangedCriterion.Conditions::player), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(ItemDurabilityChangedCriterion.Conditions::item), (App)NumberRange.IntRange.CODEC.optionalFieldOf("durability", (Object)NumberRange.IntRange.ANY).forGetter(ItemDurabilityChangedCriterion.Conditions::durability), (App)NumberRange.IntRange.CODEC.optionalFieldOf("delta", (Object)NumberRange.IntRange.ANY).forGetter(ItemDurabilityChangedCriterion.Conditions::delta)).apply((Applicative)instance, ItemDurabilityChangedCriterion.Conditions::new));

    public static AdvancementCriterion<ItemDurabilityChangedCriterion.Conditions> create(Optional<ItemPredicate> item, NumberRange.IntRange durability) {
        return ItemDurabilityChangedCriterion.Conditions.create(Optional.empty(), item, durability);
    }

    public static AdvancementCriterion<ItemDurabilityChangedCriterion.Conditions> create(Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> item, NumberRange.IntRange durability) {
        return Criteria.ITEM_DURABILITY_CHANGED.create(new ItemDurabilityChangedCriterion.Conditions(playerPredicate, item, durability, NumberRange.IntRange.ANY));
    }

    public boolean matches(ItemStack stack, int durability) {
        if (this.item.isPresent() && !this.item.get().test(stack)) {
            return false;
        }
        if (!this.durability.test(stack.getMaxDamage() - durability)) {
            return false;
        }
        return this.delta.test(stack.getDamage() - durability);
    }
}
