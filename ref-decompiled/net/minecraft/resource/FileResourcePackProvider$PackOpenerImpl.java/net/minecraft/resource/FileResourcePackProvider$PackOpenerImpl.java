/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.resource;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePackOpener;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.resource.fs.ResourceFileSystem;
import net.minecraft.util.path.SymlinkFinder;
import org.jspecify.annotations.Nullable;

static class FileResourcePackProvider.PackOpenerImpl
extends ResourcePackOpener<ResourcePackProfile.PackFactory> {
    protected FileResourcePackProvider.PackOpenerImpl(SymlinkFinder symlinkFinder) {
        super(symlinkFinder);
    }

    @Override
    protected  @Nullable ResourcePackProfile.PackFactory openZip(Path path) {
        FileSystem fileSystem = path.getFileSystem();
        if (fileSystem == FileSystems.getDefault() || fileSystem instanceof ResourceFileSystem) {
            return new ZipResourcePack.ZipBackedFactory(path);
        }
        LOGGER.info("Can't open pack archive at {}", (Object)path);
        return null;
    }

    @Override
    protected ResourcePackProfile.PackFactory openDirectory(Path path) {
        return new DirectoryResourcePack.DirectoryBackedFactory(path);
    }

    @Override
    protected /* synthetic */ Object openDirectory(Path path) throws IOException {
        return this.openDirectory(path);
    }

    @Override
    protected /* synthetic */ @Nullable Object openZip(Path path) throws IOException {
        return this.openZip(path);
    }
}
