/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.util.OptionalLong;

public static interface NetworkUtils.DownloadListener {
    public void onStart();

    public void onContentLength(OptionalLong var1);

    public void onProgress(long var1);

    public void onFinish(boolean var1);
}
