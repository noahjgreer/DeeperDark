/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.MusicToastMode;

@Environment(value=EnvType.CLIENT)
static class ToastManager.1 {
    static final /* synthetic */ int[] field_64543;

    static {
        field_64543 = new int[MusicToastMode.values().length];
        try {
            ToastManager.1.field_64543[MusicToastMode.PAUSE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ToastManager.1.field_64543[MusicToastMode.PAUSE_AND_TOAST.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ToastManager.1.field_64543[MusicToastMode.NEVER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
