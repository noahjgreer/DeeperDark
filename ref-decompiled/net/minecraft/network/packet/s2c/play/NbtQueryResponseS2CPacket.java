package net.minecraft.network.packet.s2c.play;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import org.jetbrains.annotations.Nullable;

public class NbtQueryResponseS2CPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(NbtQueryResponseS2CPacket::write, NbtQueryResponseS2CPacket::new);
   private final int transactionId;
   @Nullable
   private final NbtCompound nbt;

   public NbtQueryResponseS2CPacket(int transactionId, @Nullable NbtCompound nbt) {
      this.transactionId = transactionId;
      this.nbt = nbt;
   }

   private NbtQueryResponseS2CPacket(PacketByteBuf buf) {
      this.transactionId = buf.readVarInt();
      this.nbt = buf.readNbt();
   }

   private void write(PacketByteBuf buf) {
      buf.writeVarInt(this.transactionId);
      buf.writeNbt(this.nbt);
   }

   public PacketType getPacketType() {
      return PlayPackets.TAG_QUERY;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onNbtQueryResponse(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   public NbtCompound getNbt() {
      return this.nbt;
   }

   public boolean isWritingErrorSkippable() {
      return true;
   }
}
