/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text NOW_TEXT = Text.translatable("mco.util.time.now");
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_DAY = 86400;

    public static Text convertToAgePresentation(long milliseconds) {
        if (milliseconds < 0L) {
            return NOW_TEXT;
        }
        long l = milliseconds / 1000L;
        if (l < 60L) {
            return Text.translatable("mco.time.secondsAgo", l);
        }
        if (l < 3600L) {
            long m = l / 60L;
            return Text.translatable("mco.time.minutesAgo", m);
        }
        if (l < 86400L) {
            long m = l / 3600L;
            return Text.translatable("mco.time.hoursAgo", m);
        }
        long m = l / 86400L;
        return Text.translatable("mco.time.daysAgo", m);
    }

    public static Text convertToAgePresentation(Instant instant) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - instant.toEpochMilli());
    }

    public static void drawPlayerHead(DrawContext context, int x, int y, int size, UUID playerUuid) {
        PlayerSkinCache.Entry entry = MinecraftClient.getInstance().getPlayerSkinCache().get(ProfileComponent.ofDynamic(playerUuid));
        PlayerSkinDrawer.draw(context, entry.getTextures(), x, y, size);
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
        }, Util.getDownloadWorkerExecutor());
    }

    public static CompletableFuture<Void> runAsync(RealmsRunnable runnable, @Nullable Consumer<RealmsServiceException> errorCallback) {
        return RealmsUtil.runAsync(runnable, errorCallback);
    }

    public static Consumer<RealmsServiceException> openingScreen(Function<RealmsServiceException, Screen> screenCreator) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return error -> minecraftClient.execute(() -> minecraftClient.setScreen((Screen)screenCreator.apply((RealmsServiceException)error)));
    }

    public static Consumer<RealmsServiceException> openingScreenAndLogging(Function<RealmsServiceException, Screen> screenCreator, String errorPrefix) {
        return RealmsUtil.openingScreen(screenCreator).andThen(error -> LOGGER.error(errorPrefix, (Throwable)error));
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface RealmsSupplier<T> {
        public T apply(RealmsClient var1) throws RealmsServiceException;
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface RealmsRunnable
    extends RealmsSupplier<Void> {
        public void accept(RealmsClient var1) throws RealmsServiceException;

        @Override
        default public Void apply(RealmsClient realmsClient) throws RealmsServiceException {
            this.accept(realmsClient);
            return null;
        }
    }
}
