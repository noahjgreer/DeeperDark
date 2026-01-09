package net.minecraft.network.packet;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

public record PacketType(NetworkSide side, Identifier id) {
   public PacketType(NetworkSide networkSide, Identifier identifier) {
      this.side = networkSide;
      this.id = identifier;
   }

   public String toString() {
      String var10000 = this.side.getName();
      return var10000 + "/" + String.valueOf(this.id);
   }

   public NetworkSide side() {
      return this.side;
   }

   public Identifier id() {
      return this.id;
   }
}
