/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.minecraft.advancement.PlayerAdvancementTracker
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.AbstractCriterion$Conditions
 *  net.minecraft.advancement.criterion.Criterion
 *  net.minecraft.advancement.criterion.Criterion$ConditionsContainer
 *  net.minecraft.entity.Entity
 *  net.minecraft.loot.context.LootContext
 *  net.minecraft.predicate.entity.EntityPredicate
 *  net.minecraft.predicate.entity.LootContextPredicate
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractCriterion<T extends Conditions>
implements Criterion<T> {
    private final Map<PlayerAdvancementTracker, Set<Criterion.ConditionsContainer<T>>> progressions = Maps.newIdentityHashMap();

    public final void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions) {
        this.progressions.computeIfAbsent(manager, managerx -> Sets.newHashSet()).add(conditions);
    }

    public final void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<T> conditions) {
        Set set = (Set)this.progressions.get(manager);
        if (set != null) {
            set.remove(conditions);
            if (set.isEmpty()) {
                this.progressions.remove(manager);
            }
        }
    }

    public final void endTracking(PlayerAdvancementTracker tracker) {
        this.progressions.remove(tracker);
    }

    protected void trigger(ServerPlayerEntity player, Predicate<T> predicate) {
        PlayerAdvancementTracker playerAdvancementTracker = player.getAdvancementTracker();
        Set set = (Set)this.progressions.get(playerAdvancementTracker);
        if (set == null || set.isEmpty()) {
            return;
        }
        LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext((ServerPlayerEntity)player, (Entity)player);
        List list = null;
        for (Criterion.ConditionsContainer conditionsContainer : set) {
            Optional optional;
            Conditions conditions = (Conditions)conditionsContainer.conditions();
            if (!predicate.test(conditions) || !(optional = conditions.player()).isEmpty() && !((LootContextPredicate)optional.get()).test(lootContext)) continue;
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(conditionsContainer);
        }
        if (list != null) {
            for (Criterion.ConditionsContainer conditionsContainer : list) {
                conditionsContainer.grant(playerAdvancementTracker);
            }
        }
    }
}

