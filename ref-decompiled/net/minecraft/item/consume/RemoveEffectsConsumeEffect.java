package net.minecraft.item.consume;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.World;

public record RemoveEffectsConsumeEffect(RegistryEntryList effects) implements ConsumeEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.STATUS_EFFECT).fieldOf("effects").forGetter(RemoveEffectsConsumeEffect::effects)).apply(instance, RemoveEffectsConsumeEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public RemoveEffectsConsumeEffect(RegistryEntry effect) {
      this((RegistryEntryList)RegistryEntryList.of(effect));
   }

   public RemoveEffectsConsumeEffect(RegistryEntryList registryEntryList) {
      this.effects = registryEntryList;
   }

   public ConsumeEffect.Type getType() {
      return ConsumeEffect.Type.REMOVE_EFFECTS;
   }

   public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
      boolean bl = false;
      Iterator var5 = this.effects.iterator();

      while(var5.hasNext()) {
         RegistryEntry registryEntry = (RegistryEntry)var5.next();
         if (user.removeStatusEffect(registryEntry)) {
            bl = true;
         }
      }

      return bl;
   }

   public RegistryEntryList effects() {
      return this.effects;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntryList(RegistryKeys.STATUS_EFFECT), RemoveEffectsConsumeEffect::effects, RemoveEffectsConsumeEffect::new);
   }
}
