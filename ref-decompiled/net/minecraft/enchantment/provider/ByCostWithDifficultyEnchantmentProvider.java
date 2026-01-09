package net.minecraft.enchantment.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;

public record ByCostWithDifficultyEnchantmentProvider(RegistryEntryList enchantments, int minCost, int maxCostSpan) implements EnchantmentProvider {
   public static final int MAX_COST = 10000;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).fieldOf("enchantments").forGetter(ByCostWithDifficultyEnchantmentProvider::enchantments), Codecs.rangedInt(1, 10000).fieldOf("min_cost").forGetter(ByCostWithDifficultyEnchantmentProvider::minCost), Codecs.rangedInt(0, 10000).fieldOf("max_cost_span").forGetter(ByCostWithDifficultyEnchantmentProvider::maxCostSpan)).apply(instance, ByCostWithDifficultyEnchantmentProvider::new);
   });

   public ByCostWithDifficultyEnchantmentProvider(RegistryEntryList registryEntryList, int i, int j) {
      this.enchantments = registryEntryList;
      this.minCost = i;
      this.maxCostSpan = j;
   }

   public void provideEnchantments(ItemStack stack, ItemEnchantmentsComponent.Builder componentBuilder, Random random, LocalDifficulty localDifficulty) {
      float f = localDifficulty.getClampedLocalDifficulty();
      int i = MathHelper.nextBetween(random, this.minCost, this.minCost + (int)(f * (float)this.maxCostSpan));
      List list = EnchantmentHelper.generateEnchantments(random, stack, i, this.enchantments.stream());
      Iterator var8 = list.iterator();

      while(var8.hasNext()) {
         EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)var8.next();
         componentBuilder.add(enchantmentLevelEntry.enchantment(), enchantmentLevelEntry.level());
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntryList enchantments() {
      return this.enchantments;
   }

   public int minCost() {
      return this.minCost;
   }

   public int maxCostSpan() {
      return this.maxCostSpan;
   }
}
