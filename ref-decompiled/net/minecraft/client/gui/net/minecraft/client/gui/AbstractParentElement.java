/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractParentElement
implements ParentElement {
    private @Nullable Element focused;
    private boolean dragging;

    @Override
    public final boolean isDragging() {
        return this.dragging;
    }

    @Override
    public final void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    public @Nullable Element getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        if (this.focused == focused) {
            return;
        }
        if (this.focused != null) {
            this.focused.setFocused(false);
        }
        if (focused != null) {
            focused.setFocused(true);
        }
        this.focused = focused;
    }
}
