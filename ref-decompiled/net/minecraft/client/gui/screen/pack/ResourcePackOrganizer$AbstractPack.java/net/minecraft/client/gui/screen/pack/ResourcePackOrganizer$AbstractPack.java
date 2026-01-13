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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class ResourcePackOrganizer.AbstractPack
implements ResourcePackOrganizer.Pack {
    private final ResourcePackProfile profile;

    public ResourcePackOrganizer.AbstractPack(ResourcePackProfile profile) {
        this.profile = profile;
    }

    protected abstract List<ResourcePackProfile> getCurrentList();

    protected abstract List<ResourcePackProfile> getOppositeList();

    @Override
    public Identifier getIconId() {
        return ResourcePackOrganizer.this.iconIdSupplier.apply(this.profile);
    }

    @Override
    public ResourcePackCompatibility getCompatibility() {
        return this.profile.getCompatibility();
    }

    @Override
    public String getName() {
        return this.profile.getId();
    }

    @Override
    public Text getDisplayName() {
        return this.profile.getDisplayName();
    }

    @Override
    public Text getDescription() {
        return this.profile.getDescription();
    }

    @Override
    public ResourcePackSource getSource() {
        return this.profile.getSource();
    }

    @Override
    public boolean isPinned() {
        return this.profile.isPinned();
    }

    @Override
    public boolean isAlwaysEnabled() {
        return this.profile.isRequired();
    }

    protected void toggle() {
        this.getCurrentList().remove(this.profile);
        this.profile.getInitialPosition().insert(this.getOppositeList(), this.profile, ResourcePackProfile::getPosition, true);
        ResourcePackOrganizer.this.updateCallback.accept(this);
        ResourcePackOrganizer.this.refreshEnabledProfiles();
        this.toggleHighContrastOption();
    }

    private void toggleHighContrastOption() {
        if (this.profile.getId().equals("high_contrast")) {
            SimpleOption<Boolean> simpleOption;
            simpleOption.setValue((simpleOption = MinecraftClient.getInstance().options.getHighContrast()).getValue() == false);
        }
    }

    protected void move(int offset) {
        List<ResourcePackProfile> list = this.getCurrentList();
        int i = list.indexOf(this.profile);
        list.remove(i);
        list.add(i + offset, this.profile);
        ResourcePackOrganizer.this.updateCallback.accept(this);
    }

    @Override
    public boolean canMoveTowardStart() {
        List<ResourcePackProfile> list = this.getCurrentList();
        int i = list.indexOf(this.profile);
        return i > 0 && !list.get(i - 1).isPinned();
    }

    @Override
    public void moveTowardStart() {
        this.move(-1);
    }

    @Override
    public boolean canMoveTowardEnd() {
        List<ResourcePackProfile> list = this.getCurrentList();
        int i = list.indexOf(this.profile);
        return i >= 0 && i < list.size() - 1 && !list.get(i + 1).isPinned();
    }

    @Override
    public void moveTowardEnd() {
        this.move(1);
    }
}
