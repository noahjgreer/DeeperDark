package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class AdvancementUpdateS2CPacket implements Packet {
   public static final PacketCodec CODEC = Packet.createCodec(AdvancementUpdateS2CPacket::write, AdvancementUpdateS2CPacket::new);
   private final boolean clearCurrent;
   private final List toEarn;
   private final Set toRemove;
   private final Map toSetProgress;
   private final boolean showToast;

   public AdvancementUpdateS2CPacket(boolean clearCurrent, Collection toEarn, Set toRemove, Map toSetProgress, boolean showToast) {
      this.clearCurrent = clearCurrent;
      this.toEarn = List.copyOf(toEarn);
      this.toRemove = Set.copyOf(toRemove);
      this.toSetProgress = Map.copyOf(toSetProgress);
      this.showToast = showToast;
   }

   private AdvancementUpdateS2CPacket(RegistryByteBuf buf) {
      this.clearCurrent = buf.readBoolean();
      this.toEarn = (List)AdvancementEntry.LIST_PACKET_CODEC.decode(buf);
      this.toRemove = (Set)buf.readCollection(Sets::newLinkedHashSetWithExpectedSize, PacketByteBuf::readIdentifier);
      this.toSetProgress = buf.readMap(PacketByteBuf::readIdentifier, AdvancementProgress::fromPacket);
      this.showToast = buf.readBoolean();
   }

   private void write(RegistryByteBuf buf) {
      buf.writeBoolean(this.clearCurrent);
      AdvancementEntry.LIST_PACKET_CODEC.encode(buf, this.toEarn);
      buf.writeCollection(this.toRemove, PacketByteBuf::writeIdentifier);
      buf.writeMap(this.toSetProgress, PacketByteBuf::writeIdentifier, (buf2, progress) -> {
         progress.toPacket(buf2);
      });
      buf.writeBoolean(this.showToast);
   }

   public PacketType getPacketType() {
      return PlayPackets.UPDATE_ADVANCEMENTS;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onAdvancements(this);
   }

   public List getAdvancementsToEarn() {
      return this.toEarn;
   }

   public Set getAdvancementIdsToRemove() {
      return this.toRemove;
   }

   public Map getAdvancementsToProgress() {
      return this.toSetProgress;
   }

   public boolean shouldClearCurrent() {
      return this.clearCurrent;
   }

   public boolean shouldShowToast() {
      return this.showToast;
   }
}
