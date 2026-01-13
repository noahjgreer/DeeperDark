/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.hud.debug.DebugProfileType
 *  net.minecraft.client.gui.screen.DebugOptionsScreen
 *  net.minecraft.client.gui.screen.DebugOptionsScreen$OptionsListWidget
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.hud.debug.DebugProfileType;
import net.minecraft.client.gui.screen.DebugOptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DebugOptionsScreen
extends Screen {
    private static final Text TITLE = Text.translatable((String)"debug.options.title");
    private static final Text WARNING_TEXT = Text.translatable((String)"debug.options.warning").withColor(-2142128);
    static final Text ALWAYS_ON_TEXT = Text.translatable((String)"debug.entry.always");
    static final Text IN_F3_TEXT = Text.translatable((String)"debug.entry.overlay");
    static final Text NEVER_TEXT = ScreenTexts.OFF;
    static final Text NOT_ALLOWED_TEXT = Text.translatable((String)"debug.options.notAllowed.tooltip");
    private static final Text SEARCH_TEXT = Text.translatable((String)"debug.options.search").fillStyle(TextFieldWidget.SEARCH_STYLE);
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this, 61, 33);
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable OptionsListWidget optionsListWidget;
    private TextFieldWidget searchStringWidget;
    final List<ButtonWidget> profileButtons = new ArrayList();

    public DebugOptionsScreen() {
        super(TITLE);
    }

    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        this.optionsListWidget = new OptionsListWidget(this);
        int i = this.optionsListWidget.getRowWidth();
        DirectionalLayoutWidget directionalLayoutWidget2 = DirectionalLayoutWidget.horizontal().spacing(8);
        directionalLayoutWidget2.add((Widget)new EmptyWidget(i / 3, 1));
        directionalLayoutWidget2.add((Widget)new TextWidget(TITLE, this.textRenderer), directionalLayoutWidget2.copyPositioner().alignVerticalCenter());
        this.searchStringWidget = new TextFieldWidget(this.textRenderer, 0, 0, i / 3, 20, this.searchStringWidget, SEARCH_TEXT);
        this.searchStringWidget.setChangedListener(searchString -> this.optionsListWidget.fillEntries(searchString));
        this.searchStringWidget.setPlaceholder(SEARCH_TEXT);
        directionalLayoutWidget2.add((Widget)this.searchStringWidget);
        directionalLayoutWidget.add((Widget)directionalLayoutWidget2, Positioner::alignHorizontalCenter);
        directionalLayoutWidget.add((Widget)new MultilineTextWidget(WARNING_TEXT, this.textRenderer).setMaxWidth(i).setCentered(true), Positioner::alignHorizontalCenter);
        this.layout.addBody((Widget)this.optionsListWidget);
        DirectionalLayoutWidget directionalLayoutWidget3 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        this.addProfile(DebugProfileType.DEFAULT, directionalLayoutWidget3);
        this.addProfile(DebugProfileType.PERFORMANCE, directionalLayoutWidget3);
        directionalLayoutWidget3.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).width(60).build());
        this.layout.forEachChild(widget -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(widget);
        });
        this.refreshWidgetPositions();
    }

    public void applyBlur(DrawContext context) {
        this.client.inGameHud.renderDebugHud(context);
        super.applyBlur(context);
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.searchStringWidget);
    }

    private void addProfile(DebugProfileType profileType, DirectionalLayoutWidget widget) {
        ButtonWidget buttonWidget = ButtonWidget.builder((Text)Text.translatable((String)profileType.getTranslationKey()), button -> {
            this.client.debugHudEntryList.setProfileType(profileType);
            this.client.debugHudEntryList.saveProfileFile();
            this.optionsListWidget.init();
            for (ButtonWidget buttonWidget : this.profileButtons) {
                buttonWidget.active = true;
            }
            button.active = false;
        }).width(120).build();
        buttonWidget.active = !this.client.debugHudEntryList.profileTypeMatches(profileType);
        this.profileButtons.add(buttonWidget);
        widget.add((Widget)buttonWidget);
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.optionsListWidget != null) {
            this.optionsListWidget.position(this.width, this.layout);
        }
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable OptionsListWidget getOptionsListWidget() {
        return this.optionsListWidget;
    }

    static /* synthetic */ MinecraftClient method_72806(DebugOptionsScreen debugOptionsScreen) {
        return debugOptionsScreen.client;
    }

    static /* synthetic */ MinecraftClient method_72809(DebugOptionsScreen debugOptionsScreen) {
        return debugOptionsScreen.client;
    }

    static /* synthetic */ MinecraftClient method_72810(DebugOptionsScreen debugOptionsScreen) {
        return debugOptionsScreen.client;
    }

    static /* synthetic */ MinecraftClient method_72811(DebugOptionsScreen debugOptionsScreen) {
        return debugOptionsScreen.client;
    }

    static /* synthetic */ MinecraftClient method_72812(DebugOptionsScreen debugOptionsScreen) {
        return debugOptionsScreen.client;
    }

    static /* synthetic */ MinecraftClient method_72813(DebugOptionsScreen debugOptionsScreen) {
        return debugOptionsScreen.client;
    }
}

