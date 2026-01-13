/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resource.OverlayResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ZipResourcePack;

public static class ZipResourcePack.ZipBackedFactory
implements ResourcePackProfile.PackFactory {
    private final File file;

    public ZipResourcePack.ZipBackedFactory(Path path) {
        this(path.toFile());
    }

    public ZipResourcePack.ZipBackedFactory(File file) {
        this.file = file;
    }

    @Override
    public ResourcePack open(ResourcePackInfo info) {
        ZipResourcePack.ZipFileWrapper zipFileWrapper = new ZipResourcePack.ZipFileWrapper(this.file);
        return new ZipResourcePack(info, zipFileWrapper, "");
    }

    @Override
    public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
        ZipResourcePack.ZipFileWrapper zipFileWrapper = new ZipResourcePack.ZipFileWrapper(this.file);
        ZipResourcePack resourcePack = new ZipResourcePack(info, zipFileWrapper, "");
        List<String> list = metadata.overlays();
        if (list.isEmpty()) {
            return resourcePack;
        }
        ArrayList<ResourcePack> list2 = new ArrayList<ResourcePack>(list.size());
        for (String string : list) {
            list2.add(new ZipResourcePack(info, zipFileWrapper, string));
        }
        return new OverlayResourcePack(resourcePack, list2);
    }
}
