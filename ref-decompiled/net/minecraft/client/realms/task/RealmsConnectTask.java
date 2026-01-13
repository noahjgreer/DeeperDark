/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.client.realms.RealmsConnection
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServerAddress
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.RealmsConnectTask
 *  net.minecraft.text.Text
 */
package net.minecraft.client.realms.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.RealmsConnection;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RealmsConnectTask
extends LongRunningTask {
    private static final Text TITLE = Text.translatable((String)"mco.connect.connecting");
    private final RealmsConnection realmsConnection;
    private final RealmsServer server;
    private final RealmsServerAddress address;

    public RealmsConnectTask(Screen lastScreen, RealmsServer server, RealmsServerAddress address) {
        this.server = server;
        this.address = address;
        this.realmsConnection = new RealmsConnection(lastScreen);
    }

    public void run() {
        if (this.address.address() != null) {
            this.realmsConnection.connect(this.server, ServerAddress.parse((String)this.address.address()));
        } else {
            this.abortTask();
        }
    }

    public void abortTask() {
        super.abortTask();
        this.realmsConnection.abort();
        MinecraftClient.getInstance().getServerResourcePackProvider().clear();
    }

    public void tick() {
        this.realmsConnection.tick();
    }

    public Text getTitle() {
        return TITLE;
    }
}

