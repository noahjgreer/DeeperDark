/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.Selectable$SelectionType
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigation$Arrow
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.navigation.NavigationAxis
 *  net.minecraft.client.gui.navigation.NavigationDirection
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.widget.ElementListWidget
 *  net.minecraft.client.gui.widget.ElementListWidget$1
 *  net.minecraft.client.gui.widget.ElementListWidget$Entry
 *  net.minecraft.client.gui.widget.EntryListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class ElementListWidget<E extends Entry<E>>
extends EntryListWidget<E> {
    public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (this.getEntryCount() == 0) {
            return null;
        }
        if (navigation instanceof GuiNavigation.Arrow) {
            GuiNavigationPath guiNavigationPath;
            GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
            Entry entry = (Entry)this.getFocused();
            if (arrow.direction().getAxis() == NavigationAxis.HORIZONTAL && entry != null) {
                return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)entry.getNavigationPath(navigation));
            }
            int i = -1;
            NavigationDirection navigationDirection = arrow.direction();
            if (entry != null) {
                i = entry.children().indexOf(entry.getFocused());
            }
            if (i == -1) {
                switch (1.field_41804[navigationDirection.ordinal()]) {
                    case 1: {
                        i = Integer.MAX_VALUE;
                        navigationDirection = NavigationDirection.DOWN;
                        break;
                    }
                    case 2: {
                        i = 0;
                        navigationDirection = NavigationDirection.DOWN;
                        break;
                    }
                    default: {
                        i = 0;
                    }
                }
            }
            Entry entry2 = entry;
            do {
                if ((entry2 = (Entry)this.getNeighboringEntry(navigationDirection, element -> !element.children().isEmpty(), (EntryListWidget.Entry)entry2)) != null) continue;
                return null;
            } while ((guiNavigationPath = entry2.getNavigationPath((GuiNavigation)arrow, i)) == null);
            return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)guiNavigationPath);
        }
        return super.getNavigationPath(navigation);
    }

    public void setFocused(@Nullable Element focused) {
        if (this.getFocused() == focused) {
            return;
        }
        super.setFocused(focused);
        if (focused == null) {
            this.setSelected(null);
        }
    }

    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        }
        return super.getType();
    }

    protected boolean isEntrySelectionAllowed() {
        return false;
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        EntryListWidget.Entry entry = this.getHoveredEntry();
        if (entry instanceof Entry) {
            Entry entry2 = (Entry)entry;
            entry2.appendNarrations(builder.nextMessage());
            this.appendNarrations(builder, (EntryListWidget.Entry)entry2);
        } else {
            entry = this.getFocused();
            if (entry instanceof Entry) {
                Entry entry2 = (Entry)entry;
                entry2.appendNarrations(builder.nextMessage());
                this.appendNarrations(builder, (EntryListWidget.Entry)entry2);
            }
        }
    }
}

