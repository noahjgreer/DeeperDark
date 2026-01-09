package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.Packet;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface SequencedPacketCreator {
   Packet predict(int sequence);
}
