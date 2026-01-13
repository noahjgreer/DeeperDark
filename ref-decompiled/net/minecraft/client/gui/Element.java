/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.navigation.Navigable
 *  net.minecraft.client.gui.navigation.NavigationDirection
 *  net.minecraft.client.input.CharInput
 *  net.minecraft.client.input.KeyInput
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface Element
extends Navigable {
    default public void mouseMoved(double mouseX, double mouseY) {
    }

    default public boolean mouseClicked(Click click, boolean doubled) {
        return false;
    }

    default public boolean mouseReleased(Click click) {
        return false;
    }

    default public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        return false;
    }

    default public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    default public boolean keyPressed(KeyInput input) {
        return false;
    }

    default public boolean keyReleased(KeyInput input) {
        return false;
    }

    default public boolean charTyped(CharInput input) {
        return false;
    }

    default public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }

    default public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    public void setFocused(boolean var1);

    public boolean isFocused();

    default public boolean isClickable() {
        return true;
    }

    default public @Nullable GuiNavigationPath getFocusedPath() {
        if (this.isFocused()) {
            return GuiNavigationPath.of((Element)this);
        }
        return null;
    }

    default public ScreenRect getNavigationFocus() {
        return ScreenRect.empty();
    }

    default public ScreenRect getBorder(NavigationDirection direction) {
        return this.getNavigationFocus().getBorder(direction);
    }
}

