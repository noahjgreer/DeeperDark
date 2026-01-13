/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import java.io.IOException;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.resource.ResourcePackOpener;
import net.minecraft.util.path.SymlinkFinder;

@Environment(value=EnvType.CLIENT)
class PackScreen.1
extends ResourcePackOpener<Path> {
    PackScreen.1(PackScreen packScreen, SymlinkFinder symlinkFinder) {
        super(symlinkFinder);
    }

    @Override
    protected Path openZip(Path path) {
        return path;
    }

    @Override
    protected Path openDirectory(Path path) {
        return path;
    }

    @Override
    protected /* synthetic */ Object openDirectory(Path path) throws IOException {
        return this.openDirectory(path);
    }

    @Override
    protected /* synthetic */ Object openZip(Path path) throws IOException {
        return this.openZip(path);
    }
}
