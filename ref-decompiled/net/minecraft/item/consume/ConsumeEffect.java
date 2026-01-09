package net.minecraft.item.consume;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public interface ConsumeEffect {
   Codec CODEC = Registries.CONSUME_EFFECT_TYPE.getCodec().dispatch(ConsumeEffect::getType, Type::codec);
   PacketCodec PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.CONSUME_EFFECT_TYPE).dispatch(ConsumeEffect::getType, Type::streamCodec);

   Type getType();

   boolean onConsume(World world, ItemStack stack, LivingEntity user);

   public static record Type(MapCodec codec, PacketCodec streamCodec) {
      public static final Type APPLY_EFFECTS;
      public static final Type REMOVE_EFFECTS;
      public static final Type CLEAR_ALL_EFFECTS;
      public static final Type TELEPORT_RANDOMLY;
      public static final Type PLAY_SOUND;

      public Type(MapCodec mapCodec, PacketCodec packetCodec) {
         this.codec = mapCodec;
         this.streamCodec = packetCodec;
      }

      private static Type register(String id, MapCodec codec, PacketCodec packetCodec) {
         return (Type)Registry.register(Registries.CONSUME_EFFECT_TYPE, (String)id, new Type(codec, packetCodec));
      }

      public MapCodec codec() {
         return this.codec;
      }

      public PacketCodec streamCodec() {
         return this.streamCodec;
      }

      static {
         APPLY_EFFECTS = register("apply_effects", ApplyEffectsConsumeEffect.CODEC, ApplyEffectsConsumeEffect.PACKET_CODEC);
         REMOVE_EFFECTS = register("remove_effects", RemoveEffectsConsumeEffect.CODEC, RemoveEffectsConsumeEffect.PACKET_CODEC);
         CLEAR_ALL_EFFECTS = register("clear_all_effects", ClearAllEffectsConsumeEffect.CODEC, ClearAllEffectsConsumeEffect.PACKET_CODEC);
         TELEPORT_RANDOMLY = register("teleport_randomly", TeleportRandomlyConsumeEffect.CODEC, TeleportRandomlyConsumeEffect.PACKET_CODEC);
         PLAY_SOUND = register("play_sound", PlaySoundConsumeEffect.CODEC, PlaySoundConsumeEffect.PACKET_CODEC);
      }
   }
}
