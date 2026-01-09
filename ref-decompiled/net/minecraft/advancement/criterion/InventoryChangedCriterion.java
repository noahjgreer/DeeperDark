package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryChangedCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return InventoryChangedCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack) {
      int i = 0;
      int j = 0;
      int k = 0;

      for(int l = 0; l < inventory.size(); ++l) {
         ItemStack itemStack = inventory.getStack(l);
         if (itemStack.isEmpty()) {
            ++j;
         } else {
            ++k;
            if (itemStack.getCount() >= itemStack.getMaxCount()) {
               ++i;
            }
         }
      }

      this.trigger(player, inventory, stack, i, j, k);
   }

   private void trigger(ServerPlayerEntity player, PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(inventory, stack, full, empty, occupied);
      });
   }

   public static record Conditions(Optional player, Slots slots, List items) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), InventoryChangedCriterion.Conditions.Slots.CODEC.optionalFieldOf("slots", InventoryChangedCriterion.Conditions.Slots.ANY).forGetter(Conditions::slots), ItemPredicate.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(Conditions::items)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Slots slots, List list) {
         this.player = playerPredicate;
         this.slots = slots;
         this.items = list;
      }

      public static AdvancementCriterion items(ItemPredicate.Builder... items) {
         return items((ItemPredicate[])Stream.of(items).map(ItemPredicate.Builder::build).toArray((i) -> {
            return new ItemPredicate[i];
         }));
      }

      public static AdvancementCriterion items(ItemPredicate... items) {
         return Criteria.INVENTORY_CHANGED.create(new Conditions(Optional.empty(), InventoryChangedCriterion.Conditions.Slots.ANY, List.of(items)));
      }

      public static AdvancementCriterion items(ItemConvertible... items) {
         ItemPredicate[] itemPredicates = new ItemPredicate[items.length];

         for(int i = 0; i < items.length; ++i) {
            itemPredicates[i] = new ItemPredicate(Optional.of(RegistryEntryList.of(items[i].asItem().getRegistryEntry())), NumberRange.IntRange.ANY, ComponentsPredicate.EMPTY);
         }

         return items(itemPredicates);
      }

      public boolean matches(PlayerInventory inventory, ItemStack stack, int full, int empty, int occupied) {
         if (!this.slots.test(full, empty, occupied)) {
            return false;
         } else if (this.items.isEmpty()) {
            return true;
         } else if (this.items.size() != 1) {
            List list = new ObjectArrayList(this.items);
            int i = inventory.size();

            for(int j = 0; j < i; ++j) {
               if (list.isEmpty()) {
                  return true;
               }

               ItemStack itemStack = inventory.getStack(j);
               if (!itemStack.isEmpty()) {
                  list.removeIf((item) -> {
                     return item.test(itemStack);
                  });
               }
            }

            return list.isEmpty();
         } else {
            return !stack.isEmpty() && ((ItemPredicate)this.items.get(0)).test(stack);
         }
      }

      public Optional player() {
         return this.player;
      }

      public Slots slots() {
         return this.slots;
      }

      public List items() {
         return this.items;
      }

      public static record Slots(NumberRange.IntRange occupied, NumberRange.IntRange full, NumberRange.IntRange empty) {
         public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("occupied", NumberRange.IntRange.ANY).forGetter(Slots::occupied), NumberRange.IntRange.CODEC.optionalFieldOf("full", NumberRange.IntRange.ANY).forGetter(Slots::full), NumberRange.IntRange.CODEC.optionalFieldOf("empty", NumberRange.IntRange.ANY).forGetter(Slots::empty)).apply(instance, Slots::new);
         });
         public static final Slots ANY;

         public Slots(NumberRange.IntRange intRange, NumberRange.IntRange intRange2, NumberRange.IntRange intRange3) {
            this.occupied = intRange;
            this.full = intRange2;
            this.empty = intRange3;
         }

         public boolean test(int full, int empty, int occupied) {
            if (!this.full.test(full)) {
               return false;
            } else if (!this.empty.test(empty)) {
               return false;
            } else {
               return this.occupied.test(occupied);
            }
         }

         public NumberRange.IntRange occupied() {
            return this.occupied;
         }

         public NumberRange.IntRange full() {
            return this.full;
         }

         public NumberRange.IntRange empty() {
            return this.empty;
         }

         static {
            ANY = new Slots(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY);
         }
      }
   }
}
