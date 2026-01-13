/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class ClientPlayNetworkHandler.2
extends ClientCommonNetworkHandler.CommonDialogNetworkAccess {
    ClientPlayNetworkHandler.2() {
        super(ClientPlayNetworkHandler.this);
    }

    @Override
    public void runClickEventCommand(String command, @Nullable Screen afterActionScreen) {
        ClientPlayNetworkHandler.this.runClickEventCommand(command, afterActionScreen);
    }
}
