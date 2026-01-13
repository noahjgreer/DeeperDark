/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.client.realms.RealmsConnection
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsConnection {
    static final Logger LOGGER = LogUtils.getLogger();
    final Screen onlineScreen;
    volatile boolean aborted;
    @Nullable ClientConnection connection;

    public RealmsConnection(Screen onlineScreen) {
        this.onlineScreen = onlineScreen;
    }

    public void connect(RealmsServer server, ServerAddress address) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.loadBlockList();
        minecraftClient.getNarratorManager().narrateSystemImmediately((Text)Text.translatable((String)"mco.connect.success"));
        String string = address.getAddress();
        int i = address.getPort();
        new /* Unavailable Anonymous Inner Class!! */.start();
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isOpen()) {
            this.connection.disconnect((Text)Text.translatable((String)"disconnect.genericReason"));
            this.connection.handleDisconnection();
        }
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }
}

