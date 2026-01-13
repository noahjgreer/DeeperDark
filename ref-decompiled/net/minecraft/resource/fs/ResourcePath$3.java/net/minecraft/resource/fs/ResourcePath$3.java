/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource.fs;

import java.io.IOException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

class ResourcePath.3
implements BasicFileAttributeView {
    ResourcePath.3() {
    }

    @Override
    public String name() {
        return "basic";
    }

    @Override
    public BasicFileAttributes readAttributes() throws IOException {
        return ResourcePath.this.getAttributes();
    }

    @Override
    public void setTimes(FileTime lastModifiedTime, FileTime lastAccessFile, FileTime createTime) {
        throw new ReadOnlyFileSystemException();
    }
}
