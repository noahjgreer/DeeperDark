/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.CuredZombieVillagerCriterion
 *  net.minecraft.advancement.criterion.CuredZombieVillagerCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.ZombieEntity
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class CuredZombieVillagerCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ZombieEntity zombie, VillagerEntity villager) {
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)zombie);
        LootContext lootContext2 = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)villager);
        this.trigger(player, conditions -> conditions.matches(lootContext, lootContext2));
    }
}

