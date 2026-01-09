package net.minecraft.enchantment.provider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;

public record SingleEnchantmentProvider(RegistryEntry enchantment, IntProvider level) implements EnchantmentProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Enchantment.ENTRY_CODEC.fieldOf("enchantment").forGetter(SingleEnchantmentProvider::enchantment), IntProvider.VALUE_CODEC.fieldOf("level").forGetter(SingleEnchantmentProvider::level)).apply(instance, SingleEnchantmentProvider::new);
   });

   public SingleEnchantmentProvider(RegistryEntry registryEntry, IntProvider intProvider) {
      this.enchantment = registryEntry;
      this.level = intProvider;
   }

   public void provideEnchantments(ItemStack stack, ItemEnchantmentsComponent.Builder componentBuilder, Random random, LocalDifficulty localDifficulty) {
      componentBuilder.add(this.enchantment, MathHelper.clamp(this.level.get(random), ((Enchantment)this.enchantment.value()).getMinLevel(), ((Enchantment)this.enchantment.value()).getMaxLevel()));
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryEntry enchantment() {
      return this.enchantment;
   }

   public IntProvider level() {
      return this.level;
   }
}
