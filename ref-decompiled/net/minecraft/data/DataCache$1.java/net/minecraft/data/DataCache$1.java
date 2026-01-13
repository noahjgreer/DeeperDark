/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.data;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import org.apache.commons.lang3.mutable.MutableInt;

class DataCache.1
extends SimpleFileVisitor<Path> {
    final /* synthetic */ MutableInt field_48462;
    final /* synthetic */ Set field_48463;
    final /* synthetic */ MutableInt field_48464;

    DataCache.1(MutableInt mutableInt, Set set, MutableInt mutableInt2) {
        this.field_48462 = mutableInt;
        this.field_48463 = set;
        this.field_48464 = mutableInt2;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) {
        if (DataCache.this.paths.contains(path)) {
            return FileVisitResult.CONTINUE;
        }
        this.field_48462.increment();
        if (this.field_48463.contains(path)) {
            return FileVisitResult.CONTINUE;
        }
        try {
            Files.delete(path);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to delete file {}", (Object)path, (Object)iOException);
        }
        this.field_48464.increment();
        return FileVisitResult.CONTINUE;
    }

    @Override
    public /* synthetic */ FileVisitResult visitFile(Object path, BasicFileAttributes attributes) throws IOException {
        return this.visitFile((Path)path, attributes);
    }
}
