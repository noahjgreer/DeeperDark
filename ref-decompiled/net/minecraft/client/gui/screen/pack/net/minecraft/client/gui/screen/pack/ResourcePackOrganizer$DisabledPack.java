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
class ResourcePackOrganizer.DisabledPack
extends ResourcePackOrganizer.AbstractPack {
    public ResourcePackOrganizer.DisabledPack(ResourcePackProfile resourcePackProfile) {
        super(ResourcePackOrganizer.this, resourcePackProfile);
    }

    @Override
    protected List<ResourcePackProfile> getCurrentList() {
        return ResourcePackOrganizer.this.disabledPacks;
    }

    @Override
    protected List<ResourcePackProfile> getOppositeList() {
        return ResourcePackOrganizer.this.enabledPacks;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void enable() {
        this.toggle();
    }

    @Override
    public void disable() {
    }
}
