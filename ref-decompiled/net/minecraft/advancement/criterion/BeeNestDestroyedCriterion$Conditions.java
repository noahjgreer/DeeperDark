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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public record BeeNestDestroyedCriterion.Conditions(Optional<LootContextPredicate> player, Optional<RegistryEntry<Block>> block, Optional<ItemPredicate> item, NumberRange.IntRange beesInside) implements AbstractCriterion.Conditions
{
    public static final Codec<BeeNestDestroyedCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(BeeNestDestroyedCriterion.Conditions::player), (App)Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(BeeNestDestroyedCriterion.Conditions::block), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(BeeNestDestroyedCriterion.Conditions::item), (App)NumberRange.IntRange.CODEC.optionalFieldOf("num_bees_inside", (Object)NumberRange.IntRange.ANY).forGetter(BeeNestDestroyedCriterion.Conditions::beesInside)).apply((Applicative)instance, BeeNestDestroyedCriterion.Conditions::new));

    public static AdvancementCriterion<BeeNestDestroyedCriterion.Conditions> create(Block block, ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange beeCountRange) {
        return Criteria.BEE_NEST_DESTROYED.create(new BeeNestDestroyedCriterion.Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.of(itemPredicateBuilder.build()), beeCountRange));
    }

    public boolean test(BlockState state, ItemStack stack, int count) {
        if (this.block.isPresent() && !state.isOf(this.block.get())) {
            return false;
        }
        if (this.item.isPresent() && !this.item.get().test(stack)) {
            return false;
        }
        return this.beesInside.test(count);
    }
}
