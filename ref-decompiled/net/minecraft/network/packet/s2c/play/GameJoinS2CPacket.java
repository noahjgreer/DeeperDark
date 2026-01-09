package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.RegistryKeys;

public record GameJoinS2CPacket(int playerEntityId, boolean hardcore, Set dimensionIds, int maxPlayers, int viewDistance, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean doLimitedCrafting, CommonPlayerSpawnInfo commonPlayerSpawnInfo, boolean enforcesSecureChat) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(GameJoinS2CPacket::write, GameJoinS2CPacket::new);

   private GameJoinS2CPacket(RegistryByteBuf buf) {
      this(buf.readInt(), buf.readBoolean(), (Set)buf.readCollection(Sets::newHashSetWithExpectedSize, (b) -> {
         return b.readRegistryKey(RegistryKeys.WORLD);
      }), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), new CommonPlayerSpawnInfo(buf), buf.readBoolean());
   }

   public GameJoinS2CPacket(int playerEntityId, boolean bl, Set set, int i, int j, int k, boolean bl2, boolean bl3, boolean bl4, CommonPlayerSpawnInfo commonPlayerSpawnInfo, boolean bl5) {
      this.playerEntityId = playerEntityId;
      this.hardcore = bl;
      this.dimensionIds = set;
      this.maxPlayers = i;
      this.viewDistance = j;
      this.simulationDistance = k;
      this.reducedDebugInfo = bl2;
      this.showDeathScreen = bl3;
      this.doLimitedCrafting = bl4;
      this.commonPlayerSpawnInfo = commonPlayerSpawnInfo;
      this.enforcesSecureChat = bl5;
   }

   private void write(RegistryByteBuf buf) {
      buf.writeInt(this.playerEntityId);
      buf.writeBoolean(this.hardcore);
      buf.writeCollection(this.dimensionIds, PacketByteBuf::writeRegistryKey);
      buf.writeVarInt(this.maxPlayers);
      buf.writeVarInt(this.viewDistance);
      buf.writeVarInt(this.simulationDistance);
      buf.writeBoolean(this.reducedDebugInfo);
      buf.writeBoolean(this.showDeathScreen);
      buf.writeBoolean(this.doLimitedCrafting);
      this.commonPlayerSpawnInfo.write(buf);
      buf.writeBoolean(this.enforcesSecureChat);
   }

   public PacketType getPacketType() {
      return PlayPackets.LOGIN;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onGameJoin(this);
   }

   public int playerEntityId() {
      return this.playerEntityId;
   }

   public boolean hardcore() {
      return this.hardcore;
   }

   public Set dimensionIds() {
      return this.dimensionIds;
   }

   public int maxPlayers() {
      return this.maxPlayers;
   }

   public int viewDistance() {
      return this.viewDistance;
   }

   public int simulationDistance() {
      return this.simulationDistance;
   }

   public boolean reducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public boolean showDeathScreen() {
      return this.showDeathScreen;
   }

   public boolean doLimitedCrafting() {
      return this.doLimitedCrafting;
   }

   public CommonPlayerSpawnInfo commonPlayerSpawnInfo() {
      return this.commonPlayerSpawnInfo;
   }

   public boolean enforcesSecureChat() {
      return this.enforcesSecureChat;
   }
}
