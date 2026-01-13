/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static abstract class ChatSelectionScreen.SelectionListWidget.Entry
extends AlwaysSelectedEntryListWidget.Entry<ChatSelectionScreen.SelectionListWidget.Entry> {
    @Override
    public Text getNarration() {
        return ScreenTexts.EMPTY;
    }

    public boolean isSelected() {
        return false;
    }

    public boolean canSelect() {
        return false;
    }

    public boolean isHighlightedOnHover() {
        return this.canSelect();
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return this.canSelect();
    }
}
