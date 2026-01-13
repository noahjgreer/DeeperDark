/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.VillagerTradeCriterion
 *  net.minecraft.advancement.criterion.VillagerTradeCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.MerchantEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class VillagerTradeCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, MerchantEntity merchant, ItemStack stack) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)merchant);
        this.trigger(player, conditions -> conditions.matches(lootContext, stack));
    }
}

