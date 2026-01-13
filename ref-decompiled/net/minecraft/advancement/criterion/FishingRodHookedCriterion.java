/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.FishingRodHookedCriterion
 *  net.minecraft.advancement.criterion.FishingRodHookedCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.FishingBobberEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import java.util.Collection;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class FishingRodHookedCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack rod, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)(bobber.getHookedEntity() != null ? bobber.getHookedEntity() : bobber));
        this.trigger(player, conditions -> conditions.matches(rod, lootContext, fishingLoots));
    }
}

