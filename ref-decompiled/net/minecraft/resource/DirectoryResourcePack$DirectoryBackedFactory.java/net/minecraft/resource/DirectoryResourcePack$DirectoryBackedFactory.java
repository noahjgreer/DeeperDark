/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.OverlayResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackProfile;

public static class DirectoryResourcePack.DirectoryBackedFactory
implements ResourcePackProfile.PackFactory {
    private final Path path;

    public DirectoryResourcePack.DirectoryBackedFactory(Path path) {
        this.path = path;
    }

    @Override
    public ResourcePack open(ResourcePackInfo info) {
        return new DirectoryResourcePack(info, this.path);
    }

    @Override
    public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
        ResourcePack resourcePack = this.open(info);
        List<String> list = metadata.overlays();
        if (list.isEmpty()) {
            return resourcePack;
        }
        ArrayList<ResourcePack> list2 = new ArrayList<ResourcePack>(list.size());
        for (String string : list) {
            Path path = this.path.resolve(string);
            list2.add(new DirectoryResourcePack(info, path));
        }
        return new OverlayResourcePack(resourcePack, list2);
    }
}
