/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackProfile;

public static interface ResourcePackProfile.PackFactory {
    public ResourcePack open(ResourcePackInfo var1);

    public ResourcePack openWithOverlays(ResourcePackInfo var1, ResourcePackProfile.Metadata var2);
}
