/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashFunction
 */
package net.minecraft.util;

import com.google.common.hash.HashFunction;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.Proxy;
import java.util.Map;
import net.minecraft.util.NetworkUtils;

public static final class Downloader.Config
extends Record {
    final HashFunction hashFunction;
    final int maxSize;
    final Map<String, String> headers;
    final Proxy proxy;
    final NetworkUtils.DownloadListener listener;

    public Downloader.Config(HashFunction hashFunction, int maxSize, Map<String, String> headers, Proxy proxy, NetworkUtils.DownloadListener listener) {
        this.hashFunction = hashFunction;
        this.maxSize = maxSize;
        this.headers = headers;
        this.proxy = proxy;
        this.listener = listener;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Downloader.Config.class, "hashFunction;maxSize;headers;proxy;listener", "hashFunction", "maxSize", "headers", "proxy", "listener"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Downloader.Config.class, "hashFunction;maxSize;headers;proxy;listener", "hashFunction", "maxSize", "headers", "proxy", "listener"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Downloader.Config.class, "hashFunction;maxSize;headers;proxy;listener", "hashFunction", "maxSize", "headers", "proxy", "listener"}, this, object);
    }

    public HashFunction hashFunction() {
        return this.hashFunction;
    }

    public int maxSize() {
        return this.maxSize;
    }

    public Map<String, String> headers() {
        return this.headers;
    }

    public Proxy proxy() {
        return this.proxy;
    }

    public NetworkUtils.DownloadListener listener() {
        return this.listener;
    }
}
