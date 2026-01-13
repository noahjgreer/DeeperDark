/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import net.minecraft.resource.fs.ResourceFileAttributes;

class ResourcePath.1
extends ResourceFileAttributes {
    ResourcePath.1() {
    }

    @Override
    public boolean isRegularFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
