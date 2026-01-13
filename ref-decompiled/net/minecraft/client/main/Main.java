/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Ticker
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.jtracy.TracyClient
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UndashedUuid
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.Bootstrap
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.ClientBootstrap
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.RunArgs
 *  net.minecraft.client.RunArgs$Directories
 *  net.minecraft.client.RunArgs$Game
 *  net.minecraft.client.RunArgs$MultiplayerQuickPlay
 *  net.minecraft.client.RunArgs$Network
 *  net.minecraft.client.RunArgs$QuickPlay
 *  net.minecraft.client.RunArgs$QuickPlayVariant
 *  net.minecraft.client.RunArgs$RealmsQuickPlay
 *  net.minecraft.client.RunArgs$SingleplayerQuickPlay
 *  net.minecraft.client.WindowSettings
 *  net.minecraft.client.main.Main
 *  net.minecraft.client.main.Main$2
 *  net.minecraft.client.session.Session
 *  net.minecraft.client.session.telemetry.GameLoadTimeEvent
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  net.minecraft.client.util.GlException
 *  net.minecraft.client.util.tracy.TracyLoader
 *  net.minecraft.datafixer.DataFixTypes
 *  net.minecraft.datafixer.Schemas
 *  net.minecraft.obfuscate.DontObfuscate
 *  net.minecraft.util.Nullables
 *  net.minecraft.util.Util
 *  net.minecraft.util.Uuids
 *  net.minecraft.util.WinNativeModuleUtil
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.logging.UncaughtExceptionLogger
 *  net.minecraft.util.profiling.jfr.FlightProfiler
 *  net.minecraft.util.profiling.jfr.InstanceType
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.main;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.main.Main;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.tracy.TracyLoader;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.obfuscate.DontObfuscate;
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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Main {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @DontObfuscate
    public static void main(String[] args) {
        RunArgs runArgs;
        Logger logger;
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("disableMultiplayer");
        optionParser.accepts("disableChat");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        OptionSpecBuilder optionSpec = optionParser.accepts("renderDebugLabels");
        OptionSpecBuilder optionSpec2 = optionParser.accepts("jfrProfile");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("tracy");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("tracyNoImages");
        ArgumentAcceptingOptionSpec optionSpec5 = optionParser.accepts("quickPlayPath").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec6 = optionParser.accepts("quickPlaySingleplayer").withOptionalArg();
        ArgumentAcceptingOptionSpec optionSpec7 = optionParser.accepts("quickPlayMultiplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec8 = optionParser.accepts("quickPlayRealms").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo((Object)new File("."), (Object[])new File[0]);
        ArgumentAcceptingOptionSpec optionSpec10 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionSpec11 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionSpec12 = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec13 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo((Object)"8080", (Object[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionSpec14 = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec15 = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec16 = optionParser.accepts("username").withRequiredArg().defaultsTo((Object)("Player" + System.currentTimeMillis() % 1000L), (Object[])new String[0]);
        OptionSpecBuilder optionSpec17 = optionParser.accepts("offlineDeveloperMode");
        ArgumentAcceptingOptionSpec optionSpec18 = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec19 = optionParser.accepts("xuid").withOptionalArg().defaultsTo((Object)"", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec20 = optionParser.accepts("clientId").withOptionalArg().defaultsTo((Object)"", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec21 = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec optionSpec22 = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec optionSpec23 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo((Object)854, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec24 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo((Object)480, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec25 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionSpec26 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionSpec27 = optionParser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec28 = optionParser.accepts("versionType").withRequiredArg().defaultsTo((Object)"release", (Object[])new String[0]);
        NonOptionArgumentSpec optionSpec29 = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(args);
        File file = (File)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec9);
        String string = (String)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec22);
        String string2 = "Pre-bootstrap";
        try {
            if (optionSet.has((OptionSpec)optionSpec2)) {
                FlightProfiler.INSTANCE.start(InstanceType.CLIENT);
            }
            if (optionSet.has((OptionSpec)optionSpec3)) {
                TracyLoader.load();
            }
            Stopwatch stopwatch = Stopwatch.createStarted((Ticker)Ticker.systemTicker());
            Stopwatch stopwatch2 = Stopwatch.createStarted((Ticker)Ticker.systemTicker());
            GameLoadTimeEvent.INSTANCE.addTimer(TelemetryEventProperty.LOAD_TIME_TOTAL_TIME_MS, stopwatch);
            GameLoadTimeEvent.INSTANCE.addTimer(TelemetryEventProperty.LOAD_TIME_PRE_WINDOW_MS, stopwatch2);
            SharedConstants.createGameVersion();
            TracyClient.reportAppInfo((String)("Minecraft Java Edition " + SharedConstants.getGameVersion().name()));
            CompletableFuture completableFuture = Schemas.optimize((Set)DataFixTypes.REQUIRED_TYPES);
            CrashReport.initCrashReport();
            logger = LogUtils.getLogger();
            string2 = "Bootstrap";
            Bootstrap.initialize();
            ClientBootstrap.initialize();
            GameLoadTimeEvent.INSTANCE.setBootstrapTime(Bootstrap.LOAD_TIME.get());
            Bootstrap.logMissing();
            string2 = "Argument parsing";
            List list = optionSet.valuesOf((OptionSpec)optionSpec29);
            if (!list.isEmpty()) {
                logger.info("Completely ignored arguments: {}", (Object)list);
            }
            String string3 = (String)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec12);
            Proxy proxy = Proxy.NO_PROXY;
            if (string3 != null) {
                try {
                    proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(string3, (int)((Integer)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec13))));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            String string4 = (String)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec14);
            String string5 = (String)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec15);
            if (!proxy.equals(Proxy.NO_PROXY) && Main.isNotNullOrEmpty((String)string4) && Main.isNotNullOrEmpty((String)string5)) {
                Authenticator.setDefault((Authenticator)new /* Unavailable Anonymous Inner Class!! */);
            }
            int i = (Integer)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec23);
            int j = (Integer)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec24);
            OptionalInt optionalInt = Main.toOptional((Integer)((Integer)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec25)));
            OptionalInt optionalInt2 = Main.toOptional((Integer)((Integer)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec26)));
            boolean bl = optionSet.has("fullscreen");
            boolean bl2 = optionSet.has("demo");
            boolean bl3 = optionSet.has("disableMultiplayer");
            boolean bl4 = optionSet.has("disableChat");
            boolean bl5 = !optionSet.has((OptionSpec)optionSpec4);
            boolean bl6 = optionSet.has((OptionSpec)optionSpec);
            String string6 = (String)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec28);
            File file2 = optionSet.has((OptionSpec)optionSpec10) ? (File)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec10) : new File(file, "assets/");
            File file3 = optionSet.has((OptionSpec)optionSpec11) ? (File)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec11) : new File(file, "resourcepacks/");
            UUID uUID = Main.isUuidSetAndValid((OptionSpec)optionSpec18, (OptionSet)optionSet, (Logger)logger) ? UndashedUuid.fromStringLenient((String)((String)optionSpec18.value(optionSet))) : Uuids.getOfflinePlayerUuid((String)((String)optionSpec16.value(optionSet)));
            String string7 = optionSet.has((OptionSpec)optionSpec27) ? (String)optionSpec27.value(optionSet) : null;
            String string8 = (String)optionSet.valueOf((OptionSpec)optionSpec19);
            String string9 = (String)optionSet.valueOf((OptionSpec)optionSpec20);
            String string10 = (String)Main.getOption((OptionSet)optionSet, (OptionSpec)optionSpec5);
            RunArgs.QuickPlayVariant quickPlayVariant = Main.getQuickPlayVariant((OptionSet)optionSet, (OptionSpec)optionSpec6, (OptionSpec)optionSpec7, (OptionSpec)optionSpec8);
            Session session = new Session((String)optionSpec16.value(optionSet), uUID, (String)optionSpec21.value(optionSet), Main.toOptional((String)string8), Main.toOptional((String)string9));
            runArgs = new RunArgs(new RunArgs.Network(session, proxy), new WindowSettings(i, j, optionalInt, optionalInt2, bl), new RunArgs.Directories(file, file3, file2, string7), new RunArgs.Game(bl2, string, string6, bl3, bl4, bl5, bl6, optionSet.has((OptionSpec)optionSpec17)), new RunArgs.QuickPlay(string10, quickPlayVariant));
            Util.startTimerHack();
            completableFuture.join();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)string2);
            CrashReportSection crashReportSection = crashReport.addElement("Initialization");
            WinNativeModuleUtil.addDetailTo((CrashReportSection)crashReportSection);
            MinecraftClient.addSystemDetailsToCrashReport(null, null, (String)string, null, (CrashReport)crashReport);
            MinecraftClient.printCrashReport(null, (File)file, (CrashReport)crashReport);
            return;
        }
        2 thread = new /* Unavailable Anonymous Inner Class!! */;
        thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(logger));
        Runtime.getRuntime().addShutdownHook((Thread)thread);
        MinecraftClient minecraftClient = null;
        try {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            minecraftClient = new MinecraftClient(runArgs);
        }
        catch (GlException glException) {
            Util.shutdownExecutors();
            logger.warn("Failed to create window: ", (Throwable)glException);
            return;
        }
        catch (Throwable throwable2) {
            CrashReport crashReport2 = CrashReport.create((Throwable)throwable2, (String)"Initializing game");
            CrashReportSection crashReportSection2 = crashReport2.addElement("Initialization");
            WinNativeModuleUtil.addDetailTo((CrashReportSection)crashReportSection2);
            MinecraftClient.addSystemDetailsToCrashReport((MinecraftClient)minecraftClient, null, (String)runArgs.game.version, null, (CrashReport)crashReport2);
            MinecraftClient.printCrashReport((MinecraftClient)minecraftClient, (File)runArgs.directories.runDir, (CrashReport)crashReport2);
            return;
        }
        MinecraftClient minecraftClient2 = minecraftClient;
        minecraftClient2.run();
        try {
            minecraftClient2.scheduleStop();
        }
        finally {
            minecraftClient2.stop();
        }
    }

    private static RunArgs.QuickPlayVariant getQuickPlayVariant(OptionSet optionSet, OptionSpec<String> worldIdOption, OptionSpec<String> serverAddressOption, OptionSpec<String> realmIdOption) {
        long l = Stream.of(worldIdOption, serverAddressOption, realmIdOption).filter(arg_0 -> ((OptionSet)optionSet).has(arg_0)).count();
        if (l == 0L) {
            return RunArgs.QuickPlayVariant.DEFAULT;
        }
        if (l > 1L) {
            throw new IllegalArgumentException("Only one quick play option can be specified");
        }
        if (optionSet.has(worldIdOption)) {
            String string = Main.unescape((String)((String)Main.getOption((OptionSet)optionSet, worldIdOption)));
            return new RunArgs.SingleplayerQuickPlay(string);
        }
        if (optionSet.has(serverAddressOption)) {
            String string = Main.unescape((String)((String)Main.getOption((OptionSet)optionSet, serverAddressOption)));
            return (RunArgs.QuickPlayVariant)Nullables.mapOrElse((Object)string, RunArgs.MultiplayerQuickPlay::new, (Object)RunArgs.QuickPlayVariant.DEFAULT);
        }
        if (optionSet.has(realmIdOption)) {
            String string = Main.unescape((String)((String)Main.getOption((OptionSet)optionSet, realmIdOption)));
            return (RunArgs.QuickPlayVariant)Nullables.mapOrElse((Object)string, RunArgs.RealmsQuickPlay::new, (Object)RunArgs.QuickPlayVariant.DEFAULT);
        }
        return RunArgs.QuickPlayVariant.DEFAULT;
    }

    private static @Nullable String unescape(@Nullable String string) {
        if (string == null) {
            return null;
        }
        return StringEscapeUtils.unescapeJava((String)string);
    }

    private static Optional<String> toOptional(String string) {
        return string.isEmpty() ? Optional.empty() : Optional.of(string);
    }

    private static OptionalInt toOptional(@Nullable Integer i) {
        return i != null ? OptionalInt.of(i) : OptionalInt.empty();
    }

    private static <T> @Nullable T getOption(OptionSet optionSet, OptionSpec<T> optionSpec) {
        try {
            return (T)optionSet.valueOf(optionSpec);
        }
        catch (Throwable throwable) {
            ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec;
            List list;
            if (optionSpec instanceof ArgumentAcceptingOptionSpec && !(list = (argumentAcceptingOptionSpec = (ArgumentAcceptingOptionSpec)optionSpec).defaultValues()).isEmpty()) {
                return (T)list.get(0);
            }
            throw throwable;
        }
    }

    private static boolean isNotNullOrEmpty(@Nullable String s) {
        return s != null && !s.isEmpty();
    }

    private static boolean isUuidSetAndValid(OptionSpec<String> uuidOption, OptionSet optionSet, Logger logger) {
        return optionSet.has(uuidOption) && Main.isUuidValid(uuidOption, (OptionSet)optionSet, (Logger)logger);
    }

    private static boolean isUuidValid(OptionSpec<String> uuidOption, OptionSet optionSet, Logger logger) {
        try {
            UndashedUuid.fromStringLenient((String)((String)uuidOption.value(optionSet)));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            logger.warn("Invalid UUID: '{}", uuidOption.value(optionSet));
            return false;
        }
        return true;
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }
}

