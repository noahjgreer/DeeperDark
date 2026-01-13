/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.ChangedDimensionCriterion
 *  net.minecraft.advancement.criterion.ChangedDimensionCriterion$Conditions
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.world.World
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class ChangedDimensionCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, RegistryKey<World> from, RegistryKey<World> to) {
        this.trigger(player, conditions -> conditions.matches(from, to));
    }
}

