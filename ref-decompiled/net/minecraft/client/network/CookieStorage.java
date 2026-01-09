package net.minecraft.client.network;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record CookieStorage(Map cookies) {
   public CookieStorage(Map map) {
      this.cookies = map;
   }

   public Map cookies() {
      return this.cookies;
   }
}
