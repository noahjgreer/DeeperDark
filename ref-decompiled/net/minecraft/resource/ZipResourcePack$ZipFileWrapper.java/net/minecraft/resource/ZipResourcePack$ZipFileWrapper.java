/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.resource;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.jspecify.annotations.Nullable;

static class ZipResourcePack.ZipFileWrapper
implements AutoCloseable {
    final File file;
    private @Nullable ZipFile zip;
    private boolean closed;

    ZipResourcePack.ZipFileWrapper(File file) {
        this.file = file;
    }

    @Nullable ZipFile open() {
        if (this.closed) {
            return null;
        }
        if (this.zip == null) {
            try {
                this.zip = new ZipFile(this.file);
            }
            catch (IOException iOException) {
                LOGGER.error("Failed to open pack {}", (Object)this.file, (Object)iOException);
                this.closed = true;
                return null;
            }
        }
        return this.zip;
    }

    @Override
    public void close() {
        if (this.zip != null) {
            IOUtils.closeQuietly((Closeable)this.zip);
            this.zip = null;
        }
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
