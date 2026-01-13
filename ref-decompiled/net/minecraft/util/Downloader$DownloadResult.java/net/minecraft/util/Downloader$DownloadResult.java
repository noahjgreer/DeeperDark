/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public static final class Downloader.DownloadResult
extends Record {
    final Map<UUID, Path> downloaded;
    final Set<UUID> failed;

    public Downloader.DownloadResult() {
        this(new HashMap<UUID, Path>(), new HashSet<UUID>());
    }

    public Downloader.DownloadResult(Map<UUID, Path> downloaded, Set<UUID> failed) {
        this.downloaded = downloaded;
        this.failed = failed;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Downloader.DownloadResult.class, "downloaded;failed", "downloaded", "failed"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Downloader.DownloadResult.class, "downloaded;failed", "downloaded", "failed"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Downloader.DownloadResult.class, "downloaded;failed", "downloaded", "failed"}, this, object);
    }

    public Map<UUID, Path> downloaded() {
        return this.downloaded;
    }

    public Set<UUID> failed() {
        return this.failed;
    }
}
