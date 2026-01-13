/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.exception.RealmsHttpException;

@Environment(value=EnvType.CLIENT)
public static class Request.Get
extends Request<Request.Get> {
    public Request.Get(String string, int i, int j) {
        super(string, i, j);
    }

    @Override
    public Request.Get doConnect() {
        try {
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("GET");
            return this;
        }
        catch (Exception exception) {
            throw new RealmsHttpException(exception.getMessage(), exception);
        }
    }

    @Override
    public /* synthetic */ Request doConnect() {
        return this.doConnect();
    }
}
