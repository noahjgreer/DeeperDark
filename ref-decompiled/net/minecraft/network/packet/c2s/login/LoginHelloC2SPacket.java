package net.minecraft.network.packet.c2s.login;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record LoginHelloC2SPacket(String name, UUID profileId) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(LoginHelloC2SPacket::write, LoginHelloC2SPacket::new);

   private LoginHelloC2SPacket(PacketByteBuf buf) {
      this(buf.readString(16), buf.readUuid());
   }

   public LoginHelloC2SPacket(String string, UUID uUID) {
      this.name = string;
      this.profileId = uUID;
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.name, 16);
      buf.writeUuid(this.profileId);
   }

   public PacketType getPacketType() {
      return LoginPackets.HELLO_C2S;
   }

   public void apply(ServerLoginPacketListener serverLoginPacketListener) {
      serverLoginPacketListener.onHello(this);
   }

   public String name() {
      return this.name;
   }

   public UUID profileId() {
      return this.profileId;
   }
}
