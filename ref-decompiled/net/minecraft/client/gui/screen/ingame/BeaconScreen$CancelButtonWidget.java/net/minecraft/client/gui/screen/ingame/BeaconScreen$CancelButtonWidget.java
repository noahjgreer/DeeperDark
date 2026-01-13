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
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.screen.ScreenTexts;

@Environment(value=EnvType.CLIENT)
class BeaconScreen.CancelButtonWidget
extends BeaconScreen.IconButtonWidget {
    public BeaconScreen.CancelButtonWidget(int x, int y) {
        super(x, y, CANCEL_TEXTURE, ScreenTexts.CANCEL);
    }

    @Override
    public void onPress(AbstractInput input) {
        ((BeaconScreen)BeaconScreen.this).client.player.closeHandledScreen();
    }

    @Override
    public void tick(int level) {
    }
}
