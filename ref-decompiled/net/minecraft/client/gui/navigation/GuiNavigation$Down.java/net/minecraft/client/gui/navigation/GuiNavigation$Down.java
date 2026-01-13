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
public static class GuiNavigation.Down
implements GuiNavigation {
    @Override
    public NavigationDirection getDirection() {
        return NavigationDirection.DOWN;
    }
}
