/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.pack;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class PackScreen.DirectoryWatcher
implements AutoCloseable {
    private final WatchService watchService;
    private final Path path;

    public PackScreen.DirectoryWatcher(Path path) throws IOException {
        this.path = path;
        this.watchService = path.getFileSystem().newWatchService();
        try {
            this.watchDirectory(path);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);){
                for (Path path2 : directoryStream) {
                    if (!Files.isDirectory(path2, LinkOption.NOFOLLOW_LINKS)) continue;
                    this.watchDirectory(path2);
                }
            }
        }
        catch (Exception exception) {
            this.watchService.close();
            throw exception;
        }
    }

    public static @Nullable PackScreen.DirectoryWatcher create(Path path) {
        try {
            return new PackScreen.DirectoryWatcher(path);
        }
        catch (IOException iOException) {
            LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)path, (Object)iOException);
            return null;
        }
    }

    private void watchDirectory(Path path) throws IOException {
        path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public boolean pollForChange() throws IOException {
        WatchKey watchKey;
        boolean bl = false;
        while ((watchKey = this.watchService.poll()) != null) {
            List<WatchEvent<?>> list = watchKey.pollEvents();
            for (WatchEvent<?> watchEvent : list) {
                Path path;
                bl = true;
                if (watchKey.watchable() != this.path || watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory(path = this.path.resolve((Path)watchEvent.context()), LinkOption.NOFOLLOW_LINKS)) continue;
                this.watchDirectory(path);
            }
            watchKey.reset();
        }
        return bl;
    }

    @Override
    public void close() throws IOException {
        this.watchService.close();
    }
}
