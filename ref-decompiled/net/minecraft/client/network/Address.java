package net.minecraft.client.network;

import java.net.InetSocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Address {
   String getHostName();

   String getHostAddress();

   int getPort();

   InetSocketAddress getInetSocketAddress();

   static Address create(final InetSocketAddress address) {
      return new Address() {
         public String getHostName() {
            return address.getAddress().getHostName();
         }

         public String getHostAddress() {
            return address.getAddress().getHostAddress();
         }

         public int getPort() {
            return address.getPort();
         }

         public InetSocketAddress getInetSocketAddress() {
            return address;
         }
      };
   }
}
