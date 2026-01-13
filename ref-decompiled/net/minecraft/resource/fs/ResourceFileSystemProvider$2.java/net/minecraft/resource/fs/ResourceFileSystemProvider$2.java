/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.nio.file.AccessMode;

static class ResourceFileSystemProvider.2 {
    static final /* synthetic */ int[] field_40027;

    static {
        field_40027 = new int[AccessMode.values().length];
        try {
            ResourceFileSystemProvider.2.field_40027[AccessMode.READ.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ResourceFileSystemProvider.2.field_40027[AccessMode.EXECUTE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ResourceFileSystemProvider.2.field_40027[AccessMode.WRITE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
