/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackProfile;

static class VanillaResourcePackProvider.1
implements ResourcePackProfile.PackFactory {
    final /* synthetic */ ResourcePack field_45053;

    VanillaResourcePackProvider.1(ResourcePack resourcePack) {
        this.field_45053 = resourcePack;
    }

    @Override
    public ResourcePack open(ResourcePackInfo info) {
        return this.field_45053;
    }

    @Override
    public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
        return this.field_45053;
    }
}
