/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static interface ResourcePackOrganizer.Pack {
    public Identifier getIconId();

    public ResourcePackCompatibility getCompatibility();

    public String getName();

    public Text getDisplayName();

    public Text getDescription();

    public ResourcePackSource getSource();

    default public Text getDecoratedDescription() {
        return this.getSource().decorate(this.getDescription());
    }

    public boolean isPinned();

    public boolean isAlwaysEnabled();

    public void enable();

    public void disable();

    public void moveTowardStart();

    public void moveTowardEnd();

    public boolean isEnabled();

    default public boolean canBeEnabled() {
        return !this.isEnabled();
    }

    default public boolean canBeDisabled() {
        return this.isEnabled() && !this.isAlwaysEnabled();
    }

    public boolean canMoveTowardStart();

    public boolean canMoveTowardEnd();
}
