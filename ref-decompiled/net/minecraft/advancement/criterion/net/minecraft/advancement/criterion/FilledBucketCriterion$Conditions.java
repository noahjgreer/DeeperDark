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

public record FilledBucketCriterion.Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions
{
    public static final Codec<FilledBucketCriterion.Conditions> CODEC = RecordCodecBuilder.create((T instance) -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(FilledBucketCriterion.Conditions::player), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FilledBucketCriterion.Conditions::item)).apply((Applicative)instance, FilledBucketCriterion.Conditions::new));

    public static AdvancementCriterion<FilledBucketCriterion.Conditions> create(ItemPredicate.Builder item) {
        return Criteria.FILLED_BUCKET.create(new FilledBucketCriterion.Conditions(Optional.empty(), Optional.of(item.build())));
    }

    public boolean matches(ItemStack stack) {
        return !this.item.isPresent() || this.item.get().test(stack);
    }
}
