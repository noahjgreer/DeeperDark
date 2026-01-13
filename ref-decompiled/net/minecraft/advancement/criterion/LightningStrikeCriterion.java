/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.LightningStrikeCriterion
 *  net.minecraft.advancement.criterion.LightningStrikeCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LightningEntity
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.LightningStrikeCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class LightningStrikeCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, LightningEntity lightning, List<Entity> bystanders) {
        List list = bystanders.stream().map(bystander -> EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)bystander)).collect(Collectors.toList());
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)lightning);
        this.trigger(player, conditions -> conditions.test(lootContext, list));
    }
}

