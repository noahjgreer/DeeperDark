/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.dialog.AfterAction;

@Environment(value=EnvType.CLIENT)
static class DialogScreen.1 {
    static final /* synthetic */ int[] field_61009;

    static {
        field_61009 = new int[AfterAction.values().length];
        try {
            DialogScreen.1.field_61009[AfterAction.NONE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DialogScreen.1.field_61009[AfterAction.CLOSE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            DialogScreen.1.field_61009[AfterAction.WAIT_FOR_RESPONSE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
