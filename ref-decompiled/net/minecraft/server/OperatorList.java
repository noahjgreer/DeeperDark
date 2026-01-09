package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class OperatorList extends ServerConfigList {
   public OperatorList(File file) {
      super(file);
   }

   protected ServerConfigEntry fromJson(JsonObject json) {
      return new OperatorEntry(json);
   }

   public String[] getNames() {
      return (String[])this.values().stream().map(ServerConfigEntry::getKey).filter(Objects::nonNull).map(GameProfile::getName).toArray((i) -> {
         return new String[i];
      });
   }

   public boolean canBypassPlayerLimit(GameProfile profile) {
      OperatorEntry operatorEntry = (OperatorEntry)this.get(profile);
      return operatorEntry != null ? operatorEntry.canBypassPlayerLimit() : false;
   }

   protected String toString(GameProfile gameProfile) {
      return gameProfile.getId().toString();
   }

   // $FF: synthetic method
   protected String toString(final Object profile) {
      return this.toString((GameProfile)profile);
   }
}
