package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record ChangeItemDamageEnchantmentEffect(EnchantmentLevelBasedValue amount) implements EnchantmentEntityEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter((changeItemDamageEnchantmentEffect) -> {
         return changeItemDamageEnchantmentEffect.amount;
      })).apply(instance, ChangeItemDamageEnchantmentEffect::new);
   });

   public ChangeItemDamageEnchantmentEffect(EnchantmentLevelBasedValue enchantmentLevelBasedValue) {
      this.amount = enchantmentLevelBasedValue;
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
      ItemStack itemStack = context.stack();
      if (itemStack.contains(DataComponentTypes.MAX_DAMAGE) && itemStack.contains(DataComponentTypes.DAMAGE)) {
         LivingEntity var9 = context.owner();
         ServerPlayerEntity var10000;
         if (var9 instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var9;
            var10000 = serverPlayerEntity;
         } else {
            var10000 = null;
         }

         ServerPlayerEntity serverPlayerEntity2 = var10000;
         int i = (int)this.amount.getValue(level);
         itemStack.damage(i, world, serverPlayerEntity2, context.breakCallback());
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public EnchantmentLevelBasedValue amount() {
      return this.amount;
   }
}
