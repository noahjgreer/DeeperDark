package net.minecraft.network.packet.s2c.play;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record EntityTrackerUpdateS2CPacket(int id, List trackedValues) implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(EntityTrackerUpdateS2CPacket::write, EntityTrackerUpdateS2CPacket::new);
   public static final int MARKER_ID = 255;

   private EntityTrackerUpdateS2CPacket(RegistryByteBuf buf) {
      this(buf.readVarInt(), read(buf));
   }

   public EntityTrackerUpdateS2CPacket(int id, List list) {
      this.id = id;
      this.trackedValues = list;
   }

   private static void write(List trackedValues, RegistryByteBuf buf) {
      Iterator var2 = trackedValues.iterator();

      while(var2.hasNext()) {
         DataTracker.SerializedEntry serializedEntry = (DataTracker.SerializedEntry)var2.next();
         serializedEntry.write(buf);
      }

      buf.writeByte(255);
   }

   private static List read(RegistryByteBuf buf) {
      List list = new ArrayList();

      short i;
      while((i = buf.readUnsignedByte()) != 255) {
         list.add(DataTracker.SerializedEntry.fromBuf(buf, i));
      }

      return list;
   }

   private void write(RegistryByteBuf buf) {
      buf.writeVarInt(this.id);
      write(this.trackedValues, buf);
   }

   public PacketType getPacketType() {
      return PlayPackets.SET_ENTITY_DATA;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onEntityTrackerUpdate(this);
   }

   public int id() {
      return this.id;
   }

   public List trackedValues() {
      return this.trackedValues;
   }
}
