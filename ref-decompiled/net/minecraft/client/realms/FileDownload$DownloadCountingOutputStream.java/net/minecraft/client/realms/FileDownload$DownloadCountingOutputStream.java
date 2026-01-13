/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.output.CountingOutputStream
 */
package net.minecraft.client.realms;

import java.io.IOException;
import java.io.OutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.gui.screen.RealmsDownloadLatestWorldScreen;
import org.apache.commons.io.output.CountingOutputStream;

@Environment(value=EnvType.CLIENT)
static class FileDownload.DownloadCountingOutputStream
extends CountingOutputStream {
    private final RealmsDownloadLatestWorldScreen.DownloadStatus status;

    public FileDownload.DownloadCountingOutputStream(OutputStream stream, RealmsDownloadLatestWorldScreen.DownloadStatus status) {
        super(stream);
        this.status = status;
    }

    protected void afterWrite(int n) throws IOException {
        super.afterWrite(n);
        this.status.bytesWritten = this.getByteCount();
    }
}
