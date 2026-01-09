package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record EnchantableComponent(int value) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.POSITIVE_INT.fieldOf("value").forGetter(EnchantableComponent::value)).apply(instance, EnchantableComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public EnchantableComponent(int i) {
      if (i <= 0) {
         throw new IllegalArgumentException("Enchantment value must be positive, but was " + i);
      } else {
         this.value = i;
      }
   }

   public int value() {
      return this.value;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, EnchantableComponent::value, EnchantableComponent::new);
   }
}
