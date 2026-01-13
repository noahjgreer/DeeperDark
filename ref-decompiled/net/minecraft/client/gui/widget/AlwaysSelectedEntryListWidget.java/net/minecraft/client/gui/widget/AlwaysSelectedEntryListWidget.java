/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AlwaysSelectedEntryListWidget<E extends Entry<E>>
extends EntryListWidget<E> {
    private static final Text SELECTION_USAGE_TEXT = Text.translatable("narration.selection.usage");

    public AlwaysSelectedEntryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    @Override
    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (this.getEntryCount() == 0) {
            return null;
        }
        if (this.isFocused() && navigation instanceof GuiNavigation.Arrow) {
            GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
            Entry entry = (Entry)this.getNeighboringEntry(arrow.direction());
            if (entry != null) {
                return GuiNavigationPath.of(this, GuiNavigationPath.of(entry));
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
            return GuiNavigationPath.of(this, GuiNavigationPath.of(entry2));
        }
        return null;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        Entry entry = (Entry)this.getHoveredEntry();
        if (entry != null) {
            this.appendNarrations(builder.nextMessage(), entry);
            entry.appendNarrations(builder);
        } else {
            Entry entry2 = (Entry)this.getSelectedOrNull();
            if (entry2 != null) {
                this.appendNarrations(builder.nextMessage(), entry2);
                entry2.appendNarrations(builder);
            }
        }
        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, SELECTION_USAGE_TEXT);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    extends EntryListWidget.Entry<E>
    implements Narratable {
        public abstract Text getNarration();

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            return true;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
            builder.put(NarrationPart.TITLE, this.getNarration());
        }
    }
}
