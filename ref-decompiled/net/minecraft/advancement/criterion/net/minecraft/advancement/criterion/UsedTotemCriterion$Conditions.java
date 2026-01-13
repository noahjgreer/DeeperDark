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
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;

public record UsedTotemCriterion.Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions
{
    public static final Codec<UsedTotemCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsedTotemCriterion.Conditions::player), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(UsedTotemCriterion.Conditions::item)).apply((Applicative)instance, UsedTotemCriterion.Conditions::new));

    public static AdvancementCriterion<UsedTotemCriterion.Conditions> create(ItemPredicate itemPredicate) {
        return Criteria.USED_TOTEM.create(new UsedTotemCriterion.Conditions(Optional.empty(), Optional.of(itemPredicate)));
    }

    public static AdvancementCriterion<UsedTotemCriterion.Conditions> create(RegistryEntryLookup<Item> itemRegistry, ItemConvertible item) {
        return Criteria.USED_TOTEM.create(new UsedTotemCriterion.Conditions(Optional.empty(), Optional.of(ItemPredicate.Builder.create().items(itemRegistry, item).build())));
    }

    public boolean matches(ItemStack stack) {
        return this.item.isEmpty() || this.item.get().test(stack);
    }
}
