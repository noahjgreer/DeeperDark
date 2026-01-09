package net.minecraft.network.packet.s2c.play;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import org.jetbrains.annotations.Nullable;

public record MapUpdateS2CPacket(MapIdComponent mapId, byte scale, boolean locked, Optional decorations, Optional updateData) implements Packet {
   public static final PacketCodec CODEC;

   public MapUpdateS2CPacket(MapIdComponent mapId, byte scale, boolean locked, @Nullable Collection decorations, @Nullable MapState.UpdateData updateData) {
      this(mapId, scale, locked, decorations != null ? Optional.of(List.copyOf(decorations)) : Optional.empty(), Optional.ofNullable(updateData));
   }

   public MapUpdateS2CPacket(MapIdComponent mapIdComponent, byte b, boolean bl, Optional optional, Optional optional2) {
      this.mapId = mapIdComponent;
      this.scale = b;
      this.locked = bl;
      this.decorations = optional;
      this.updateData = optional2;
   }

   public PacketType getPacketType() {
      return PlayPackets.MAP_ITEM_DATA;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onMapUpdate(this);
   }

   public void apply(MapState mapState) {
      Optional var10000 = this.decorations;
      Objects.requireNonNull(mapState);
      var10000.ifPresent(mapState::replaceDecorations);
      this.updateData.ifPresent((updateData) -> {
         updateData.setColorsTo(mapState);
      });
   }

   public MapIdComponent mapId() {
      return this.mapId;
   }

   public byte scale() {
      return this.scale;
   }

   public boolean locked() {
      return this.locked;
   }

   public Optional decorations() {
      return this.decorations;
   }

   public Optional updateData() {
      return this.updateData;
   }

   static {
      CODEC = PacketCodec.tuple(MapIdComponent.PACKET_CODEC, MapUpdateS2CPacket::mapId, PacketCodecs.BYTE, MapUpdateS2CPacket::scale, PacketCodecs.BOOLEAN, MapUpdateS2CPacket::locked, MapDecoration.CODEC.collect(PacketCodecs.toList()).collect(PacketCodecs::optional), MapUpdateS2CPacket::decorations, MapState.UpdateData.CODEC, MapUpdateS2CPacket::updateData, MapUpdateS2CPacket::new);
   }
}
