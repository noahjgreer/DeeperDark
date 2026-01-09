package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.Bootstrap;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class JfrListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Runnable stopCallback;

   protected JfrListener(Runnable stopCallback) {
      this.stopCallback = stopCallback;
   }

   public void stop(@Nullable Path dumpPath) {
      if (dumpPath != null) {
         this.stopCallback.run();
         log(() -> {
            return "Dumped flight recorder profiling to " + String.valueOf(dumpPath);
         });

         JfrProfile jfrProfile;
         try {
            jfrProfile = JfrProfileRecorder.readProfile(dumpPath);
         } catch (Throwable var5) {
            warn(() -> {
               return "Failed to parse JFR recording";
            }, var5);
            return;
         }

         try {
            Objects.requireNonNull(jfrProfile);
            log(jfrProfile::toJson);
            String var10001 = dumpPath.getFileName().toString();
            Path path = dumpPath.resolveSibling("jfr-report-" + StringUtils.substringBefore(var10001, ".jfr") + ".json");
            Files.writeString(path, jfrProfile.toJson(), StandardOpenOption.CREATE);
            log(() -> {
               return "Dumped recording summary to " + String.valueOf(path);
            });
         } catch (Throwable var4) {
            warn(() -> {
               return "Failed to output JFR report";
            }, var4);
         }

      }
   }

   private static void log(Supplier logSupplier) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.info((String)logSupplier.get());
      } else {
         Bootstrap.println((String)logSupplier.get());
      }

   }

   private static void warn(Supplier logSupplier, Throwable throwable) {
      if (LogUtils.isLoggerActive()) {
         LOGGER.warn((String)logSupplier.get(), throwable);
      } else {
         Bootstrap.println((String)logSupplier.get());
         throwable.printStackTrace(Bootstrap.SYSOUT);
      }

   }
}
