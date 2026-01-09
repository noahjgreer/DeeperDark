package net.minecraft.world.event;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface PositionSourceType {
   PositionSourceType BLOCK = register("block", new BlockPositionSource.Type());
   PositionSourceType ENTITY = register("entity", new EntityPositionSource.Type());

   MapCodec getCodec();

   PacketCodec getPacketCodec();

   static PositionSourceType register(String id, PositionSourceType positionSourceType) {
      return (PositionSourceType)Registry.register(Registries.POSITION_SOURCE_TYPE, (String)id, positionSourceType);
   }
}
