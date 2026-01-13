/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.navigation.GuiNavigationPath;

@Environment(value=EnvType.CLIENT)
public record GuiNavigationPath.Leaf(Element component) implements GuiNavigationPath
{
    @Override
    public void setFocused(boolean focused) {
        this.component.setFocused(focused);
    }
}
