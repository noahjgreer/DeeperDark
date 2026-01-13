/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.screen.DebugOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class DebugOptionsScreen.Entry
extends DebugOptionsScreen.AbstractEntry {
    private static final int field_63529 = 60;
    private final Identifier label;
    protected final List<ClickableWidget> widgets = Lists.newArrayList();
    private final CyclingButtonWidget<Boolean> alwaysOnButton;
    private final CyclingButtonWidget<Boolean> inF3Button;
    private final CyclingButtonWidget<Boolean> neverButton;
    private final String renderedLabel;
    private final boolean canShow;

    public DebugOptionsScreen.Entry(Identifier label) {
        this.label = label;
        DebugHudEntry debugHudEntry = DebugHudEntries.get(label);
        this.canShow = debugHudEntry != null && debugHudEntry.canShow(DebugOptionsScreen.this.client.hasReducedDebugInfo());
        String string = label.getPath();
        this.renderedLabel = this.canShow ? string : String.valueOf(Formatting.ITALIC) + string;
        this.alwaysOnButton = CyclingButtonWidget.onOffBuilder(ALWAYS_ON_TEXT.copy().withColor(-2142128), ALWAYS_ON_TEXT.copy().withColor(-4539718), false).omitKeyText().narration(this::getNarrationMessage).build(10, 5, 60, 16, Text.literal(string), (button, value) -> this.setEntryVisibility(label, DebugHudEntryVisibility.ALWAYS_ON));
        this.inF3Button = CyclingButtonWidget.onOffBuilder(IN_F3_TEXT.copy().withColor(-171), IN_F3_TEXT.copy().withColor(-4539718), false).omitKeyText().narration(this::getNarrationMessage).build(10, 5, 60, 16, Text.literal(string), (button, value) -> this.setEntryVisibility(label, DebugHudEntryVisibility.IN_OVERLAY));
        this.neverButton = CyclingButtonWidget.onOffBuilder(NEVER_TEXT.copy().withColor(-1), NEVER_TEXT.copy().withColor(-4539718), false).omitKeyText().narration(this::getNarrationMessage).build(10, 5, 60, 16, Text.literal(string), (button, value) -> this.setEntryVisibility(label, DebugHudEntryVisibility.NEVER));
        this.widgets.add(this.neverButton);
        this.widgets.add(this.inF3Button);
        this.widgets.add(this.alwaysOnButton);
        this.init();
    }

    private MutableText getNarrationMessage(CyclingButtonWidget<Boolean> widget) {
        DebugHudEntryVisibility debugHudEntryVisibility = ((DebugOptionsScreen)DebugOptionsScreen.this).client.debugHudEntryList.getVisibility(this.label);
        MutableText mutableText = Text.translatable("debug.entry.currently." + debugHudEntryVisibility.asString(), this.renderedLabel);
        return ScreenTexts.composeGenericOptionText(mutableText, widget.getMessage());
    }

    private void setEntryVisibility(Identifier label, DebugHudEntryVisibility visibility) {
        ((DebugOptionsScreen)DebugOptionsScreen.this).client.debugHudEntryList.setEntryVisibility(label, visibility);
        for (ButtonWidget buttonWidget : DebugOptionsScreen.this.profileButtons) {
            buttonWidget.active = true;
        }
        this.init();
    }

    @Override
    public List<? extends Element> children() {
        return this.widgets;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.widgets;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = this.getContentX();
        int j = this.getContentY();
        context.drawTextWithShadow(((DebugOptionsScreen)DebugOptionsScreen.this).client.textRenderer, this.renderedLabel, i, j + 5, this.canShow ? -1 : -8355712);
        int k = i + this.getContentWidth() - this.neverButton.getWidth() - this.inF3Button.getWidth() - this.alwaysOnButton.getWidth();
        if (!this.canShow && hovered && mouseX < k) {
            context.drawTooltip(NOT_ALLOWED_TEXT, mouseX, mouseY);
        }
        this.neverButton.setX(k);
        this.inF3Button.setX(this.neverButton.getX() + this.neverButton.getWidth());
        this.alwaysOnButton.setX(this.inF3Button.getX() + this.inF3Button.getWidth());
        this.alwaysOnButton.setY(j);
        this.inF3Button.setY(j);
        this.neverButton.setY(j);
        this.alwaysOnButton.render(context, mouseX, mouseY, deltaTicks);
        this.inF3Button.render(context, mouseX, mouseY, deltaTicks);
        this.neverButton.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void init() {
        DebugHudEntryVisibility debugHudEntryVisibility = ((DebugOptionsScreen)DebugOptionsScreen.this).client.debugHudEntryList.getVisibility(this.label);
        this.alwaysOnButton.setValue(debugHudEntryVisibility == DebugHudEntryVisibility.ALWAYS_ON);
        this.inF3Button.setValue(debugHudEntryVisibility == DebugHudEntryVisibility.IN_OVERLAY);
        this.neverButton.setValue(debugHudEntryVisibility == DebugHudEntryVisibility.NEVER);
        this.alwaysOnButton.active = this.alwaysOnButton.getValue() == false;
        this.inF3Button.active = this.inF3Button.getValue() == false;
        this.neverButton.active = this.neverButton.getValue() == false;
    }
}
