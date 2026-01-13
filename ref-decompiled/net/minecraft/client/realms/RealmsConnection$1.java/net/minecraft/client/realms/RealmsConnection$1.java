/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.net.InetSocketAddress;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.resource.server.ServerResourcePackManager;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class RealmsConnection.1
extends Thread {
    final /* synthetic */ String field_11112;
    final /* synthetic */ int field_11114;
    final /* synthetic */ MinecraftClient field_22818;
    final /* synthetic */ RealmsServer field_26928;

    RealmsConnection.1(String string, String string2, int i, MinecraftClient minecraftClient, RealmsServer realmsServer) {
        this.field_11112 = string2;
        this.field_11114 = i;
        this.field_22818 = minecraftClient;
        this.field_26928 = realmsServer;
        super(string);
    }

    @Override
    public void run() {
        InetSocketAddress inetSocketAddress = null;
        try {
            inetSocketAddress = new InetSocketAddress(this.field_11112, this.field_11114);
            if (RealmsConnection.this.aborted) {
                return;
            }
            RealmsConnection.this.connection = ClientConnection.connect(inetSocketAddress, NetworkingBackend.remote(this.field_22818.options.shouldUseNativeTransport()), this.field_22818.getDebugHud().getPacketSizeLog());
            if (RealmsConnection.this.aborted) {
                return;
            }
            ClientLoginNetworkHandler clientLoginNetworkHandler = new ClientLoginNetworkHandler(RealmsConnection.this.connection, this.field_22818, this.field_26928.createServerInfo(this.field_11112), RealmsConnection.this.onlineScreen, false, null, status -> {}, new ClientChunkLoadProgress(), null);
            if (this.field_26928.isMinigame()) {
                clientLoginNetworkHandler.setMinigameName(this.field_26928.minigameName);
            }
            if (RealmsConnection.this.aborted) {
                return;
            }
            RealmsConnection.this.connection.connect(this.field_11112, this.field_11114, clientLoginNetworkHandler);
            if (RealmsConnection.this.aborted) {
                return;
            }
            RealmsConnection.this.connection.send(new LoginHelloC2SPacket(this.field_22818.getSession().getUsername(), this.field_22818.getSession().getUuidOrNull()));
            this.field_22818.ensureAbuseReportContext(ReporterEnvironment.ofRealm(this.field_26928));
            this.field_22818.getQuickPlayLogger().setWorld(QuickPlayLogger.WorldType.REALMS, String.valueOf(this.field_26928.id), Objects.requireNonNullElse(this.field_26928.name, "unknown"));
            this.field_22818.getServerResourcePackProvider().init(RealmsConnection.this.connection, ServerResourcePackManager.AcceptanceStatus.ALLOWED);
        }
        catch (Exception exception) {
            this.field_22818.getServerResourcePackProvider().clear();
            if (RealmsConnection.this.aborted) {
                return;
            }
            LOGGER.error("Couldn't connect to world", (Throwable)exception);
            String string = exception.toString();
            if (inetSocketAddress != null) {
                String string2 = String.valueOf(inetSocketAddress) + ":" + this.field_11114;
                string = string.replaceAll(string2, "");
            }
            DisconnectedScreen disconnectedScreen = new DisconnectedScreen(RealmsConnection.this.onlineScreen, (Text)Text.translatable("mco.connect.failed"), Text.translatable("disconnect.genericReason", string), ScreenTexts.BACK);
            this.field_22818.execute(() -> this.field_22818.setScreen(disconnectedScreen));
        }
    }
}
