/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(value=EnvType.CLIENT)
static class RealmsMainScreen.2 {
    static final /* synthetic */ int[] field_45221;
    static final /* synthetic */ int[] field_46674;

    static {
        field_46674 = new int[RealmsServer.Compatibility.values().length];
        try {
            RealmsMainScreen.2.field_46674[RealmsServer.Compatibility.COMPATIBLE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RealmsMainScreen.2.field_46674[RealmsServer.Compatibility.UNVERIFIABLE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RealmsMainScreen.2.field_46674[RealmsServer.Compatibility.NEEDS_DOWNGRADE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RealmsMainScreen.2.field_46674[RealmsServer.Compatibility.NEEDS_UPGRADE.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RealmsMainScreen.2.field_46674[RealmsServer.Compatibility.INCOMPATIBLE.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RealmsMainScreen.2.field_46674[RealmsServer.Compatibility.RELEASE_TYPE_INCOMPATIBLE.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_45221 = new int[RealmsClient.Environment.values().length];
        try {
            RealmsMainScreen.2.field_45221[RealmsClient.Environment.STAGE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RealmsMainScreen.2.field_45221[RealmsClient.Environment.LOCAL.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
