/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.base.Ticker
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
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
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
import net.minecraft.client.session.Session;
import net.minecraft.client.session.telemetry.GameLoadTimeEvent;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.tracy.TracyLoader;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.integrated.IntegratedServer;
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
        File file = (File)Main.getOption(optionSet, optionSpec9);
        String string = (String)Main.getOption(optionSet, optionSpec22);
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
            CompletableFuture<?> completableFuture = Schemas.optimize(DataFixTypes.REQUIRED_TYPES);
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
            String string3 = (String)Main.getOption(optionSet, optionSpec12);
            Proxy proxy = Proxy.NO_PROXY;
            if (string3 != null) {
                try {
                    proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(string3, (int)((Integer)Main.getOption(optionSet, optionSpec13))));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            final String string4 = (String)Main.getOption(optionSet, optionSpec14);
            final String string5 = (String)Main.getOption(optionSet, optionSpec15);
            if (!proxy.equals(Proxy.NO_PROXY) && Main.isNotNullOrEmpty(string4) && Main.isNotNullOrEmpty(string5)) {
                Authenticator.setDefault(new Authenticator(){

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(string4, string5.toCharArray());
                    }
                });
            }
            int i = (Integer)Main.getOption(optionSet, optionSpec23);
            int j = (Integer)Main.getOption(optionSet, optionSpec24);
            OptionalInt optionalInt = Main.toOptional((Integer)Main.getOption(optionSet, optionSpec25));
            OptionalInt optionalInt2 = Main.toOptional((Integer)Main.getOption(optionSet, optionSpec26));
            boolean bl = optionSet.has("fullscreen");
            boolean bl2 = optionSet.has("demo");
            boolean bl3 = optionSet.has("disableMultiplayer");
            boolean bl4 = optionSet.has("disableChat");
            boolean bl5 = !optionSet.has((OptionSpec)optionSpec4);
            boolean bl6 = optionSet.has((OptionSpec)optionSpec);
            String string6 = (String)Main.getOption(optionSet, optionSpec28);
            File file2 = optionSet.has((OptionSpec)optionSpec10) ? (File)Main.getOption(optionSet, optionSpec10) : new File(file, "assets/");
            File file3 = optionSet.has((OptionSpec)optionSpec11) ? (File)Main.getOption(optionSet, optionSpec11) : new File(file, "resourcepacks/");
            UUID uUID = Main.isUuidSetAndValid((OptionSpec<String>)optionSpec18, optionSet, logger) ? UndashedUuid.fromStringLenient((String)((String)optionSpec18.value(optionSet))) : Uuids.getOfflinePlayerUuid((String)optionSpec16.value(optionSet));
            String string7 = optionSet.has((OptionSpec)optionSpec27) ? (String)optionSpec27.value(optionSet) : null;
            String string8 = (String)optionSet.valueOf((OptionSpec)optionSpec19);
            String string9 = (String)optionSet.valueOf((OptionSpec)optionSpec20);
            String string10 = (String)Main.getOption(optionSet, optionSpec5);
            RunArgs.QuickPlayVariant quickPlayVariant = Main.getQuickPlayVariant(optionSet, (OptionSpec<String>)optionSpec6, (OptionSpec<String>)optionSpec7, (OptionSpec<String>)optionSpec8);
            Session session = new Session((String)optionSpec16.value(optionSet), uUID, (String)optionSpec21.value(optionSet), Main.toOptional(string8), Main.toOptional(string9));
            runArgs = new RunArgs(new RunArgs.Network(session, proxy), new WindowSettings(i, j, optionalInt, optionalInt2, bl), new RunArgs.Directories(file, file3, file2, string7), new RunArgs.Game(bl2, string, string6, bl3, bl4, bl5, bl6, optionSet.has((OptionSpec)optionSpec17)), new RunArgs.QuickPlay(string10, quickPlayVariant));
            Util.startTimerHack();
            completableFuture.join();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, string2);
            CrashReportSection crashReportSection = crashReport.addElement("Initialization");
            WinNativeModuleUtil.addDetailTo(crashReportSection);
            MinecraftClient.addSystemDetailsToCrashReport(null, null, string, null, crashReport);
            MinecraftClient.printCrashReport(null, file, crashReport);
            return;
        }
        Thread thread = new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                if (minecraftClient == null) {
                    return;
                }
                IntegratedServer integratedServer = minecraftClient.getServer();
                if (integratedServer != null) {
                    integratedServer.stop(true);
                }
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(logger));
        Runtime.getRuntime().addShutdownHook(thread);
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
            CrashReport crashReport2 = CrashReport.create(throwable2, "Initializing game");
            CrashReportSection crashReportSection2 = crashReport2.addElement("Initialization");
            WinNativeModuleUtil.addDetailTo(crashReportSection2);
            MinecraftClient.addSystemDetailsToCrashReport(minecraftClient, null, runArgs.game.version, null, crashReport2);
            MinecraftClient.printCrashReport(minecraftClient, runArgs.directories.runDir, crashReport2);
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
            String string = Main.unescape(Main.getOption(optionSet, worldIdOption));
            return new RunArgs.SingleplayerQuickPlay(string);
        }
        if (optionSet.has(serverAddressOption)) {
            String string = Main.unescape(Main.getOption(optionSet, serverAddressOption));
            return Nullables.mapOrElse(string, RunArgs.MultiplayerQuickPlay::new, RunArgs.QuickPlayVariant.DEFAULT);
        }
        if (optionSet.has(realmIdOption)) {
            String string = Main.unescape(Main.getOption(optionSet, realmIdOption));
            return Nullables.mapOrElse(string, RunArgs.RealmsQuickPlay::new, RunArgs.QuickPlayVariant.DEFAULT);
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
        return optionSet.has(uuidOption) && Main.isUuidValid(uuidOption, optionSet, logger);
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
