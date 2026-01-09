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
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;

public record ByCostEnchantmentProvider(RegistryEntryList enchantments, IntProvider cost) implements EnchantmentProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).fieldOf("enchantments").forGetter(ByCostEnchantmentProvider::enchantments), IntProvider.VALUE_CODEC.fieldOf("cost").forGetter(ByCostEnchantmentProvider::cost)).apply(instance, ByCostEnchantmentProvider::new);
   });

   public ByCostEnchantmentProvider(RegistryEntryList registryEntryList, IntProvider intProvider) {
      this.enchantments = registryEntryList;
      this.cost = intProvider;
   }

   public void provideEnchantments(ItemStack stack, ItemEnchantmentsComponent.Builder componentBuilder, Random random, LocalDifficulty localDifficulty) {
      List list = EnchantmentHelper.generateEnchantments(random, stack, this.cost.get(random), this.enchantments.stream());
      Iterator var6 = list.iterator();

      while(var6.hasNext()) {
         EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)var6.next();
         componentBuilder.add(enchantmentLevelEntry.enchantment(), enchantmentLevelEntry.level());
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntryList enchantments() {
      return this.enchantments;
   }

   public IntProvider cost() {
      return this.cost;
   }
}
