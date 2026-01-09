package net.minecraft.client.resource.server;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ReloadScheduler {
   void scheduleReload(ReloadContext context);

   @Environment(EnvType.CLIENT)
   public interface ReloadContext {
      void onSuccess();

      void onFailure(boolean force);

      List getPacks();
   }

   @Environment(EnvType.CLIENT)
   public static record PackInfo(UUID id, Path path) {
      public PackInfo(UUID uUID, Path path) {
         this.id = uUID;
         this.path = path;
      }

      public UUID id() {
         return this.id;
      }

      public Path path() {
         return this.path;
      }
   }
}
