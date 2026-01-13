/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenTexts;

@Environment(value=EnvType.CLIENT)
class BeaconScreen.DoneButtonWidget
extends BeaconScreen.IconButtonWidget {
    public BeaconScreen.DoneButtonWidget(int x, int y) {
        super(x, y, CONFIRM_TEXTURE, ScreenTexts.DONE);
    }

    @Override
    public void onPress(AbstractInput input) {
        BeaconScreen.this.client.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(Optional.ofNullable(BeaconScreen.this.primaryEffect), Optional.ofNullable(BeaconScreen.this.secondaryEffect)));
        ((BeaconScreen)BeaconScreen.this).client.player.closeHandledScreen();
    }

    @Override
    public void tick(int level) {
        this.active = ((BeaconScreenHandler)BeaconScreen.this.handler).hasPayment() && BeaconScreen.this.primaryEffect != null;
    }
}
