/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Narratable
 *  net.minecraft.client.gui.Selectable
 *  net.minecraft.client.gui.Selectable$SelectionType
 *  net.minecraft.client.gui.navigation.Navigable
 */
package net.minecraft.client.gui;

import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.Navigable;

@Environment(value=EnvType.CLIENT)
public interface Selectable
extends Navigable,
Narratable {
    public SelectionType getType();

    default public boolean isInteractable() {
        return true;
    }

    default public Collection<? extends Selectable> getNarratedParts() {
        return List.of(this);
    }
}

