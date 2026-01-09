package net.minecraft.scoreboard.number;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;

public interface NumberFormatType {
   MapCodec getCodec();

   PacketCodec getPacketCodec();
}
