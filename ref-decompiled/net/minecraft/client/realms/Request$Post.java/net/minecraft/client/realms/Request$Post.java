/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.exception.RealmsHttpException;

@Environment(value=EnvType.CLIENT)
public static class Request.Post
extends Request<Request.Post> {
    private final String content;

    public Request.Post(String uri, String content, int connectTimeout, int readTimeout) {
        super(uri, connectTimeout, readTimeout);
        this.content = content;
    }

    @Override
    public Request.Post doConnect() {
        try {
            if (this.content != null) {
                this.connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            }
            this.connection.setDoInput(true);
            this.connection.setDoOutput(true);
            this.connection.setUseCaches(false);
            this.connection.setRequestMethod("POST");
            OutputStream outputStream = this.connection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            outputStreamWriter.write(this.content);
            outputStreamWriter.close();
            outputStream.flush();
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
