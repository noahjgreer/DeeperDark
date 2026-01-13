/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  io.netty.channel.ChannelFuture
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.QuickPlay
 *  net.minecraft.client.QuickPlayLogger$WorldType
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.multiplayer.ConnectScreen
 *  net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.network.CookieStorage
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.session.report.ReporterEnvironment
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.logging.UncaughtExceptionLogger
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFuture;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ConnectScreen
extends Screen {
    private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final long NARRATOR_INTERVAL = 2000L;
    public static final Text ABORTED_TEXT = Text.translatable((String)"connect.aborted");
    public static final Text UNKNOWN_HOST_TEXT = Text.translatable((String)"disconnect.genericReason", (Object[])new Object[]{Text.translatable((String)"disconnect.unknownHost")});
    volatile @Nullable ClientConnection connection;
    @Nullable ChannelFuture future;
    volatile boolean connectingCancelled;
    final Screen parent;
    private Text status = Text.translatable((String)"connect.connecting");
    private long lastNarrationTime = -1L;
    final Text failureErrorMessage;

    private ConnectScreen(Screen parent, Text failureErrorMessage) {
        super(NarratorManager.EMPTY);
        this.parent = parent;
        this.failureErrorMessage = failureErrorMessage;
    }

    public static void connect(Screen screen, MinecraftClient client, ServerAddress address, ServerInfo info, boolean quickPlay, @Nullable CookieStorage cookieStorage) {
        if (client.currentScreen instanceof ConnectScreen) {
            LOGGER.error("Attempt to connect while already connecting");
            return;
        }
        Text text = cookieStorage != null ? ScreenTexts.CONNECT_FAILED_TRANSFER : (quickPlay ? QuickPlay.ERROR_TITLE : ScreenTexts.CONNECT_FAILED);
        ConnectScreen connectScreen = new ConnectScreen(screen, text);
        if (cookieStorage != null) {
            connectScreen.setStatus((Text)Text.translatable((String)"connect.transferring"));
        }
        client.disconnectWithProgressScreen(false);
        client.loadBlockList();
        client.ensureAbuseReportContext(ReporterEnvironment.ofThirdPartyServer((String)info.address));
        client.getQuickPlayLogger().setWorld(QuickPlayLogger.WorldType.MULTIPLAYER, info.address, info.name);
        client.setScreen((Screen)connectScreen);
        connectScreen.connect(client, address, info, cookieStorage);
    }

    private void connect(MinecraftClient client, ServerAddress address, ServerInfo info, @Nullable CookieStorage cookieStorage) {
        LOGGER.info("Connecting to {}, {}", (Object)address.getAddress(), (Object)address.getPort());
        1 thread = new /* Unavailable Anonymous Inner Class!! */;
        thread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    private void setStatus(Text status) {
        this.status = status;
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }

    protected void init() {
        this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> {
            ConnectScreen connectScreen = this;
            synchronized (connectScreen) {
                this.connectingCancelled = true;
                if (this.future != null) {
                    this.future.cancel(true);
                    this.future = null;
                }
                if (this.connection != null) {
                    this.connection.disconnect(ABORTED_TEXT);
                }
            }
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        long l = Util.getMeasuringTimeMs();
        if (l - this.lastNarrationTime > 2000L) {
            this.lastNarrationTime = l;
            this.client.getNarratorManager().narrateSystemImmediately((Text)Text.translatable((String)"narrator.joining"));
        }
        context.drawCenteredTextWithShadow(this.textRenderer, this.status, this.width / 2, this.height / 2 - 50, -1);
    }
}

