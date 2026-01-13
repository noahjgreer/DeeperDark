/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class ElementListWidget<E extends Entry<E>>
extends EntryListWidget<E> {
    public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    @Override
    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        if (this.getEntryCount() == 0) {
            return null;
        }
        if (navigation instanceof GuiNavigation.Arrow) {
            GuiNavigationPath guiNavigationPath;
            GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
            Entry entry = (Entry)this.getFocused();
            if (arrow.direction().getAxis() == NavigationAxis.HORIZONTAL && entry != null) {
                return GuiNavigationPath.of(this, entry.getNavigationPath(navigation));
            }
            int i = -1;
            NavigationDirection navigationDirection = arrow.direction();
            if (entry != null) {
                i = entry.children().indexOf(entry.getFocused());
            }
            if (i == -1) {
                switch (navigationDirection) {
                    case LEFT: {
                        i = Integer.MAX_VALUE;
                        navigationDirection = NavigationDirection.DOWN;
                        break;
                    }
                    case RIGHT: {
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
                if ((entry2 = this.getNeighboringEntry(navigationDirection, element -> !element.children().isEmpty(), entry2)) != null) continue;
                return null;
            } while ((guiNavigationPath = entry2.getNavigationPath(arrow, i)) == null);
            return GuiNavigationPath.of(this, guiNavigationPath);
        }
        return super.getNavigationPath(navigation);
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        if (this.getFocused() == focused) {
            return;
        }
        super.setFocused(focused);
        if (focused == null) {
            this.setSelected(null);
        }
    }

    @Override
    public Selectable.SelectionType getType() {
        if (this.isFocused()) {
            return Selectable.SelectionType.FOCUSED;
        }
        return super.getType();
    }

    @Override
    protected boolean isEntrySelectionAllowed() {
        return false;
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        Object object = this.getHoveredEntry();
        if (object instanceof Entry) {
            Entry entry = (Entry)object;
            entry.appendNarrations(builder.nextMessage());
            this.appendNarrations(builder, entry);
        } else {
            object = this.getFocused();
            if (object instanceof Entry) {
                Entry entry2 = (Entry)object;
                entry2.appendNarrations(builder.nextMessage());
                this.appendNarrations(builder, entry2);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry<E extends Entry<E>>
    extends EntryListWidget.Entry<E>
    implements ParentElement {
        private @Nullable Element focused;
        private @Nullable Selectable focusedSelectable;
        private boolean dragging;

        @Override
        public boolean isDragging() {
            return this.dragging;
        }

        @Override
        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            return ParentElement.super.mouseClicked(click, doubled);
        }

        @Override
        public void setFocused(@Nullable Element focused) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }
            if (focused != null) {
                focused.setFocused(true);
            }
            this.focused = focused;
        }

        @Override
        public @Nullable Element getFocused() {
            return this.focused;
        }

        public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation, int index) {
            if (this.children().isEmpty()) {
                return null;
            }
            GuiNavigationPath guiNavigationPath = this.children().get(Math.min(index, this.children().size() - 1)).getNavigationPath(navigation);
            return GuiNavigationPath.of(this, guiNavigationPath);
        }

        @Override
        public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
            if (navigation instanceof GuiNavigation.Arrow) {
                int j;
                int i;
                GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
                switch (arrow.direction()) {
                    default: {
                        throw new MatchException(null, null);
                    }
                    case UP: 
                    case DOWN: {
                        int n = 0;
                        break;
                    }
                    case LEFT: {
                        int n = -1;
                        break;
                    }
                    case RIGHT: {
                        int n = i = 1;
                    }
                }
                if (i == 0) {
                    return null;
                }
                for (int k = j = MathHelper.clamp(i + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1); k >= 0 && k < this.children().size(); k += i) {
                    Element element = this.children().get(k);
                    GuiNavigationPath guiNavigationPath = element.getNavigationPath(navigation);
                    if (guiNavigationPath == null) continue;
                    return GuiNavigationPath.of(this, guiNavigationPath);
                }
            }
            return ParentElement.super.getNavigationPath(navigation);
        }

        public abstract List<? extends Selectable> selectableChildren();

        void appendNarrations(NarrationMessageBuilder builder) {
            List<Selectable> list = this.selectableChildren();
            Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, this.focusedSelectable);
            if (selectedElementNarrationData != null) {
                if (selectedElementNarrationData.selectType().isFocused()) {
                    this.focusedSelectable = selectedElementNarrationData.selectable();
                }
                if (list.size() > 1) {
                    builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.object_list", selectedElementNarrationData.index() + 1, list.size()));
                }
                selectedElementNarrationData.selectable().appendNarrations(builder.nextMessage());
            }
        }
    }
}
