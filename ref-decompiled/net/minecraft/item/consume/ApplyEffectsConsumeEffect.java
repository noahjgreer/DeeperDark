package net.minecraft.item.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;

public record ApplyEffectsConsumeEffect(List effects, float probability) implements ConsumeEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(StatusEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(ApplyEffectsConsumeEffect::effects), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(ApplyEffectsConsumeEffect::probability)).apply(instance, ApplyEffectsConsumeEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public ApplyEffectsConsumeEffect(StatusEffectInstance effect, float probability) {
      this(List.of(effect), probability);
   }

   public ApplyEffectsConsumeEffect(List effects) {
      this(effects, 1.0F);
   }

   public ApplyEffectsConsumeEffect(StatusEffectInstance effect) {
      this(effect, 1.0F);
   }

   public ApplyEffectsConsumeEffect(List list, float f) {
      this.effects = list;
      this.probability = f;
   }

   public ConsumeEffect.Type getType() {
      return ConsumeEffect.Type.APPLY_EFFECTS;
   }

   public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
      if (user.getRandom().nextFloat() >= this.probability) {
         return false;
      } else {
         boolean bl = false;
         Iterator var5 = this.effects.iterator();

         while(var5.hasNext()) {
            StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var5.next();
            if (user.addStatusEffect(new StatusEffectInstance(statusEffectInstance))) {
               bl = true;
            }
         }

         return bl;
      }
   }

   public List effects() {
      return this.effects;
   }

   public float probability() {
      return this.probability;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(StatusEffectInstance.PACKET_CODEC.collect(PacketCodecs.toList()), ApplyEffectsConsumeEffect::effects, PacketCodecs.FLOAT, ApplyEffectsConsumeEffect::probability, ApplyEffectsConsumeEffect::new);
   }
}
