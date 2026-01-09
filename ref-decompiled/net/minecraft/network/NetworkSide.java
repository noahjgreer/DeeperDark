package net.minecraft.network;

public enum NetworkSide {
   SERVERBOUND("serverbound"),
   CLIENTBOUND("clientbound");

   private final String name;

   private NetworkSide(final String name) {
      this.name = name;
   }

   public NetworkSide getOpposite() {
      return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   private static NetworkSide[] method_36947() {
      return new NetworkSide[]{SERVERBOUND, CLIENTBOUND};
   }
}
