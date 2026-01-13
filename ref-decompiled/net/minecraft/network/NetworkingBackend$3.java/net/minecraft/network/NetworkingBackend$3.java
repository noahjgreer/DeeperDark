/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.IoHandlerFactory
 *  io.netty.channel.kqueue.KQueueIoHandler
 */
package net.minecraft.network;

import io.netty.channel.IoHandlerFactory;
import io.netty.channel.kqueue.KQueueIoHandler;
import net.minecraft.network.NetworkingBackend;

class NetworkingBackend.3
extends NetworkingBackend {
    NetworkingBackend.3(String string, Class class_, Class class2) {
        super(string, class_, class2);
    }

    @Override
    protected IoHandlerFactory newFactory() {
        return KQueueIoHandler.newFactory();
    }
}
