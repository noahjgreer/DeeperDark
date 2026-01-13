/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 */
package net.minecraft.data;

import com.google.common.hash.HashCode;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

static final class SnbtProvider.CompressedData
extends Record {
    final String name;
    final byte[] bytes;
    final HashCode sha1;

    SnbtProvider.CompressedData(String name, byte[] bytes, HashCode sha1) {
        this.name = name;
        this.bytes = bytes;
        this.sha1 = sha1;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SnbtProvider.CompressedData.class, "name;payload;hash", "name", "bytes", "sha1"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SnbtProvider.CompressedData.class, "name;payload;hash", "name", "bytes", "sha1"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SnbtProvider.CompressedData.class, "name;payload;hash", "name", "bytes", "sha1"}, this, object);
    }

    public String name() {
        return this.name;
    }

    public byte[] bytes() {
        return this.bytes;
    }

    public HashCode sha1() {
        return this.sha1;
    }
}
