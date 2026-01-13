/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$AbstractPack
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$DisabledPack
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$EnabledPack
 *  net.minecraft.client.gui.screen.pack.ResourcePackOrganizer$Pack
 *  net.minecraft.resource.ResourcePackManager
 *  net.minecraft.resource.ResourcePackProfile
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOrganizer {
    private final ResourcePackManager resourcePackManager;
    final List<ResourcePackProfile> enabledPacks;
    final List<ResourcePackProfile> disabledPacks;
    final Function<ResourcePackProfile, Identifier> iconIdSupplier;
    final Consumer<AbstractPack> updateCallback;
    private final Consumer<ResourcePackManager> applier;

    public ResourcePackOrganizer(Consumer<AbstractPack> updateCallback, Function<ResourcePackProfile, Identifier> iconIdSupplier, ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier) {
        this.updateCallback = updateCallback;
        this.iconIdSupplier = iconIdSupplier;
        this.resourcePackManager = resourcePackManager;
        this.enabledPacks = Lists.newArrayList((Iterable)resourcePackManager.getEnabledProfiles());
        Collections.reverse(this.enabledPacks);
        this.disabledPacks = Lists.newArrayList((Iterable)resourcePackManager.getProfiles());
        this.disabledPacks.removeAll(this.enabledPacks);
        this.applier = applier;
    }

    public Stream<Pack> getDisabledPacks() {
        return this.disabledPacks.stream().map(pack -> new DisabledPack(this, pack));
    }

    public Stream<Pack> getEnabledPacks() {
        return this.enabledPacks.stream().map(pack -> new EnabledPack(this, pack));
    }

    void refreshEnabledProfiles() {
        this.resourcePackManager.setEnabledProfiles((Collection)Lists.reverse((List)this.enabledPacks).stream().map(ResourcePackProfile::getId).collect(ImmutableList.toImmutableList()));
    }

    public void apply() {
        this.refreshEnabledProfiles();
        this.applier.accept(this.resourcePackManager);
    }

    public void refresh() {
        this.resourcePackManager.scanPacks();
        this.enabledPacks.retainAll(this.resourcePackManager.getProfiles());
        this.disabledPacks.clear();
        this.disabledPacks.addAll(this.resourcePackManager.getProfiles());
        this.disabledPacks.removeAll(this.enabledPacks);
    }
}

