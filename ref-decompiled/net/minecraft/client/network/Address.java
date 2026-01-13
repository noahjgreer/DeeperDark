/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.Address
 */
package net.minecraft.client.network;

import java.net.InetSocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface Address {
    public String getHostName();

    public String getHostAddress();

    public int getPort();

    public InetSocketAddress getInetSocketAddress();

    public static Address create(InetSocketAddress address) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }
}

