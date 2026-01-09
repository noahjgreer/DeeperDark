package net.minecraft.server;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public abstract class ServerConfigEntry {
   @Nullable
   private final Object key;

   public ServerConfigEntry(@Nullable Object key) {
      this.key = key;
   }

   @Nullable
   Object getKey() {
      return this.key;
   }

   boolean isInvalid() {
      return false;
   }

   protected abstract void write(JsonObject json);
}
