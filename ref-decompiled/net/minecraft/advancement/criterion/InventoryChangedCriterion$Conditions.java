/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.advancement.criterion;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.entry.RegistryEntryList;

public record InventoryChangedCriterion.Conditions(Optional<LootContextPredicate> player, Slots slots, List<ItemPredicate> items) implements AbstractCriterion.Conditions
{
    public static final Codec<InventoryChangedCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(InventoryChangedCriterion.Conditions::player), (App)Slots.CODEC.optionalFieldOf("slots", (Object)Slots.ANY).forGetter(InventoryChangedCriterion.Conditions::slots), (App)ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(InventoryChangedCriterion.Conditions::items)).apply((Applicative)instance, InventoryChangedCriterion.Conditions::new));

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> items(ItemPredicate.Builder ... items) {
        return InventoryChangedCriterion.Conditions.items((ItemPredicate[])Stream.of(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> items(ItemPredicate ... items) {
        return Criteria.INVENTORY_CHANGED.create(new InventoryChangedCriterion.Conditions(Optional.empty(), Slots.ANY, List.of(items)));
    }

    public static AdvancementCriterion<InventoryChangedCriterion.Conditions> items(ItemConvertible ... items) {
        ItemPredicate[] itemPredicates = new ItemPredicate[items.length];
        for (int i = 0; i < items.length; ++i) {
            itemPredicates[i] = new ItemPredicate(Optional.of(RegistryEntryList.of(items[i].asItem().getRegistryEntry())), NumberRange.IntRange.ANY, ComponentsPredicate.EMPTY);
        }
        return InventoryChangedCriterion.Conditions.items(itemPredicates);
    }

    public boolean matches(PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied) {
        if (!this.slots.test(full, empty, occupied)) {
            return false;
        }
        if (this.items.isEmpty()) {
            return true;
        }
        if (this.items.size() == 1) {
            return !stack.isEmpty() && this.items.get(0).test(stack);
        }
        ObjectArrayList list = new ObjectArrayList(this.items);
        int i = inventory.size();
        for (int j = 0; j < i; ++j) {
            if (list.isEmpty()) {
                return true;
            }
            ItemStack itemStack = inventory.getStack(j);
            if (itemStack.isEmpty()) continue;
            list.removeIf(item -> item.test(itemStack));
        }
        return list.isEmpty();
    }

    public record Slots(NumberRange.IntRange occupied, NumberRange.IntRange full, NumberRange.IntRange empty) {
        public static final Codec<Slots> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NumberRange.IntRange.CODEC.optionalFieldOf("occupied", (Object)NumberRange.IntRange.ANY).forGetter(Slots::occupied), (App)NumberRange.IntRange.CODEC.optionalFieldOf("full", (Object)NumberRange.IntRange.ANY).forGetter(Slots::full), (App)NumberRange.IntRange.CODEC.optionalFieldOf("empty", (Object)NumberRange.IntRange.ANY).forGetter(Slots::empty)).apply((Applicative)instance, Slots::new));
        public static final Slots ANY = new Slots(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY);

        public boolean test(int full, int empty, int occupied) {
            if (!this.full.test(full)) {
                return false;
            }
            if (!this.empty.test(empty)) {
                return false;
            }
            return this.occupied.test(occupied);
        }
    }
}
