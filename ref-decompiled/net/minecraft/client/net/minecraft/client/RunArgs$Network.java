/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import java.net.Proxy;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.Session;

@Environment(value=EnvType.CLIENT)
public static class RunArgs.Network {
    public final Session session;
    public final Proxy netProxy;

    public RunArgs.Network(Session session, Proxy proxy) {
        this.session = session;
        this.netProxy = proxy;
    }
}
