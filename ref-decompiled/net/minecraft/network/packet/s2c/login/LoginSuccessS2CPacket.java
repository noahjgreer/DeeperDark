package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record LoginSuccessS2CPacket(GameProfile profile) implements Packet {
   public static final PacketCodec CODEC;

   public LoginSuccessS2CPacket(GameProfile gameProfile) {
      this.profile = gameProfile;
   }

   public PacketType getPacketType() {
      return LoginPackets.LOGIN_FINISHED;
   }

   public void apply(ClientLoginPacketListener clientLoginPacketListener) {
      clientLoginPacketListener.onSuccess(this);
   }

   public boolean transitionsNetworkState() {
      return true;
   }

   public GameProfile profile() {
      return this.profile;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.GAME_PROFILE, LoginSuccessS2CPacket::profile, LoginSuccessS2CPacket::new);
   }
}
