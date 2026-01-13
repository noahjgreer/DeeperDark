/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.slot.SlotActionType;

@Environment(value=EnvType.CLIENT)
static class CrafterScreen.1 {
    static final /* synthetic */ int[] field_47118;

    static {
        field_47118 = new int[SlotActionType.values().length];
        try {
            CrafterScreen.1.field_47118[SlotActionType.PICKUP.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CrafterScreen.1.field_47118[SlotActionType.SWAP.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
