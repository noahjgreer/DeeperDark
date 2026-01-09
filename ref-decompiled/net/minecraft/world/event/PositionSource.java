package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public interface PositionSource {
   Codec CODEC = Registries.POSITION_SOURCE_TYPE.getCodec().dispatch(PositionSource::getType, PositionSourceType::getCodec);
   PacketCodec PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.POSITION_SOURCE_TYPE).dispatch(PositionSource::getType, PositionSourceType::getPacketCodec);

   Optional getPos(World world);

   PositionSourceType getType();
}
