/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.exceptions.MinecraftClientException$ErrorType
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.exceptions.MinecraftClientException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class AbuseReportSender.1 {
    static final /* synthetic */ int[] field_39641;

    static {
        field_39641 = new int[MinecraftClientException.ErrorType.values().length];
        try {
            AbuseReportSender.1.field_39641[MinecraftClientException.ErrorType.SERVICE_UNAVAILABLE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbuseReportSender.1.field_39641[MinecraftClientException.ErrorType.HTTP_ERROR.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbuseReportSender.1.field_39641[MinecraftClientException.ErrorType.JSON_ERROR.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
