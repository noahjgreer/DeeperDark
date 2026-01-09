package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsDefaultUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
   private final Logger logger;

   public RealmsDefaultUncaughtExceptionHandler(Logger logger) {
      this.logger = logger;
   }

   public void uncaughtException(Thread t, Throwable e) {
      this.logger.error("Caught previously unhandled exception", e);
   }
}
