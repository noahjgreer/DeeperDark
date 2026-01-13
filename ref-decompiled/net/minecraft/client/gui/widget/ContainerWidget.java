/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.widget.ContainerWidget
 *  net.minecraft.client.gui.widget.ScrollableWidget
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class ContainerWidget
extends ScrollableWidget
implements ParentElement {
    private @Nullable Element focusedElement;
    private boolean dragging;

    public ContainerWidget(int i, int j, int k, int l, Text text) {
        super(i, j, k, l, text);
    }

    public final boolean isDragging() {
        return this.dragging;
    }

    public final void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public @Nullable Element getFocused() {
        return this.focusedElement;
    }

    public void setFocused(@Nullable Element focused) {
        if (this.focusedElement != null) {
            this.focusedElement.setFocused(false);
        }
        if (focused != null) {
            focused.setFocused(true);
        }
        this.focusedElement = focused;
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return super.getNavigationPath(navigation);
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        boolean bl = this.checkScrollbarDragged(click);
        return super.mouseClicked(click, doubled) || bl;
    }

    public boolean mouseReleased(Click click) {
        super.mouseReleased(click);
        return super.mouseReleased(click);
    }

    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        super.mouseDragged(click, offsetX, offsetY);
        return super.mouseDragged(click, offsetX, offsetY);
    }

    public boolean isFocused() {
        return super.isFocused();
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }
}

