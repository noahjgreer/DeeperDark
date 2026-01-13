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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;

@Environment(value=EnvType.CLIENT)
class LecternScreen.1
implements ScreenHandlerListener {
    LecternScreen.1() {
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        LecternScreen.this.updatePageProvider();
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
        if (property == 0) {
            LecternScreen.this.updatePage();
        }
    }
}
