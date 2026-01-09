package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class FishingRodHookedCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return FishingRodHookedCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, ItemStack rod, FishingBobberEntity bobber, Collection fishingLoots) {
      LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, (Entity)(bobber.getHookedEntity() != null ? bobber.getHookedEntity() : bobber));
      this.trigger(player, (conditions) -> {
         return conditions.matches(rod, lootContext, fishingLoots);
      });
   }

   public static record Conditions(Optional player, Optional rod, Optional entity, Optional item) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), ItemPredicate.CODEC.optionalFieldOf("rod").forGetter(Conditions::rod), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("entity").forGetter(Conditions::entity), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional rod, Optional hookedEntity, Optional caughtItem) {
         this.player = playerPredicate;
         this.rod = rod;
         this.entity = hookedEntity;
         this.item = caughtItem;
      }

      public static AdvancementCriterion create(Optional rod, Optional hookedEntity, Optional caughtItem) {
         return Criteria.FISHING_ROD_HOOKED.create(new Conditions(Optional.empty(), rod, EntityPredicate.contextPredicateFromEntityPredicate(hookedEntity), caughtItem));
      }

      public boolean matches(ItemStack rodStack, LootContext hookedEntity, Collection fishingLoots) {
         if (this.rod.isPresent() && !((ItemPredicate)this.rod.get()).test(rodStack)) {
            return false;
         } else if (this.entity.isPresent() && !((LootContextPredicate)this.entity.get()).test(hookedEntity)) {
            return false;
         } else {
            if (this.item.isPresent()) {
               boolean bl = false;
               Entity entity = (Entity)hookedEntity.get(LootContextParameters.THIS_ENTITY);
               if (entity instanceof ItemEntity) {
                  ItemEntity itemEntity = (ItemEntity)entity;
                  if (((ItemPredicate)this.item.get()).test(itemEntity.getStack())) {
                     bl = true;
                  }
               }

               Iterator var8 = fishingLoots.iterator();

               while(var8.hasNext()) {
                  ItemStack itemStack = (ItemStack)var8.next();
                  if (((ItemPredicate)this.item.get()).test(itemStack)) {
                     bl = true;
                     break;
                  }
               }

               if (!bl) {
                  return false;
               }
            }

            return true;
         }
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.entity, "entity");
      }

      public Optional player() {
         return this.player;
      }

      public Optional rod() {
         return this.rod;
      }

      public Optional entity() {
         return this.entity;
      }

      public Optional item() {
         return this.item;
      }
   }
}
