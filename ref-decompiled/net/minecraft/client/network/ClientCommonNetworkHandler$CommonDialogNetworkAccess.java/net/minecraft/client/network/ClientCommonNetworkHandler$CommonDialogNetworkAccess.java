/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
protected abstract class ClientCommonNetworkHandler.CommonDialogNetworkAccess
implements DialogNetworkAccess {
    protected ClientCommonNetworkHandler.CommonDialogNetworkAccess() {
    }

    @Override
    public void disconnect(Text reason) {
        ClientCommonNetworkHandler.this.connection.disconnect(reason);
        ClientCommonNetworkHandler.this.connection.handleDisconnection();
    }

    @Override
    public void showDialog(RegistryEntry<Dialog> dialog, @Nullable Screen afterActionScreen) {
        ClientCommonNetworkHandler.this.showDialog(dialog, this, afterActionScreen);
    }

    @Override
    public void sendCustomClickActionPacket(Identifier id, Optional<NbtElement> payload) {
        ClientCommonNetworkHandler.this.sendPacket(new CustomClickActionC2SPacket(id, payload));
    }

    @Override
    public ServerLinks getServerLinks() {
        return ClientCommonNetworkHandler.this.getServerLinks();
    }
}
