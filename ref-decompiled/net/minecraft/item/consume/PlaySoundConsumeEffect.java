package net.minecraft.item.consume;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public record PlaySoundConsumeEffect(RegistryEntry sound) implements ConsumeEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter(PlaySoundConsumeEffect::sound)).apply(instance, PlaySoundConsumeEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public PlaySoundConsumeEffect(RegistryEntry registryEntry) {
      this.sound = registryEntry;
   }

   public ConsumeEffect.Type getType() {
      return ConsumeEffect.Type.PLAY_SOUND;
   }

   public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
      world.playSound((Entity)null, user.getBlockPos(), (SoundEvent)this.sound.value(), user.getSoundCategory(), 1.0F, 1.0F);
      return true;
   }

   public RegistryEntry sound() {
      return this.sound;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(SoundEvent.ENTRY_PACKET_CODEC, PlaySoundConsumeEffect::sound, PlaySoundConsumeEffect::new);
   }
}
