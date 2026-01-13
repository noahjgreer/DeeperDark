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
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.NavigationDirection;

@Environment(value=EnvType.CLIENT)
public record GuiNavigation.Tab(boolean forward) implements GuiNavigation
{
    @Override
    public NavigationDirection getDirection() {
        return this.forward ? NavigationDirection.DOWN : NavigationDirection.UP;
    }
}
