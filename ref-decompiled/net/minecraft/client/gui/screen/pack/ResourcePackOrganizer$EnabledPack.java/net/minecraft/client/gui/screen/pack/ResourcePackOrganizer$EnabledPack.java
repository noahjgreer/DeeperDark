/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.ResourcePackProfile;

@Environment(value=EnvType.CLIENT)
class ResourcePackOrganizer.EnabledPack
extends ResourcePackOrganizer.AbstractPack {
    public ResourcePackOrganizer.EnabledPack(ResourcePackProfile resourcePackProfile) {
        super(ResourcePackOrganizer.this, resourcePackProfile);
    }

    @Override
    protected List<ResourcePackProfile> getCurrentList() {
        return ResourcePackOrganizer.this.enabledPacks;
    }

    @Override
    protected List<ResourcePackProfile> getOppositeList() {
        return ResourcePackOrganizer.this.disabledPacks;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
        this.toggle();
    }
}
