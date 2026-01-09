package net.minecraft.item.consume;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.world.World;

public record ClearAllEffectsConsumeEffect() implements ConsumeEffect {
   public static final ClearAllEffectsConsumeEffect INSTANCE = new ClearAllEffectsConsumeEffect();
   public static final MapCodec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public ConsumeEffect.Type getType() {
      return ConsumeEffect.Type.CLEAR_ALL_EFFECTS;
   }

   public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
      return user.clearStatusEffects();
   }

   static {
      CODEC = MapCodec.unit(INSTANCE);
      PACKET_CODEC = PacketCodec.unit(INSTANCE);
   }
}
