/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen.tab;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.RealmsWorldSlotButton;

@Environment(value=EnvType.CLIENT)
static class RealmsWorldsTab.1 {
    static final /* synthetic */ int[] field_19812;

    static {
        field_19812 = new int[RealmsWorldSlotButton.Action.values().length];
        try {
            RealmsWorldsTab.1.field_19812[RealmsWorldSlotButton.Action.NOTHING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            RealmsWorldsTab.1.field_19812[RealmsWorldSlotButton.Action.SWITCH_SLOT.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
