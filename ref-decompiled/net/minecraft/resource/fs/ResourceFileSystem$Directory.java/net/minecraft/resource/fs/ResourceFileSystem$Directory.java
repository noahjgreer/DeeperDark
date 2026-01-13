/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

static final class ResourceFileSystem.Directory
extends Record {
    final Map<String, ResourceFileSystem.Directory> children;
    final Map<String, Path> files;

    public ResourceFileSystem.Directory() {
        this(new HashMap<String, ResourceFileSystem.Directory>(), new HashMap<String, Path>());
    }

    private ResourceFileSystem.Directory(Map<String, ResourceFileSystem.Directory> children, Map<String, Path> files) {
        this.children = children;
        this.files = files;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ResourceFileSystem.Directory.class, "children;files", "children", "files"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ResourceFileSystem.Directory.class, "children;files", "children", "files"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ResourceFileSystem.Directory.class, "children;files", "children", "files"}, this, object);
    }

    public Map<String, ResourceFileSystem.Directory> children() {
        return this.children;
    }

    public Map<String, Path> files() {
        return this.files;
    }
}
