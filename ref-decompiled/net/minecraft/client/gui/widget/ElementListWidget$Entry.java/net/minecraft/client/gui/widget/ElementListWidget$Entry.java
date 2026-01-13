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
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static abstract class ElementListWidget.Entry<E extends ElementListWidget.Entry<E>>
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
