package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class KilledByArrowCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return KilledByArrowCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Collection piercingKilledEntities, @Nullable ItemStack weapon) {
      List list = Lists.newArrayList();
      Set set = Sets.newHashSet();
      Iterator var6 = piercingKilledEntities.iterator();

      while(var6.hasNext()) {
         Entity entity = (Entity)var6.next();
         set.add(entity.getType());
         list.add(EntityPredicate.createAdvancementEntityLootContext(player, entity));
      }

      this.trigger(player, (conditions) -> {
         return conditions.matches(list, set.size(), weapon);
      });
   }

   public static record Conditions(Optional player, List victims, NumberRange.IntRange uniqueEntityTypes, Optional firedFromWeapon) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(Conditions::victims), NumberRange.IntRange.CODEC.optionalFieldOf("unique_entity_types", NumberRange.IntRange.ANY).forGetter(Conditions::uniqueEntityTypes), ItemPredicate.CODEC.optionalFieldOf("fired_from_weapon").forGetter(Conditions::firedFromWeapon)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, List victims, NumberRange.IntRange uniqueEntityTypes, Optional optional) {
         this.player = playerPredicate;
         this.victims = victims;
         this.uniqueEntityTypes = uniqueEntityTypes;
         this.firedFromWeapon = optional;
      }

      public static AdvancementCriterion createCrossbow(RegistryEntryLookup itemRegistry, EntityPredicate.Builder... victims) {
         return Criteria.KILLED_BY_ARROW.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicates(victims), NumberRange.IntRange.ANY, Optional.of(ItemPredicate.Builder.create().items(itemRegistry, Items.CROSSBOW).build())));
      }

      public static AdvancementCriterion createCrossbow(RegistryEntryLookup itemRegistry, NumberRange.IntRange uniqueEntityTypeCount) {
         return Criteria.KILLED_BY_ARROW.create(new Conditions(Optional.empty(), List.of(), uniqueEntityTypeCount, Optional.of(ItemPredicate.Builder.create().items(itemRegistry, Items.CROSSBOW).build())));
      }

      public boolean matches(Collection victimContexts, int uniqueEntityTypeCount, @Nullable ItemStack weapon) {
         if (this.firedFromWeapon.isPresent() && (weapon == null || !((ItemPredicate)this.firedFromWeapon.get()).test(weapon))) {
            return false;
         } else {
            if (!this.victims.isEmpty()) {
               List list = Lists.newArrayList(victimContexts);
               Iterator var5 = this.victims.iterator();

               while(var5.hasNext()) {
                  LootContextPredicate lootContextPredicate = (LootContextPredicate)var5.next();
                  boolean bl = false;
                  Iterator iterator = list.iterator();

                  while(iterator.hasNext()) {
                     LootContext lootContext = (LootContext)iterator.next();
                     if (lootContextPredicate.test(lootContext)) {
                        iterator.remove();
                        bl = true;
                        break;
                     }
                  }

                  if (!bl) {
                     return false;
                  }
               }
            }

            return this.uniqueEntityTypes.test(uniqueEntityTypeCount);
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicates(this.victims, "victims");
      }

      public Optional player() {
         return this.player;
      }

      public List victims() {
         return this.victims;
      }

      public NumberRange.IntRange uniqueEntityTypes() {
         return this.uniqueEntityTypes;
      }

      public Optional firedFromWeapon() {
         return this.firedFromWeapon;
      }
   }
}
