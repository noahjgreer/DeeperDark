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
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;

@Environment(value=EnvType.CLIENT)
class BeaconScreen.1
implements ScreenHandlerListener {
    final /* synthetic */ BeaconScreenHandler field_17414;

    BeaconScreen.1() {
        this.field_17414 = beaconScreenHandler;
    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
        BeaconScreen.this.primaryEffect = this.field_17414.getPrimaryEffect();
        BeaconScreen.this.secondaryEffect = this.field_17414.getSecondaryEffect();
    }
}
