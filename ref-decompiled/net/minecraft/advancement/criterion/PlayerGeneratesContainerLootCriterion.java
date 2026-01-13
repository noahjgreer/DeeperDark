/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion
 *  net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion$Conditions
 *  net.minecraft.loot.LootTable
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerGeneratesContainerLootCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, RegistryKey<LootTable> lootTable) {
        this.trigger(player, conditions -> conditions.test(lootTable));
    }
}

