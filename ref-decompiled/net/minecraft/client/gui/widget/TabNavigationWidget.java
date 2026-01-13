/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.AbstractParentElement
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.Selectable
 *  net.minecraft.client.gui.Selectable$SelectionType
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigation$Tab
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.tab.Tab
 *  net.minecraft.client.gui.tab.TabManager
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TabButtonWidget
 *  net.minecraft.client.gui.widget.TabNavigationWidget
 *  net.minecraft.client.gui.widget.TabNavigationWidget$Builder
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TabButtonWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TabNavigationWidget
extends AbstractParentElement
implements Drawable,
Selectable {
    private static final int field_42489 = -1;
    private static final int field_43076 = 400;
    private static final int field_43077 = 24;
    private static final int field_43078 = 14;
    private static final Text USAGE_NARRATION_TEXT = Text.translatable((String)"narration.tab_navigation.usage");
    private final DirectionalLayoutWidget grid = DirectionalLayoutWidget.horizontal();
    private int tabNavWidth;
    private final TabManager tabManager;
    private final ImmutableList<Tab> tabs;
    private final ImmutableList<TabButtonWidget> tabButtons;

    TabNavigationWidget(int x, TabManager tabManager, Iterable<Tab> tabs) {
        this.tabNavWidth = x;
        this.tabManager = tabManager;
        this.tabs = ImmutableList.copyOf(tabs);
        this.grid.getMainPositioner().alignHorizontalCenter();
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Tab tab : tabs) {
            builder.add((Object)((TabButtonWidget)this.grid.add((Widget)new TabButtonWidget(tabManager, tab, 0, 24))));
        }
        this.tabButtons = builder.build();
    }

    public static Builder builder(TabManager tabManager, int width) {
        return new Builder(tabManager, width);
    }

    public void setWidth(int width) {
        this.tabNavWidth = width;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= (double)this.grid.getX() && mouseY >= (double)this.grid.getY() && mouseX < (double)(this.grid.getX() + this.grid.getWidth()) && mouseY < (double)(this.grid.getY() + this.grid.getHeight());
    }

    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (this.getFocused() != null) {
            this.setFocused(null);
        }
    }

    public void setFocused(@Nullable Element focused) {
        TabButtonWidget tabButtonWidget;
        super.setFocused(focused);
        if (focused instanceof TabButtonWidget && (tabButtonWidget = (TabButtonWidget)focused).isInteractable()) {
            this.tabManager.setCurrentTab(tabButtonWidget.getTab(), true);
        }
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        TabButtonWidget tabButtonWidget;
        if (!this.isFocused() && (tabButtonWidget = this.getCurrentTabButton()) != null) {
            return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)GuiNavigationPath.of((Element)tabButtonWidget));
        }
        if (navigation instanceof GuiNavigation.Tab) {
            return null;
        }
        return super.getNavigationPath(navigation);
    }

    public List<? extends Element> children() {
        return this.tabButtons;
    }

    public List<Tab> getTabs() {
        return this.tabs;
    }

    public Selectable.SelectionType getType() {
        return this.tabButtons.stream().map(ClickableWidget::getType).max(Comparator.naturalOrder()).orElse(Selectable.SelectionType.NONE);
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
        Optional<TabButtonWidget> optional = this.tabButtons.stream().filter(ClickableWidget::isHovered).findFirst().or(() -> Optional.ofNullable(this.getCurrentTabButton()));
        optional.ifPresent(button -> {
            this.appendNarrations(builder.nextMessage(), button);
            button.appendNarrations(builder);
        });
        if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, USAGE_NARRATION_TEXT);
        }
    }

    protected void appendNarrations(NarrationMessageBuilder builder, TabButtonWidget button) {
        int i;
        if (this.tabs.size() > 1 && (i = this.tabButtons.indexOf((Object)button)) != -1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable((String)"narrator.position.tab", (Object[])new Object[]{i + 1, this.tabs.size()}));
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR_TEXTURE, 0, this.grid.getY() + this.grid.getHeight() - 2, 0.0f, 0.0f, ((TabButtonWidget)this.tabButtons.get(0)).getX(), 2, 32, 2);
        int i = ((TabButtonWidget)this.tabButtons.get(this.tabButtons.size() - 1)).getRight();
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR_TEXTURE, i, this.grid.getY() + this.grid.getHeight() - 2, 0.0f, 0.0f, this.tabNavWidth, 2, 32, 2);
        for (TabButtonWidget tabButtonWidget : this.tabButtons) {
            tabButtonWidget.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    public ScreenRect getNavigationFocus() {
        return this.grid.getNavigationFocus();
    }

    public void init() {
        int i = Math.min(400, this.tabNavWidth) - 28;
        int j = MathHelper.roundUpToMultiple((int)(i / this.tabs.size()), (int)2);
        for (TabButtonWidget tabButtonWidget : this.tabButtons) {
            tabButtonWidget.setWidth(j);
        }
        this.grid.refreshPositions();
        this.grid.setX(MathHelper.roundUpToMultiple((int)((this.tabNavWidth - i) / 2), (int)2));
        this.grid.setY(0);
    }

    public void selectTab(int index, boolean clickSound) {
        if (this.isFocused()) {
            this.setFocused((Element)this.tabButtons.get(index));
        } else if (((TabButtonWidget)this.tabButtons.get(index)).isInteractable()) {
            this.tabManager.setCurrentTab((Tab)this.tabs.get(index), clickSound);
        }
    }

    public void setTabActive(int index, boolean active) {
        if (index >= 0 && index < this.tabButtons.size()) {
            ((TabButtonWidget)this.tabButtons.get((int)index)).active = active;
        }
    }

    public void setTabTooltip(int index, @Nullable Tooltip tooltip) {
        if (index >= 0 && index < this.tabButtons.size()) {
            ((TabButtonWidget)this.tabButtons.get(index)).setTooltip(tooltip);
        }
    }

    public boolean keyPressed(KeyInput input) {
        int i;
        if (input.hasCtrlOrCmd() && (i = this.getTabForKey(input)) != -1) {
            this.selectTab(MathHelper.clamp((int)i, (int)0, (int)(this.tabs.size() - 1)), true);
            return true;
        }
        return false;
    }

    private int getTabForKey(KeyInput keyInput) {
        return this.getTabForKey(this.getCurrentTabIndex(), keyInput);
    }

    private int getTabForKey(int index, KeyInput keyInput) {
        int i = keyInput.asNumber();
        if (i != -1) {
            return Math.floorMod(i - 1, 10);
        }
        if (keyInput.isTab() && index != -1) {
            int j = keyInput.hasShift() ? index - 1 : index + 1;
            int k = Math.floorMod(j, this.tabs.size());
            if (((TabButtonWidget)this.tabButtons.get((int)k)).active) {
                return k;
            }
            return this.getTabForKey(k, keyInput);
        }
        return -1;
    }

    private int getCurrentTabIndex() {
        Tab tab = this.tabManager.getCurrentTab();
        int i = this.tabs.indexOf((Object)tab);
        return i != -1 ? i : -1;
    }

    private @Nullable TabButtonWidget getCurrentTabButton() {
        int i = this.getCurrentTabIndex();
        return i != -1 ? (TabButtonWidget)this.tabButtons.get(i) : null;
    }
}

