package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public record SuspiciousStewEffectsComponent(List effects) implements Consumable, TooltipAppender {
   public static final SuspiciousStewEffectsComponent DEFAULT = new SuspiciousStewEffectsComponent(List.of());
   public static final int DEFAULT_DURATION = 160;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public SuspiciousStewEffectsComponent(List list) {
      this.effects = list;
   }

   public SuspiciousStewEffectsComponent with(StewEffect stewEffect) {
      return new SuspiciousStewEffectsComponent(Util.withAppended(this.effects, stewEffect));
   }

   public void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable) {
      Iterator var5 = this.effects.iterator();

      while(var5.hasNext()) {
         StewEffect stewEffect = (StewEffect)var5.next();
         user.addStatusEffect(stewEffect.createStatusEffectInstance());
      }

   }

   public void appendTooltip(Item.TooltipContext context, Consumer textConsumer, TooltipType type, ComponentsAccess components) {
      if (type.isCreative()) {
         List list = new ArrayList();
         Iterator var6 = this.effects.iterator();

         while(var6.hasNext()) {
            StewEffect stewEffect = (StewEffect)var6.next();
            list.add(stewEffect.createStatusEffectInstance());
         }

         PotionContentsComponent.buildTooltip(list, textConsumer, 1.0F, context.getUpdateTickRate());
      }

   }

   public List effects() {
      return this.effects;
   }

   static {
      CODEC = SuspiciousStewEffectsComponent.StewEffect.CODEC.listOf().xmap(SuspiciousStewEffectsComponent::new, SuspiciousStewEffectsComponent::effects);
      PACKET_CODEC = SuspiciousStewEffectsComponent.StewEffect.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(SuspiciousStewEffectsComponent::new, SuspiciousStewEffectsComponent::effects);
   }

   public static record StewEffect(RegistryEntry effect, int duration) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(StewEffect::effect), Codec.INT.lenientOptionalFieldOf("duration", 160).forGetter(StewEffect::duration)).apply(instance, StewEffect::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public StewEffect(RegistryEntry registryEntry, int i) {
         this.effect = registryEntry;
         this.duration = i;
      }

      public StatusEffectInstance createStatusEffectInstance() {
         return new StatusEffectInstance(this.effect, this.duration);
      }

      public RegistryEntry effect() {
         return this.effect;
      }

      public int duration() {
         return this.duration;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(StatusEffect.ENTRY_PACKET_CODEC, StewEffect::effect, PacketCodecs.VAR_INT, StewEffect::duration, StewEffect::new);
      }
   }
}
