/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigation$Arrow
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
 *  net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget$Entry
 *  net.minecraft.client.gui.widget.EntryListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AlwaysSelectedEntryListWidget<E extends Entry<E>>
extends EntryListWidget<E> {
    private static final Text SELECTION_USAGE_TEXT = Text.translatable((String)"narration.selection.usage");

    public AlwaysSelectedEntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (this.getEntryCount() == 0) {
            return null;
        }
        if (this.isFocused() && navigation instanceof GuiNavigation.Arrow) {
            GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
            Entry entry = (Entry)this.getNeighboringEntry(arrow.direction());
            if (entry != null) {
                return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)GuiNavigationPath.of((Element)entry));
            }
            this.setFocused(null);
            this.setSelected(null);
            return null;
        }
        if (!this.isFocused()) {
            Entry entry2 = (Entry)this.getSelectedOrNull();
            if (entry2 == null) {
                entry2 = (Entry)this.getNeighboringEntry(navigation.getDirection());
            }
            if (entry2 == null) {
                return null;
            }
            return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)GuiNavigationPath.of((Element)entry2));
        }
        return null;
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        Entry entry = (Entry)this.getHoveredEntry();
        if (entry != null) {
            this.appendNarrations(builder.nextMessage(), (EntryListWidget.Entry)entry);
            entry.appendNarrations(builder);
        } else {
            Entry entry2 = (Entry)this.getSelectedOrNull();
            if (entry2 != null) {
                this.appendNarrations(builder.nextMessage(), (EntryListWidget.Entry)entry2);
                entry2.appendNarrations(builder);
            }
        }
        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, SELECTION_USAGE_TEXT);
        }
    }
}

