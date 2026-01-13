/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import org.jspecify.annotations.Nullable;

public record KilledByArrowCriterion.Conditions(Optional<LootContextPredicate> player, List<LootContextPredicate> victims, NumberRange.IntRange uniqueEntityTypes, Optional<ItemPredicate> firedFromWeapon) implements AbstractCriterion.Conditions
{
    public static final Codec<KilledByArrowCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(KilledByArrowCriterion.Conditions::player), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(KilledByArrowCriterion.Conditions::victims), (App)NumberRange.IntRange.CODEC.optionalFieldOf("unique_entity_types", (Object)NumberRange.IntRange.ANY).forGetter(KilledByArrowCriterion.Conditions::uniqueEntityTypes), (App)ItemPredicate.CODEC.optionalFieldOf("fired_from_weapon").forGetter(KilledByArrowCriterion.Conditions::firedFromWeapon)).apply((Applicative)instance, KilledByArrowCriterion.Conditions::new));

    public static AdvancementCriterion<KilledByArrowCriterion.Conditions> createCrossbow(RegistryEntryLookup<Item> itemRegistry, EntityPredicate.Builder ... victims) {
        return Criteria.KILLED_BY_ARROW.create(new KilledByArrowCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicates(victims), NumberRange.IntRange.ANY, Optional.of(ItemPredicate.Builder.create().items(itemRegistry, Items.CROSSBOW).build())));
    }

    public static AdvancementCriterion<KilledByArrowCriterion.Conditions> createCrossbow(RegistryEntryLookup<Item> itemRegistry, NumberRange.IntRange uniqueEntityTypeCount) {
        return Criteria.KILLED_BY_ARROW.create(new KilledByArrowCriterion.Conditions(Optional.empty(), List.of(), uniqueEntityTypeCount, Optional.of(ItemPredicate.Builder.create().items(itemRegistry, Items.CROSSBOW).build())));
    }

    public boolean matches(Collection<LootContext> victimContexts, int uniqueEntityTypeCount, @Nullable ItemStack weapon) {
        if (this.firedFromWeapon.isPresent() && (weapon == null || !this.firedFromWeapon.get().test(weapon))) {
            return false;
        }
        if (!this.victims.isEmpty()) {
            ArrayList list = Lists.newArrayList(victimContexts);
            for (LootContextPredicate lootContextPredicate : this.victims) {
                boolean bl = false;
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    LootContext lootContext = (LootContext)iterator.next();
                    if (!lootContextPredicate.test(lootContext)) continue;
                    iterator.remove();
                    bl = true;
                    break;
                }
                if (bl) continue;
                return false;
            }
        }
        return this.uniqueEntityTypes.test(uniqueEntityTypeCount);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicates(this.victims, "victims");
    }
}
