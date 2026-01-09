package net.minecraft.scoreboard.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;

public class NumberFormatTypes {
   public static final MapCodec REGISTRY_CODEC;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final PacketCodec OPTIONAL_PACKET_CODEC;

   public static NumberFormatType registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"blank", BlankNumberFormat.TYPE);
      Registry.register(registry, (String)"styled", StyledNumberFormat.TYPE);
      return (NumberFormatType)Registry.register(registry, (String)"fixed", FixedNumberFormat.TYPE);
   }

   static {
      REGISTRY_CODEC = Registries.NUMBER_FORMAT_TYPE.getCodec().dispatchMap(NumberFormat::getType, NumberFormatType::getCodec);
      CODEC = REGISTRY_CODEC.codec();
      PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.NUMBER_FORMAT_TYPE).dispatch(NumberFormat::getType, NumberFormatType::getPacketCodec);
      OPTIONAL_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs::optional);
   }
}
