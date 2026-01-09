package net.minecraft.network.packet;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record UnknownCustomPayload(Identifier id) implements CustomPayload {
   public UnknownCustomPayload(Identifier identifier) {
      this.id = identifier;
   }

   public static PacketCodec createCodec(Identifier id, int maxBytes) {
      return CustomPayload.codecOf((value, buf) -> {
      }, (buf) -> {
         int j = buf.readableBytes();
         if (j >= 0 && j <= maxBytes) {
            buf.skipBytes(j);
            return new UnknownCustomPayload(id);
         } else {
            throw new IllegalArgumentException("Payload may not be larger than " + maxBytes + " bytes");
         }
      });
   }

   public CustomPayload.Id getId() {
      return new CustomPayload.Id(this.id);
   }

   public Identifier id() {
      return this.id;
   }
}
