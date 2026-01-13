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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;

@Environment(value=EnvType.CLIENT)
class RealmsMainScreen.1
extends Thread {
    final /* synthetic */ RealmsServer field_34774;

    RealmsMainScreen.1(String string, RealmsServer realmsServer) {
        this.field_34774 = realmsServer;
        super(string);
    }

    @Override
    public void run() {
        try {
            RealmsClient realmsClient = RealmsClient.create();
            realmsClient.uninviteMyselfFrom(this.field_34774.id);
            RealmsMainScreen.this.client.execute(RealmsMainScreen::resetServerList);
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't configure world", (Throwable)realmsServiceException);
            RealmsMainScreen.this.client.execute(() -> RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(realmsServiceException, (Screen)RealmsMainScreen.this)));
        }
    }
}
