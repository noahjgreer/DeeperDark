/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.IoHandlerFactory
 *  io.netty.channel.nio.NioIoHandler
 */
package net.minecraft.network;

import io.netty.channel.IoHandlerFactory;
import io.netty.channel.nio.NioIoHandler;
import net.minecraft.network.NetworkingBackend;

class NetworkingBackend.1
extends NetworkingBackend {
    NetworkingBackend.1(String string, Class class_, Class class2) {
        super(string, class_, class2);
    }

    @Override
    protected IoHandlerFactory newFactory() {
        return NioIoHandler.newFactory();
    }
}
