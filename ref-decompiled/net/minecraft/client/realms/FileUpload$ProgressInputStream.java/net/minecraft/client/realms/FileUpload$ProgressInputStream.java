/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.input.CountingInputStream
 */
package net.minecraft.client.realms;

import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.UploadProgress;
import org.apache.commons.io.input.CountingInputStream;

@Environment(value=EnvType.CLIENT)
static class FileUpload.ProgressInputStream
extends CountingInputStream {
    private final UploadProgress progress;

    FileUpload.ProgressInputStream(InputStream stream, UploadProgress progress) {
        super(stream);
        this.progress = progress;
    }

    protected void afterRead(int n) throws IOException {
        super.afterRead(n);
        this.progress.addBytesWritten(this.getByteCount());
    }
}
