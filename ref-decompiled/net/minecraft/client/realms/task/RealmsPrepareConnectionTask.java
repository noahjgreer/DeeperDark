/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.MessageScreen
 *  net.minecraft.client.gui.screen.PopupScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServerAddress
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.exception.RetryCallException
 *  net.minecraft.client.realms.gui.RealmsPopups
 *  net.minecraft.client.realms.gui.screen.RealmsBrokenWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsConnectingScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsTermsScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.RealmsConnectTask
 *  net.minecraft.client.realms.task.RealmsPrepareConnectionTask
 *  net.minecraft.client.resource.server.ServerResourcePackLoader
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.task;

import com.mojang.logging.LogUtils;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsBrokenWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsConnectingScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsTermsScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.RealmsConnectTask;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsPrepareConnectionTask
extends LongRunningTask {
    private static final Text APPLYING_PACK_TEXT = Text.translatable((String)"multiplayer.applyingPack");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text TITLE = Text.translatable((String)"mco.connect.connecting");
    private final RealmsServer server;
    private final Screen lastScreen;

    public RealmsPrepareConnectionTask(Screen lastScreen, RealmsServer server) {
        this.lastScreen = lastScreen;
        this.server = server;
    }

    public void run() {
        RealmsServerAddress realmsServerAddress;
        try {
            realmsServerAddress = this.join();
        }
        catch (CancellationException cancellationException) {
            LOGGER.info("User aborted connecting to realms");
            return;
        }
        catch (RealmsServiceException realmsServiceException) {
            switch (realmsServiceException.error.getErrorCode()) {
                case 6002: {
                    RealmsPrepareConnectionTask.setScreen((Screen)new RealmsTermsScreen(this.lastScreen, this.server));
                    return;
                }
                case 6006: {
                    boolean bl = MinecraftClient.getInstance().uuidEquals(this.server.ownerUUID);
                    RealmsPrepareConnectionTask.setScreen((Screen)(bl ? new RealmsBrokenWorldScreen(this.lastScreen, this.server.id, this.server.isMinigame()) : new RealmsGenericErrorScreen((Text)Text.translatable((String)"mco.brokenworld.nonowner.title"), (Text)Text.translatable((String)"mco.brokenworld.nonowner.error"), this.lastScreen)));
                    return;
                }
            }
            this.error(realmsServiceException);
            LOGGER.error("Couldn't connect to world", (Throwable)realmsServiceException);
            return;
        }
        catch (TimeoutException timeoutException) {
            this.error((Text)Text.translatable((String)"mco.errorMessage.connectionFailure"));
            return;
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't connect to world", (Throwable)exception);
            this.error(exception);
            return;
        }
        if (realmsServerAddress.address() == null) {
            this.error((Text)Text.translatable((String)"mco.errorMessage.connectionFailure"));
            return;
        }
        boolean bl2 = realmsServerAddress.resourcePackUrl() != null && realmsServerAddress.resourcePackHash() != null;
        RealmsLongRunningMcoTaskScreen screen = bl2 ? this.createResourcePackConfirmationScreen(realmsServerAddress, RealmsPrepareConnectionTask.getResourcePackId((RealmsServer)this.server), arg_0 -> this.createConnectingScreen(arg_0)) : this.createConnectingScreen(realmsServerAddress);
        RealmsPrepareConnectionTask.setScreen((Screen)screen);
    }

    private static UUID getResourcePackId(RealmsServer server) {
        if (server.minigameName != null) {
            return UUID.nameUUIDFromBytes(("minigame:" + server.minigameName).getBytes(StandardCharsets.UTF_8));
        }
        return UUID.nameUUIDFromBytes(("realms:" + Objects.requireNonNullElse(server.name, "") + ":" + server.activeSlot).getBytes(StandardCharsets.UTF_8));
    }

    public Text getTitle() {
        return TITLE;
    }

    private RealmsServerAddress join() throws RealmsServiceException, TimeoutException, CancellationException {
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 40; ++i) {
            if (this.aborted()) {
                throw new CancellationException();
            }
            try {
                return realmsClient.join(this.server.id);
            }
            catch (RetryCallException retryCallException) {
                RealmsPrepareConnectionTask.pause((long)retryCallException.delaySeconds);
                continue;
            }
        }
        throw new TimeoutException();
    }

    public RealmsLongRunningMcoTaskScreen createConnectingScreen(RealmsServerAddress address) {
        return new RealmsConnectingScreen(this.lastScreen, address, (LongRunningTask)new RealmsConnectTask(this.lastScreen, this.server, address));
    }

    private PopupScreen createResourcePackConfirmationScreen(RealmsServerAddress address, UUID id, Function<RealmsServerAddress, Screen> connectingScreenCreator) {
        MutableText text = Text.translatable((String)"mco.configure.world.resourcepack.question");
        return RealmsPopups.createInfoPopup((Screen)this.lastScreen, (Text)text, popup -> {
            RealmsPrepareConnectionTask.setScreen((Screen)new MessageScreen(APPLYING_PACK_TEXT));
            ((CompletableFuture)this.downloadResourcePack(address, id).thenRun(() -> RealmsPrepareConnectionTask.setScreen((Screen)((Screen)connectingScreenCreator.apply(address))))).exceptionally(throwable -> {
                MinecraftClient.getInstance().getServerResourcePackProvider().clear();
                LOGGER.error("Failed to download resource pack from {}", (Object)address, throwable);
                RealmsPrepareConnectionTask.setScreen((Screen)new RealmsGenericErrorScreen((Text)Text.translatable((String)"mco.download.resourcePack.fail"), this.lastScreen));
                return null;
            });
        });
    }

    private CompletableFuture<?> downloadResourcePack(RealmsServerAddress address, UUID id) {
        try {
            if (address.resourcePackUrl() == null) {
                return CompletableFuture.failedFuture(new IllegalStateException("resourcePackUrl was null"));
            }
            if (address.resourcePackHash() == null) {
                return CompletableFuture.failedFuture(new IllegalStateException("resourcePackHash was null"));
            }
            ServerResourcePackLoader serverResourcePackLoader = MinecraftClient.getInstance().getServerResourcePackProvider();
            CompletableFuture completableFuture = serverResourcePackLoader.getPackLoadFuture(id);
            serverResourcePackLoader.acceptAll();
            serverResourcePackLoader.addResourcePack(id, new URL(address.resourcePackUrl()), address.resourcePackHash());
            return completableFuture;
        }
        catch (Exception exception) {
            return CompletableFuture.failedFuture(exception);
        }
    }
}

