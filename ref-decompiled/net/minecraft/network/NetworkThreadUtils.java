package net.minecraft.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.thread.ThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NetworkThreadUtils {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void forceMainThread(Packet packet, PacketListener listener, ServerWorld world) throws OffThreadException {
      forceMainThread(packet, listener, (ThreadExecutor)world.getServer());
   }

   public static void forceMainThread(Packet packet, PacketListener listener, ThreadExecutor engine) throws OffThreadException {
      if (!engine.isOnThread()) {
         engine.executeSync(() -> {
            if (listener.accepts(packet)) {
               try {
                  packet.apply(listener);
               } catch (Exception var4) {
                  if (var4 instanceof CrashException) {
                     CrashException crashException = (CrashException)var4;
                     if (crashException.getCause() instanceof OutOfMemoryError) {
                        throw createCrashException(var4, packet, listener);
                     }
                  }

                  listener.onPacketException(packet, var4);
               }
            } else {
               LOGGER.debug("Ignoring packet due to disconnection: {}", packet);
            }

         });
         throw OffThreadException.INSTANCE;
      }
   }

   public static CrashException createCrashException(Exception exception, Packet packet, PacketListener listener) {
      if (exception instanceof CrashException crashException) {
         fillCrashReport(crashException.getReport(), listener, packet);
         return crashException;
      } else {
         CrashReport crashReport = CrashReport.create(exception, "Main thread packet handler");
         fillCrashReport(crashReport, listener, packet);
         return new CrashException(crashReport);
      }
   }

   public static void fillCrashReport(CrashReport report, PacketListener listener, @Nullable Packet packet) {
      if (packet != null) {
         CrashReportSection crashReportSection = report.addElement("Incoming Packet");
         crashReportSection.add("Type", () -> {
            return packet.getPacketType().toString();
         });
         crashReportSection.add("Is Terminal", () -> {
            return Boolean.toString(packet.transitionsNetworkState());
         });
         crashReportSection.add("Is Skippable", () -> {
            return Boolean.toString(packet.isWritingErrorSkippable());
         });
      }

      listener.fillCrashReport(report);
   }
}
