/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.CursorMovement;

@Environment(value=EnvType.CLIENT)
static class EditBox.1 {
    static final /* synthetic */ int[] field_39523;

    static {
        field_39523 = new int[CursorMovement.values().length];
        try {
            EditBox.1.field_39523[CursorMovement.ABSOLUTE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EditBox.1.field_39523[CursorMovement.RELATIVE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            EditBox.1.field_39523[CursorMovement.END.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
