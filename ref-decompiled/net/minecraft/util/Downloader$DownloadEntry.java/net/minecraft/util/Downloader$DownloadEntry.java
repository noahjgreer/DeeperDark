/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import com.google.common.hash.HashCode;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.URL;
import org.jspecify.annotations.Nullable;

public static final class Downloader.DownloadEntry
extends Record {
    final URL url;
    final @Nullable HashCode hash;

    public Downloader.DownloadEntry(URL url, @Nullable HashCode hash) {
        this.url = url;
        this.hash = hash;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Downloader.DownloadEntry.class, "url;hash", "url", "hash"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Downloader.DownloadEntry.class, "url;hash", "url", "hash"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Downloader.DownloadEntry.class, "url;hash", "url", "hash"}, this, object);
    }

    public URL url() {
        return this.url;
    }

    public @Nullable HashCode hash() {
        return this.hash;
    }
}
