/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.IoHandlerFactory
 *  io.netty.channel.epoll.EpollIoHandler
 */
package net.minecraft.network;

import io.netty.channel.IoHandlerFactory;
import io.netty.channel.epoll.EpollIoHandler;
import net.minecraft.network.NetworkingBackend;

class NetworkingBackend.2
extends NetworkingBackend {
    NetworkingBackend.2(String string, Class class_, Class class2) {
        super(string, class_, class2);
    }

    @Override
    protected IoHandlerFactory newFactory() {
        return EpollIoHandler.newFactory();
    }
}
