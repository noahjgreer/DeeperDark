package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChanneledLightningCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return ChanneledLightningCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Collection victims) {
      List list = (List)victims.stream().map((entity) -> {
         return EntityPredicate.createAdvancementEntityLootContext(player, entity);
      }).collect(Collectors.toList());
      this.trigger(player, (conditions) -> {
         return conditions.matches(list);
      });
   }

   public static record Conditions(Optional player, List victims) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(Conditions::victims)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, List victims) {
         this.player = playerPredicate;
         this.victims = victims;
      }

      public static AdvancementCriterion create(EntityPredicate.Builder... victims) {
         return Criteria.CHANNELED_LIGHTNING.create(new Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicates(victims)));
      }

      public boolean matches(Collection victims) {
         Iterator var2 = this.victims.iterator();

         boolean bl;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            LootContextPredicate lootContextPredicate = (LootContextPredicate)var2.next();
            bl = false;
            Iterator var5 = victims.iterator();

            while(var5.hasNext()) {
               LootContext lootContext = (LootContext)var5.next();
               if (lootContextPredicate.test(lootContext)) {
                  bl = true;
                  break;
               }
            }
         } while(bl);

         return false;
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
   }
}
