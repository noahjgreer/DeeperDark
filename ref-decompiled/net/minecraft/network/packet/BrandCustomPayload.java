package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record BrandCustomPayload(String brand) implements CustomPayload {
   public static final PacketCodec CODEC = CustomPayload.codecOf(BrandCustomPayload::write, BrandCustomPayload::new);
   public static final CustomPayload.Id ID = CustomPayload.id("brand");

   private BrandCustomPayload(PacketByteBuf buf) {
      this(buf.readString());
   }

   public BrandCustomPayload(String string) {
      this.brand = string;
   }

   private void write(PacketByteBuf buf) {
      buf.writeString(this.brand);
   }

   public CustomPayload.Id getId() {
      return ID;
   }

   public String brand() {
      return this.brand;
   }
}
