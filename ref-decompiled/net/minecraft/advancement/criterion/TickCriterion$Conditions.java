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
import net.minecraft.item.Item;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;

public record TickCriterion.Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions
{
    public static final Codec<TickCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(TickCriterion.Conditions::player)).apply((Applicative)instance, TickCriterion.Conditions::new));

    public static AdvancementCriterion<TickCriterion.Conditions> createLocation(LocationPredicate.Builder location) {
        return Criteria.LOCATION.create(new TickCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().location(location)))));
    }

    public static AdvancementCriterion<TickCriterion.Conditions> createLocation(EntityPredicate.Builder entity) {
        return Criteria.LOCATION.create(new TickCriterion.Conditions(Optional.of(EntityPredicate.asLootContextPredicate(entity.build()))));
    }

    public static AdvancementCriterion<TickCriterion.Conditions> createLocation(Optional<EntityPredicate> entity) {
        return Criteria.LOCATION.create(new TickCriterion.Conditions(EntityPredicate.contextPredicateFromEntityPredicate(entity)));
    }

    public static AdvancementCriterion<TickCriterion.Conditions> createSleptInBed() {
        return Criteria.SLEPT_IN_BED.create(new TickCriterion.Conditions(Optional.empty()));
    }

    public static AdvancementCriterion<TickCriterion.Conditions> createHeroOfTheVillage() {
        return Criteria.HERO_OF_THE_VILLAGE.create(new TickCriterion.Conditions(Optional.empty()));
    }

    public static AdvancementCriterion<TickCriterion.Conditions> createAvoidVibration() {
        return Criteria.AVOID_VIBRATION.create(new TickCriterion.Conditions(Optional.empty()));
    }

    public static AdvancementCriterion<TickCriterion.Conditions> createTick() {
        return Criteria.TICK.create(new TickCriterion.Conditions(Optional.empty()));
    }

    public static AdvancementCriterion<TickCriterion.Conditions> createLocation(RegistryEntryLookup<Block> blockRegistry, RegistryEntryLookup<Item> itemRegistry, Block steppingOn, Item boots) {
        return TickCriterion.Conditions.createLocation(EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(itemRegistry, boots))).steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(blockRegistry, steppingOn))));
    }
}
