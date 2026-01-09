package net.minecraft.server.network;

import java.util.function.Consumer;

public interface ServerPlayerConfigurationTask {
   void sendPacket(Consumer sender);

   Key getKey();

   public static record Key(String id) {
      public Key(String string) {
         this.id = string;
      }

      public String toString() {
         return this.id;
      }

      public String id() {
         return this.id;
      }
   }
}
