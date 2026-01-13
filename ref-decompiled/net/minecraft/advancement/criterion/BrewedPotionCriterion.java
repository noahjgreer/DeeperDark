/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.BrewedPotionCriterion
 *  net.minecraft.advancement.criterion.BrewedPotionCriterion$Conditions
 *  net.minecraft.potion.Potion
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BrewedPotionCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, RegistryEntry<Potion> potion) {
        this.trigger(player, conditions -> conditions.matches(potion));
    }
}

