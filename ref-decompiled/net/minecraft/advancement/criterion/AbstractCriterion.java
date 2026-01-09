package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class AbstractCriterion implements Criterion {
   private final Map progressions = Maps.newIdentityHashMap();

   public final void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer conditions) {
      ((Set)this.progressions.computeIfAbsent(manager, (managerx) -> {
         return Sets.newHashSet();
      })).add(conditions);
   }

   public final void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer conditions) {
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

   protected void trigger(ServerPlayerEntity player, Predicate predicate) {
      PlayerAdvancementTracker playerAdvancementTracker = player.getAdvancementTracker();
      Set set = (Set)this.progressions.get(playerAdvancementTracker);
      if (set != null && !set.isEmpty()) {
         LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, player);
         List list = null;
         Iterator var7 = set.iterator();

         while(true) {
            Criterion.ConditionsContainer conditionsContainer;
            Optional optional;
            do {
               Conditions conditions;
               do {
                  if (!var7.hasNext()) {
                     if (list != null) {
                        var7 = list.iterator();

                        while(var7.hasNext()) {
                           conditionsContainer = (Criterion.ConditionsContainer)var7.next();
                           conditionsContainer.grant(playerAdvancementTracker);
                        }
                     }

                     return;
                  }

                  conditionsContainer = (Criterion.ConditionsContainer)var7.next();
                  conditions = (Conditions)conditionsContainer.conditions();
               } while(!predicate.test(conditions));

               optional = conditions.player();
            } while(!optional.isEmpty() && !((LootContextPredicate)optional.get()).test(lootContext));

            if (list == null) {
               list = Lists.newArrayList();
            }

            list.add(conditionsContainer);
         }
      }
   }

   public interface Conditions extends CriterionConditions {
      default void validate(LootContextPredicateValidator validator) {
         validator.validateEntityPredicate(this.player(), "player");
      }

      Optional player();
   }
}
