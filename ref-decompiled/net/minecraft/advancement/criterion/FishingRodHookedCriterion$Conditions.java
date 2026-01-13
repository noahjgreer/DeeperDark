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
import java.util.Collection;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;

public record FishingRodHookedCriterion.Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> rod, Optional<LootContextPredicate> entity, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions
{
    public static final Codec<FishingRodHookedCriterion.Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(FishingRodHookedCriterion.Conditions::player), (App)ItemPredicate.CODEC.optionalFieldOf("rod").forGetter(FishingRodHookedCriterion.Conditions::rod), (App)EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(FishingRodHookedCriterion.Conditions::entity), (App)ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FishingRodHookedCriterion.Conditions::item)).apply((Applicative)instance, FishingRodHookedCriterion.Conditions::new));

    public static AdvancementCriterion<FishingRodHookedCriterion.Conditions> create(Optional<ItemPredicate> rod, Optional<EntityPredicate> hookedEntity, Optional<ItemPredicate> caughtItem) {
        return Criteria.FISHING_ROD_HOOKED.create(new FishingRodHookedCriterion.Conditions(Optional.empty(), rod, EntityPredicate.contextPredicateFromEntityPredicate(hookedEntity), caughtItem));
    }

    public boolean matches(ItemStack rodStack, LootContext hookedEntity, Collection<ItemStack> fishingLoots) {
        if (this.rod.isPresent() && !this.rod.get().test(rodStack)) {
            return false;
        }
        if (this.entity.isPresent() && !this.entity.get().test(hookedEntity)) {
            return false;
        }
        if (this.item.isPresent()) {
            boolean bl = false;
            Entity entity = hookedEntity.get(LootContextParameters.THIS_ENTITY);
            if (entity instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity)entity;
                if (this.item.get().test(itemEntity.getStack())) {
                    bl = true;
                }
            }
            for (ItemStack itemStack : fishingLoots) {
                if (!this.item.get().test(itemStack)) continue;
                bl = true;
                break;
            }
            if (!bl) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void validate(LootContextPredicateValidator validator) {
        AbstractCriterion.Conditions.super.validate(validator);
        validator.validateEntityPredicate(this.entity, "entity");
    }
}
