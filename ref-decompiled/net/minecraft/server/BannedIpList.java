package net.minecraft.server;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import org.jetbrains.annotations.Nullable;

public class BannedIpList extends ServerConfigList {
   public BannedIpList(File file) {
      super(file);
   }

   protected ServerConfigEntry fromJson(JsonObject json) {
      return new BannedIpEntry(json);
   }

   public boolean isBanned(SocketAddress ip) {
      String string = this.stringifyAddress(ip);
      return this.contains(string);
   }

   public boolean isBanned(String ip) {
      return this.contains(ip);
   }

   @Nullable
   public BannedIpEntry get(SocketAddress address) {
      String string = this.stringifyAddress(address);
      return (BannedIpEntry)this.get(string);
   }

   private String stringifyAddress(SocketAddress address) {
      String string = address.toString();
      if (string.contains("/")) {
         string = string.substring(string.indexOf(47) + 1);
      }

      if (string.contains(":")) {
         string = string.substring(0, string.indexOf(58));
      }

      return string;
   }
}
