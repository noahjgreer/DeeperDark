/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.net.InetSocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;

@Environment(value=EnvType.CLIENT)
static class Address.1
implements Address {
    final /* synthetic */ InetSocketAddress field_33742;

    Address.1(InetSocketAddress inetSocketAddress) {
        this.field_33742 = inetSocketAddress;
    }

    @Override
    public String getHostName() {
        return this.field_33742.getAddress().getHostName();
    }

    @Override
    public String getHostAddress() {
        return this.field_33742.getAddress().getHostAddress();
    }

    @Override
    public int getPort() {
        return this.field_33742.getPort();
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return this.field_33742;
    }
}
