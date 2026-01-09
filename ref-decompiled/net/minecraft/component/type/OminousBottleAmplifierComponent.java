package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public record OminousBottleAmplifierComponent(int value) implements Consumable, TooltipAppender {
   public static final int DURATION = 120000;
   public static final int MIN_VALUE = 0;
   public static final int MAX_VALUE = 4;
   public static final Codec CODEC = Codecs.rangedInt(0, 4).xmap(OminousBottleAmplifierComponent::new, OminousBottleAmplifierComponent::value);
   public static final PacketCodec PACKET_CODEC;

   public OminousBottleAmplifierComponent(int i) {
      this.value = i;
   }

   public void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable) {
      user.addStatusEffect(new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, this.value, false, false, true));
   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      List list = List.of(new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, this.value, false, false, true));
      PotionContentsComponent.buildTooltip(list, textConsumer, 1.0F, context.getUpdateTickRate());
   }

   public int value() {
      return this.value;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, OminousBottleAmplifierComponent::value, OminousBottleAmplifierComponent::new);
   }
}
