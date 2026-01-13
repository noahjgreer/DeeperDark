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

public record PlayerInteractedWithEntityCriterion.Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item, Optional<LootContextPredicate> entity) implements AbstractCriterion.Conditions
{
    public static final Codec<PlayerInteractedWithEntityCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(PlayerInteractedWithEntityCriterion.Conditions::player), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(PlayerInteractedWithEntityCriterion.Conditions::item), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(PlayerInteractedWithEntityCriterion.Conditions::entity)).apply((Applicative)instance, PlayerInteractedWithEntityCriterion.Conditions::new));

    public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> create(Optional<LootContextPredicate> playerPredicate, ItemPredicate.Builder item, Optional<LootContextPredicate> entity) {
        return Criteria.PLAYER_INTERACTED_WITH_ENTITY.create(new PlayerInteractedWithEntityCriterion.Conditions(playerPredicate, Optional.of(item.build()), entity));
    }

    public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> createPlayerShearedEquipment(Optional<LootContextPredicate> playerPredicate, ItemPredicate.Builder item, Optional<LootContextPredicate> entity) {
        return Criteria.PLAYER_SHEARED_EQUIPMENT.create(new PlayerInteractedWithEntityCriterion.Conditions(playerPredicate, Optional.of(item.build()), entity));
    }

    public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> createPlayerShearedEquipment(ItemPredicate.Builder item, Optional<LootContextPredicate> entity) {
        return Criteria.PLAYER_SHEARED_EQUIPMENT.create(new PlayerInteractedWithEntityCriterion.Conditions(Optional.empty(), Optional.of(item.build()), entity));
    }

    public static AdvancementCriterion<PlayerInteractedWithEntityCriterion.Conditions> create(ItemPredicate.Builder item, Optional<LootContextPredicate> entity) {
        return PlayerInteractedWithEntityCriterion.Conditions.create(Optional.empty(), item, entity);
    }

    public boolean test(ItemStack stack, LootContext entity) {
        if (this.item.isPresent() && !this.item.get().test(stack)) {
            return false;
        }
        return this.entity.isEmpty() || this.entity.get().test(entity);
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.entity, "entity");
    }
}
