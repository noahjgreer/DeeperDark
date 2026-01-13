/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.realms.RealmsAvailability
 *  net.minecraft.client.realms.RealmsAvailability$Info
 *  net.minecraft.client.realms.RealmsAvailability$Type
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.RealmsClient$CompatibleVersionResponse
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.mojang.logging.LogUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.RealmsAvailability;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsAvailability {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static @Nullable CompletableFuture<// Could not load outer class - annotation placement on inner may be incorrect
    Info> currentFuture;

    public static CompletableFuture<Info> check() {
        if (currentFuture == null || RealmsAvailability.wasUnsuccessful((CompletableFuture)currentFuture)) {
            currentFuture = RealmsAvailability.checkInternal();
        }
        return currentFuture;
    }

    private static boolean wasUnsuccessful(CompletableFuture<Info> future) {
        Info info = future.getNow(null);
        return info != null && info.exception() != null;
    }

    private static CompletableFuture<Info> checkInternal() {
        if (MinecraftClient.getInstance().isOfflineDeveloperMode()) {
            return CompletableFuture.completedFuture(new Info(Type.AUTHENTICATION_ERROR));
        }
        if (SharedConstants.BYPASS_REALMS_VERSION_CHECK) {
            return CompletableFuture.completedFuture(new Info(Type.SUCCESS));
        }
        return CompletableFuture.supplyAsync(() -> {
            RealmsClient realmsClient = RealmsClient.create();
            try {
                if (realmsClient.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
                    return new Info(Type.INCOMPATIBLE_CLIENT);
                }
                if (!realmsClient.mcoEnabled()) {
                    return new Info(Type.NEEDS_PARENTAL_CONSENT);
                }
                return new Info(Type.SUCCESS);
            }
            catch (RealmsServiceException realmsServiceException) {
                LOGGER.error("Couldn't connect to realms", (Throwable)realmsServiceException);
                if (realmsServiceException.error.getErrorCode() == 401) {
                    return new Info(Type.AUTHENTICATION_ERROR);
                }
                return new Info(realmsServiceException);
            }
        }, (Executor)Util.getIoWorkerExecutor());
    }
}

