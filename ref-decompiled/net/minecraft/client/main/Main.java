package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.tracy.TracyLoader;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Nullables;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.WinNativeModuleUtil;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.profiling.jfr.InstanceType;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class Main {
   @DontObfuscate
   public static void main(String[] args) {
      OptionParser optionParser = new OptionParser();
      optionParser.allowsUnrecognizedOptions();
      optionParser.accepts("demo");
      optionParser.accepts("disableMultiplayer");
      optionParser.accepts("disableChat");
      optionParser.accepts("fullscreen");
      optionParser.accepts("checkGlErrors");
      OptionSpec optionSpec = optionParser.accepts("renderDebugLabels");
      OptionSpec optionSpec2 = optionParser.accepts("jfrProfile");
      OptionSpec optionSpec3 = optionParser.accepts("tracy");
      OptionSpec optionSpec4 = optionParser.accepts("tracyNoImages");
      OptionSpec optionSpec5 = optionParser.accepts("quickPlayPath").withRequiredArg();
      OptionSpec optionSpec6 = optionParser.accepts("quickPlaySingleplayer").withOptionalArg();
      OptionSpec optionSpec7 = optionParser.accepts("quickPlayMultiplayer").withRequiredArg();
      OptionSpec optionSpec8 = optionParser.accepts("quickPlayRealms").withRequiredArg();
      OptionSpec optionSpec9 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      OptionSpec optionSpec10 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec optionSpec11 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec optionSpec12 = optionParser.accepts("proxyHost").withRequiredArg();
      OptionSpec optionSpec13 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      OptionSpec optionSpec14 = optionParser.accepts("proxyUser").withRequiredArg();
      OptionSpec optionSpec15 = optionParser.accepts("proxyPass").withRequiredArg();
      OptionSpec optionSpec16 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + System.currentTimeMillis() % 1000L, new String[0]);
      OptionSpec optionSpec17 = optionParser.accepts("uuid").withRequiredArg();
      OptionSpec optionSpec18 = optionParser.accepts("xuid").withOptionalArg().defaultsTo("", new String[0]);
      OptionSpec optionSpec19 = optionParser.accepts("clientId").withOptionalArg().defaultsTo("", new String[0]);
      OptionSpec optionSpec20 = optionParser.accepts("accessToken").withRequiredArg().required();
      OptionSpec optionSpec21 = optionParser.accepts("version").withRequiredArg().required();
      OptionSpec optionSpec22 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      OptionSpec optionSpec23 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      OptionSpec optionSpec24 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
      OptionSpec optionSpec25 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
      OptionSpec optionSpec26 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      OptionSpec optionSpec27 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      OptionSpec optionSpec28 = optionParser.accepts("assetIndex").withRequiredArg();
      OptionSpec optionSpec29 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      OptionSpec optionSpec30 = optionParser.accepts("versionType").withRequiredArg().defaultsTo("release", new String[0]);
      OptionSpec optionSpec31 = optionParser.nonOptions();
      OptionSet optionSet = optionParser.parse(args);
      File file = (File)getOption(optionSet, optionSpec9);
      String string = (String)getOption(optionSet, optionSpec21);
      String string2 = "Pre-bootstrap";

      Logger logger;
      RunArgs runArgs;
      CrashReport minecraftClient;
      try {
         if (optionSet.has(optionSpec2)) {
            FlightProfiler.INSTANCE.start(InstanceType.CLIENT);
         }

         if (optionSet.has(optionSpec3)) {
            TracyLoader.load();
         }

         Stopwatch stopwatch = Stopwatch.createStarted(Ticker.systemTicker());
         Stopwatch stopwatch2 = Stopwatch.createStarted(Ticker.systemTicker());
         GameLoadTimeEvent.INSTANCE.addTimer(TelemetryEventProperty.LOAD_TIME_TOTAL_TIME_MS, stopwatch);
         GameLoadTimeEvent.INSTANCE.addTimer(TelemetryEventProperty.LOAD_TIME_PRE_WINDOW_MS, stopwatch2);
         SharedConstants.createGameVersion();
         TracyClient.reportAppInfo("Minecraft Java Edition " + SharedConstants.getGameVersion().name());
         CompletableFuture completableFuture = Schemas.optimize(DataFixTypes.REQUIRED_TYPES);
         CrashReport.initCrashReport();
         logger = LogUtils.getLogger();
         string2 = "Bootstrap";
         Bootstrap.initialize();
         ClientBootstrap.initialize();
         GameLoadTimeEvent.INSTANCE.setBootstrapTime(Bootstrap.LOAD_TIME.get());
         Bootstrap.logMissing();
         string2 = "Argument parsing";
         List list = optionSet.valuesOf(optionSpec31);
         if (!list.isEmpty()) {
            logger.info("Completely ignored arguments: {}", list);
         }

         String string3 = (String)optionSpec29.value(optionSet);
         Session.AccountType accountType = Session.AccountType.byName(string3);
         if (accountType == null) {
            logger.warn("Unrecognized user type: {}", string3);
         }

         String string4 = (String)getOption(optionSet, optionSpec12);
         Proxy proxy = Proxy.NO_PROXY;
         if (string4 != null) {
            try {
               proxy = new Proxy(Type.SOCKS, new InetSocketAddress(string4, (Integer)getOption(optionSet, optionSpec13)));
            } catch (Exception var81) {
            }
         }

         final String string5 = (String)getOption(optionSet, optionSpec14);
         final String string6 = (String)getOption(optionSet, optionSpec15);
         if (!proxy.equals(Proxy.NO_PROXY) && isNotNullOrEmpty(string5) && isNotNullOrEmpty(string6)) {
            Authenticator.setDefault(new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(string5, string6.toCharArray());
               }
            });
         }

         int i = (Integer)getOption(optionSet, optionSpec22);
         int j = (Integer)getOption(optionSet, optionSpec23);
         OptionalInt optionalInt = toOptional((Integer)getOption(optionSet, optionSpec24));
         OptionalInt optionalInt2 = toOptional((Integer)getOption(optionSet, optionSpec25));
         boolean bl = optionSet.has("fullscreen");
         boolean bl2 = optionSet.has("demo");
         boolean bl3 = optionSet.has("disableMultiplayer");
         boolean bl4 = optionSet.has("disableChat");
         boolean bl5 = !optionSet.has(optionSpec4);
         boolean bl6 = optionSet.has(optionSpec);
         Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create();
         PropertyMap propertyMap = (PropertyMap)JsonHelper.deserialize(gson, (String)getOption(optionSet, optionSpec26), PropertyMap.class);
         PropertyMap propertyMap2 = (PropertyMap)JsonHelper.deserialize(gson, (String)getOption(optionSet, optionSpec27), PropertyMap.class);
         String string7 = (String)getOption(optionSet, optionSpec30);
         File file2 = optionSet.has(optionSpec10) ? (File)getOption(optionSet, optionSpec10) : new File(file, "assets/");
         File file3 = optionSet.has(optionSpec11) ? (File)getOption(optionSet, optionSpec11) : new File(file, "resourcepacks/");
         UUID uUID = isUuidSetAndValid(optionSpec17, optionSet, logger) ? UndashedUuid.fromStringLenient((String)optionSpec17.value(optionSet)) : Uuids.getOfflinePlayerUuid((String)optionSpec16.value(optionSet));
         String string8 = optionSet.has(optionSpec28) ? (String)optionSpec28.value(optionSet) : null;
         String string9 = (String)optionSet.valueOf(optionSpec18);
         String string10 = (String)optionSet.valueOf(optionSpec19);
         String string11 = (String)getOption(optionSet, optionSpec5);
         RunArgs.QuickPlayVariant quickPlayVariant = getQuickPlayVariant(optionSet, optionSpec6, optionSpec7, optionSpec8);
         Session session = new Session((String)optionSpec16.value(optionSet), uUID, (String)optionSpec20.value(optionSet), toOptional(string9), toOptional(string10), accountType);
         runArgs = new RunArgs(new RunArgs.Network(session, propertyMap, propertyMap2, proxy), new WindowSettings(i, j, optionalInt, optionalInt2, bl), new RunArgs.Directories(file, file3, file2, string8), new RunArgs.Game(bl2, string, string7, bl3, bl4, bl5, bl6), new RunArgs.QuickPlay(string11, quickPlayVariant));
         Util.startTimerHack();
         completableFuture.join();
      } catch (Throwable var82) {
         minecraftClient = CrashReport.create(var82, string2);
         CrashReportSection crashReportSection = minecraftClient.addElement("Initialization");
         WinNativeModuleUtil.addDetailTo(crashReportSection);
         MinecraftClient.addSystemDetailsToCrashReport((MinecraftClient)null, (LanguageManager)null, (String)string, (GameOptions)null, (CrashReport)minecraftClient);
         MinecraftClient.printCrashReport((MinecraftClient)null, file, minecraftClient);
         return;
      }

      Thread thread = new Thread("Client Shutdown Thread") {
         public void run() {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient != null) {
               IntegratedServer integratedServer = minecraftClient.getServer();
               if (integratedServer != null) {
                  integratedServer.stop(true);
               }

            }
         }
      };
      thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(logger));
      Runtime.getRuntime().addShutdownHook(thread);
      minecraftClient = null;

      MinecraftClient minecraftClient;
      try {
         Thread.currentThread().setName("Render thread");
         RenderSystem.initRenderThread();
         minecraftClient = new MinecraftClient(runArgs);
      } catch (GlException var79) {
         Util.shutdownExecutors();
         logger.warn("Failed to create window: ", var79);
         return;
      } catch (Throwable var80) {
         CrashReport crashReport2 = CrashReport.create(var80, "Initializing game");
         CrashReportSection crashReportSection2 = crashReport2.addElement("Initialization");
         WinNativeModuleUtil.addDetailTo(crashReportSection2);
         MinecraftClient.addSystemDetailsToCrashReport((MinecraftClient)minecraftClient, (LanguageManager)null, (String)runArgs.game.version, (GameOptions)null, (CrashReport)crashReport2);
         MinecraftClient.printCrashReport(minecraftClient, runArgs.directories.runDir, crashReport2);
         return;
      }

      MinecraftClient minecraftClient2 = minecraftClient;
      minecraftClient.run();

      try {
         minecraftClient2.scheduleStop();
      } finally {
         minecraftClient.stop();
      }

   }

   private static RunArgs.QuickPlayVariant getQuickPlayVariant(OptionSet optionSet, OptionSpec worldIdOption, OptionSpec serverAddressOption, OptionSpec realmIdOption) {
      Stream var10000 = Stream.of(worldIdOption, serverAddressOption, realmIdOption);
      Objects.requireNonNull(optionSet);
      long l = var10000.filter(optionSet::has).count();
      if (l == 0L) {
         return RunArgs.QuickPlayVariant.DEFAULT;
      } else if (l > 1L) {
         throw new IllegalArgumentException("Only one quick play option can be specified");
      } else {
         String string;
         if (optionSet.has(worldIdOption)) {
            string = unescape((String)getOption(optionSet, worldIdOption));
            return new RunArgs.SingleplayerQuickPlay(string);
         } else if (optionSet.has(serverAddressOption)) {
            string = unescape((String)getOption(optionSet, serverAddressOption));
            return (RunArgs.QuickPlayVariant)Nullables.mapOrElse(string, RunArgs.MultiplayerQuickPlay::new, RunArgs.QuickPlayVariant.DEFAULT);
         } else if (optionSet.has(realmIdOption)) {
            string = unescape((String)getOption(optionSet, realmIdOption));
            return (RunArgs.QuickPlayVariant)Nullables.mapOrElse(string, RunArgs.RealmsQuickPlay::new, RunArgs.QuickPlayVariant.DEFAULT);
         } else {
            return RunArgs.QuickPlayVariant.DEFAULT;
         }
      }
   }

   @Nullable
   private static String unescape(@Nullable String string) {
      return string == null ? null : StringEscapeUtils.unescapeJava(string);
   }

   private static Optional toOptional(String string) {
      return string.isEmpty() ? Optional.empty() : Optional.of(string);
   }

   private static OptionalInt toOptional(@Nullable Integer i) {
      return i != null ? OptionalInt.of(i) : OptionalInt.empty();
   }

   @Nullable
   private static Object getOption(OptionSet optionSet, OptionSpec optionSpec) {
      try {
         return optionSet.valueOf(optionSpec);
      } catch (Throwable var5) {
         if (optionSpec instanceof ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec) {
            List list = argumentAcceptingOptionSpec.defaultValues();
            if (!list.isEmpty()) {
               return list.get(0);
            }
         }

         throw var5;
      }
   }

   private static boolean isNotNullOrEmpty(@Nullable String s) {
      return s != null && !s.isEmpty();
   }

   private static boolean isUuidSetAndValid(OptionSpec uuidOption, OptionSet optionSet, Logger logger) {
      return optionSet.has(uuidOption) && isUuidValid(uuidOption, optionSet, logger);
   }

   private static boolean isUuidValid(OptionSpec uuidOption, OptionSet optionSet, Logger logger) {
      try {
         UndashedUuid.fromStringLenient((String)uuidOption.value(optionSet));
         return true;
      } catch (IllegalArgumentException var4) {
         logger.warn("Invalid UUID: '{}", uuidOption.value(optionSet));
         return false;
      }
   }

   static {
      System.setProperty("java.awt.headless", "true");
   }
}
