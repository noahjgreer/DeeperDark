/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.server.PackStateChangeCallback;
import net.minecraft.client.resource.server.ServerResourcePackManager;

@Environment(value=EnvType.CLIENT)
static class ServerResourcePackLoader.8 {
    static final /* synthetic */ int[] field_47698;
    static final /* synthetic */ int[] field_47621;
    static final /* synthetic */ int[] field_47620;

    static {
        field_47620 = new int[ServerResourcePackManager.AcceptanceStatus.values().length];
        try {
            ServerResourcePackLoader.8.field_47620[ServerResourcePackManager.AcceptanceStatus.ALLOWED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerResourcePackLoader.8.field_47620[ServerResourcePackManager.AcceptanceStatus.DECLINED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerResourcePackLoader.8.field_47620[ServerResourcePackManager.AcceptanceStatus.PENDING.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_47621 = new int[PackStateChangeCallback.FinishState.values().length];
        try {
            ServerResourcePackLoader.8.field_47621[PackStateChangeCallback.FinishState.APPLIED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerResourcePackLoader.8.field_47621[PackStateChangeCallback.FinishState.DOWNLOAD_FAILED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerResourcePackLoader.8.field_47621[PackStateChangeCallback.FinishState.DECLINED.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerResourcePackLoader.8.field_47621[PackStateChangeCallback.FinishState.DISCARDED.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerResourcePackLoader.8.field_47621[PackStateChangeCallback.FinishState.ACTIVATION_FAILED.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_47698 = new int[PackStateChangeCallback.State.values().length];
        try {
            ServerResourcePackLoader.8.field_47698[PackStateChangeCallback.State.ACCEPTED.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerResourcePackLoader.8.field_47698[PackStateChangeCallback.State.DOWNLOADED.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
