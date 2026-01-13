/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.resource.fs.ResourceFile;
import net.minecraft.resource.fs.ResourceFileSystemProvider;

class ResourceFileSystemProvider.1
implements DirectoryStream<Path> {
    final /* synthetic */ ResourceFile.Directory field_40024;
    final /* synthetic */ DirectoryStream.Filter field_40025;

    ResourceFileSystemProvider.1(ResourceFileSystemProvider resourceFileSystemProvider, ResourceFile.Directory directory, DirectoryStream.Filter filter) {
        this.field_40024 = directory;
        this.field_40025 = filter;
    }

    @Override
    public Iterator<Path> iterator() {
        return this.field_40024.children().values().stream().filter(child -> {
            try {
                return this.field_40025.accept(child);
            }
            catch (IOException iOException) {
                throw new DirectoryIteratorException(iOException);
            }
        }).map(child -> child).iterator();
    }

    @Override
    public void close() {
    }
}
