package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.predicate.component.ComponentSubPredicate;

public abstract class EnchantmentsPredicate implements ComponentSubPredicate {
   private final List enchantments;

   protected EnchantmentsPredicate(List enchantments) {
      this.enchantments = enchantments;
   }

   public static Codec createCodec(Function predicateFunction) {
      return EnchantmentPredicate.CODEC.listOf().xmap(predicateFunction, EnchantmentsPredicate::getEnchantments);
   }

   protected List getEnchantments() {
      return this.enchantments;
   }

   public boolean test(ItemEnchantmentsComponent itemEnchantmentsComponent) {
      Iterator var2 = this.enchantments.iterator();

      EnchantmentPredicate enchantmentPredicate;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         enchantmentPredicate = (EnchantmentPredicate)var2.next();
      } while(enchantmentPredicate.test(itemEnchantmentsComponent));

      return false;
   }

   public static Enchantments enchantments(List enchantments) {
      return new Enchantments(enchantments);
   }

   public static StoredEnchantments storedEnchantments(List storedEnchantments) {
      return new StoredEnchantments(storedEnchantments);
   }

   public static class Enchantments extends EnchantmentsPredicate {
      public static final Codec CODEC = createCodec(Enchantments::new);

      protected Enchantments(List list) {
         super(list);
      }

      public ComponentType getComponentType() {
         return DataComponentTypes.ENCHANTMENTS;
      }
   }

   public static class StoredEnchantments extends EnchantmentsPredicate {
      public static final Codec CODEC = createCodec(StoredEnchantments::new);

      protected StoredEnchantments(List list) {
         super(list);
      }

      public ComponentType getComponentType() {
         return DataComponentTypes.STORED_ENCHANTMENTS;
      }
   }
}
