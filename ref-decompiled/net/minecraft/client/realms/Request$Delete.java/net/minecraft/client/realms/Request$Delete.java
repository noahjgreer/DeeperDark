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
public static class Request.Delete
extends Request<Request.Delete> {
    public Request.Delete(String string, int i, int j) {
        super(string, i, j);
    }

    @Override
    public Request.Delete doConnect() {
        try {
            this.connection.setDoOutput(true);
            this.connection.setRequestMethod("DELETE");
            this.connection.connect();
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
