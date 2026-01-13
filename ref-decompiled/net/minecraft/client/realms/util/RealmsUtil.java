/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.PlayerSkinDrawer
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.util.RealmsUtil
 *  net.minecraft.client.realms.util.RealmsUtil$RealmsRunnable
 *  net.minecraft.client.realms.util.RealmsUtil$RealmsSupplier
 *  net.minecraft.client.texture.PlayerSkinCache$Entry
 *  net.minecraft.component.type.ProfileComponent
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text NOW_TEXT = Text.translatable((String)"mco.util.time.now");
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_DAY = 86400;

    public static Text convertToAgePresentation(long milliseconds) {
        if (milliseconds < 0L) {
            return NOW_TEXT;
        }
        long l = milliseconds / 1000L;
        if (l < 60L) {
            return Text.translatable((String)"mco.time.secondsAgo", (Object[])new Object[]{l});
        }
        if (l < 3600L) {
            long m = l / 60L;
            return Text.translatable((String)"mco.time.minutesAgo", (Object[])new Object[]{m});
        }
        if (l < 86400L) {
            long m = l / 3600L;
            return Text.translatable((String)"mco.time.hoursAgo", (Object[])new Object[]{m});
        }
        long m = l / 86400L;
        return Text.translatable((String)"mco.time.daysAgo", (Object[])new Object[]{m});
    }

    public static Text convertToAgePresentation(Instant instant) {
        return RealmsUtil.convertToAgePresentation((long)(System.currentTimeMillis() - instant.toEpochMilli()));
    }

    public static void drawPlayerHead(DrawContext context, int x, int y, int size, UUID playerUuid) {
        PlayerSkinCache.Entry entry = MinecraftClient.getInstance().getPlayerSkinCache().get(ProfileComponent.ofDynamic((UUID)playerUuid));
        PlayerSkinDrawer.draw((DrawContext)context, (SkinTextures)entry.getTextures(), (int)x, (int)y, (int)size);
    }

    public static <T> CompletableFuture<T> runAsync(RealmsSupplier<T> supplier, @Nullable Consumer<RealmsServiceException> errorCallback) {
        return CompletableFuture.supplyAsync(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            try {
                return supplier.apply(realmsClient);
            }
            catch (Throwable throwable) {
                if (throwable instanceof RealmsServiceException) {
                    RealmsServiceException realmsServiceException = (RealmsServiceException)throwable;
                    if (errorCallback != null) {
                        errorCallback.accept(realmsServiceException);
                    }
                } else {
                    LOGGER.error("Unhandled exception", throwable);
                }
                throw new RuntimeException(throwable);
            }
        }, (Executor)Util.getDownloadWorkerExecutor());
    }

    public static CompletableFuture<Void> runAsync(RealmsRunnable runnable, @Nullable Consumer<RealmsServiceException> errorCallback) {
        return RealmsUtil.runAsync((RealmsSupplier)runnable, errorCallback);
    }

    public static Consumer<RealmsServiceException> openingScreen(Function<RealmsServiceException, Screen> screenCreator) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return error -> minecraftClient.execute(() -> minecraftClient.setScreen((Screen)screenCreator.apply((RealmsServiceException)((Object)error))));
    }

    public static Consumer<RealmsServiceException> openingScreenAndLogging(Function<RealmsServiceException, Screen> screenCreator, String errorPrefix) {
        return RealmsUtil.openingScreen(screenCreator).andThen(error -> LOGGER.error(errorPrefix, (Throwable)error));
    }
}

