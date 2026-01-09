package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record TableBonusLootCondition(RegistryEntry enchantment, List chances) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(TableBonusLootCondition::enchantment), Codecs.nonEmptyList(Codec.FLOAT.listOf()).fieldOf("chances").forGetter(TableBonusLootCondition::chances)).apply(instance, TableBonusLootCondition::new);
   });

   public TableBonusLootCondition(RegistryEntry registryEntry, List list) {
      this.enchantment = registryEntry;
      this.chances = list;
   }

   public LootConditionType getType() {
      return LootConditionTypes.TABLE_BONUS;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.TOOL);
   }

   public boolean test(LootContext lootContext) {
      ItemStack itemStack = (ItemStack)lootContext.get(LootContextParameters.TOOL);
      int i = itemStack != null ? EnchantmentHelper.getLevel(this.enchantment, itemStack) : 0;
      float f = (Float)this.chances.get(Math.min(i, this.chances.size() - 1));
      return lootContext.getRandom().nextFloat() < f;
   }

   public static LootCondition.Builder builder(RegistryEntry enchantment, float... chances) {
      List list = new ArrayList(chances.length);
      float[] var3 = chances;
      int var4 = chances.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float f = var3[var5];
         list.add(f);
      }

      return () -> {
         return new TableBonusLootCondition(enchantment, list);
      };
   }

   public RegistryEntry enchantment() {
      return this.enchantment;
   }

   public List chances() {
      return this.chances;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }
}
