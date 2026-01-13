/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.IoHandlerFactory
 *  io.netty.channel.local.LocalIoHandler
 */
package net.minecraft.network;

import io.netty.channel.IoHandlerFactory;
import io.netty.channel.local.LocalIoHandler;
import net.minecraft.network.NetworkingBackend;

class NetworkingBackend.4
extends NetworkingBackend {
    NetworkingBackend.4(String string, Class class_, Class class2) {
        super(string, class_, class2);
    }

    @Override
    protected IoHandlerFactory newFactory() {
        return LocalIoHandler.newFactory();
    }
}
