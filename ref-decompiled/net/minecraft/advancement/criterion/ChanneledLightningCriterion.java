/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.ChanneledLightningCriterion
 *  net.minecraft.advancement.criterion.ChanneledLightningCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChanneledLightningCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Collection<? extends Entity> victims) {
        List list = victims.stream().map(entity -> EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)entity)).collect(Collectors.toList());
        this.trigger(player, conditions -> conditions.matches((Collection)list));
    }
}

