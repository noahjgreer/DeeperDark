package net.minecraft.network.packet.s2c.login;

import com.mojang.serialization.JsonOps;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record LoginDisconnectS2CPacket(Text reason) implements Packet {
   private static final RegistryOps OPS;
   public static final PacketCodec CODEC;

   public LoginDisconnectS2CPacket(Text text) {
      this.reason = text;
   }

   public PacketType getPacketType() {
      return LoginPackets.LOGIN_DISCONNECT;
   }

   public void apply(ClientLoginPacketListener clientLoginPacketListener) {
      clientLoginPacketListener.onDisconnect(this);
   }

   public Text reason() {
      return this.reason;
   }

   static {
      OPS = DynamicRegistryManager.EMPTY.getOps(JsonOps.INSTANCE);
      CODEC = PacketCodec.tuple(PacketCodecs.lenientJson(262144).collect(PacketCodecs.fromCodec(OPS, TextCodecs.CODEC)), LoginDisconnectS2CPacket::reason, LoginDisconnectS2CPacket::new);
   }
}
