package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class BannedPlayerList extends ServerConfigList {
   public BannedPlayerList(File file) {
      super(file);
   }

   protected ServerConfigEntry fromJson(JsonObject json) {
      return new BannedPlayerEntry(json);
   }

   public boolean contains(GameProfile profile) {
      return this.contains(profile);
   }

   public String[] getNames() {
      return (String[])this.values().stream().map(ServerConfigEntry::getKey).filter(Objects::nonNull).map(GameProfile::getName).toArray((i) -> {
         return new String[i];
      });
   }

   protected String toString(GameProfile gameProfile) {
      return gameProfile.getId().toString();
   }

   // $FF: synthetic method
   protected String toString(final Object profile) {
      return this.toString((GameProfile)profile);
   }
}
