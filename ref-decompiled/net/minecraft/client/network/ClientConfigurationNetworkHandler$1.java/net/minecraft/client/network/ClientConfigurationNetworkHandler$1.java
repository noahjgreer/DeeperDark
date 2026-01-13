/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.network.state.PlayStateFactories;

@Environment(value=EnvType.CLIENT)
class ClientConfigurationNetworkHandler.1
implements PlayStateFactories.PacketCodecModifierContext {
    ClientConfigurationNetworkHandler.1(ClientConfigurationNetworkHandler clientConfigurationNetworkHandler) {
    }

    @Override
    public boolean isInCreativeMode() {
        return true;
    }
}
