/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.KilledByArrowCriterion
 *  net.minecraft.advancement.criterion.KilledByArrowCriterion$Conditions
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.KilledByArrowCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jspecify.annotations.Nullable;

public class KilledByArrowCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Collection<Entity> piercingKilledEntities, @Nullable ItemStack weapon) {
        ArrayList list = Lists.newArrayList();
        HashSet set = Sets.newHashSet();
        for (Entity entity : piercingKilledEntities) {
            set.add(entity.getType());
            list.add(EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)entity));
        }
        this.trigger(player, conditions -> conditions.matches((Collection)list, set.size(), weapon));
    }
}

