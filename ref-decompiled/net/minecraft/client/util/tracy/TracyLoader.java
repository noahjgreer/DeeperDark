package net.minecraft.client.util.tracy;

import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogListeners;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.event.Level;

@Environment(EnvType.CLIENT)
public class TracyLoader {
   private static boolean loaded;

   public static void load() {
      if (!loaded) {
         TracyClient.load();
         if (TracyClient.isAvailable()) {
            LogListeners.addListener("Tracy", (message, level) -> {
               TracyClient.message(message, getColor(level));
            });
            loaded = true;
         }
      }
   }

   private static int getColor(Level level) {
      int var10000;
      switch (level) {
         case DEBUG:
            var10000 = 11184810;
            break;
         case WARN:
            var10000 = 16777130;
            break;
         case ERROR:
            var10000 = 16755370;
            break;
         default:
            var10000 = 16777215;
      }

      return var10000;
   }
}
