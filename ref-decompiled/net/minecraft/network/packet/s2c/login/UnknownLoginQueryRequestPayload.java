package net.minecraft.network.packet.s2c.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record UnknownLoginQueryRequestPayload(Identifier id) implements LoginQueryRequestPayload {
   public UnknownLoginQueryRequestPayload(Identifier identifier) {
      this.id = identifier;
   }

   public void write(PacketByteBuf buf) {
   }

   public Identifier id() {
      return this.id;
   }
}
