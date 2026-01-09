package net.minecraft.util;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;

public enum Unit {
   INSTANCE;

   public static final Codec CODEC = Codec.unit(INSTANCE);
   public static final PacketCodec PACKET_CODEC = PacketCodec.unit(INSTANCE);

   // $FF: synthetic method
   private static Unit[] method_36588() {
      return new Unit[]{INSTANCE};
   }
}
