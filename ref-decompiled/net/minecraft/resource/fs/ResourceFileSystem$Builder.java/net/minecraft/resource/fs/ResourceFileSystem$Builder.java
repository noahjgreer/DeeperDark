/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.resource.fs.ResourceFileSystem;

public static class ResourceFileSystem.Builder {
    private final ResourceFileSystem.Directory root = new ResourceFileSystem.Directory();

    public ResourceFileSystem.Builder withFile(List<String> directories, String name, Path path) {
        ResourceFileSystem.Directory directory2 = this.root;
        for (String string : directories) {
            directory2 = directory2.children.computeIfAbsent(string, directory -> new ResourceFileSystem.Directory());
        }
        directory2.files.put(name, path);
        return this;
    }

    public ResourceFileSystem.Builder withFile(List<String> directories, Path path) {
        if (directories.isEmpty()) {
            throw new IllegalArgumentException("Path can't be empty");
        }
        int i = directories.size() - 1;
        return this.withFile(directories.subList(0, i), directories.get(i), path);
    }

    public FileSystem build(String name) {
        return new ResourceFileSystem(name, this.root);
    }
}
