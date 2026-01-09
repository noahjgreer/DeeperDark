package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record MapColorComponent(int rgb) {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final MapColorComponent DEFAULT;

   public MapColorComponent(int i) {
      this.rgb = i;
   }

   public int rgb() {
      return this.rgb;
   }

   static {
      CODEC = Codec.INT.xmap(MapColorComponent::new, MapColorComponent::rgb);
      PACKET_CODEC = PacketCodecs.INTEGER.xmap(MapColorComponent::new, MapColorComponent::rgb);
      DEFAULT = new MapColorComponent(4603950);
   }
}
