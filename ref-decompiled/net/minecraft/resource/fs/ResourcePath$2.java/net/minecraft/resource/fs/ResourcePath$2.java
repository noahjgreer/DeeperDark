/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import net.minecraft.resource.fs.ResourceFileAttributes;

class ResourcePath.2
extends ResourceFileAttributes {
    ResourcePath.2() {
    }

    @Override
    public boolean isRegularFile() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
