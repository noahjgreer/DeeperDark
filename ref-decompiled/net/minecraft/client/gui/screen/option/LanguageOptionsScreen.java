/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.AccessibilityOnboardingScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.FontOptionsScreen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.screen.option.LanguageOptionsScreen
 *  net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget
 *  net.minecraft.client.gui.screen.option.LanguageOptionsScreen$LanguageSelectionListWidget$LanguageEntry
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.resource.language.LanguageManager
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.FontOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LanguageOptionsScreen
extends GameOptionsScreen {
    private static final Text LANGUAGE_WARNING_TEXT = Text.translatable((String)"options.languageAccuracyWarning").withColor(-4539718);
    private static final int field_49497 = 53;
    private static final Text SEARCH_TEXT = Text.translatable((String)"gui.language.search").fillStyle(TextFieldWidget.SEARCH_STYLE);
    private static final int field_64202 = 15;
    final LanguageManager languageManager;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable LanguageSelectionListWidget languageSelectionList;
    private @Nullable TextFieldWidget searchBox;

    public LanguageOptionsScreen(Screen parent, GameOptions options, LanguageManager languageManager) {
        super(parent, options, (Text)Text.translatable((String)"options.language.title"));
        this.languageManager = languageManager;
        this.layout.setFooterHeight(53);
    }

    protected void initHeader() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.title, this.textRenderer));
        this.searchBox = (TextFieldWidget)directionalLayoutWidget.add((Widget)new TextFieldWidget(this.textRenderer, 0, 0, 200, 15, (Text)Text.empty()));
        this.searchBox.setPlaceholder(SEARCH_TEXT);
        this.searchBox.setChangedListener(search -> {
            if (this.languageSelectionList != null) {
                this.languageSelectionList.setSearch(search);
            }
        });
        Objects.requireNonNull(this.textRenderer);
        this.layout.setHeaderHeight((int)(12.0 + 9.0 + 15.0));
    }

    protected void setInitialFocus() {
        if (this.searchBox != null) {
            this.setInitialFocus((Element)this.searchBox);
        } else {
            super.setInitialFocus();
        }
    }

    protected void initBody() {
        this.languageSelectionList = (LanguageSelectionListWidget)this.layout.addBody((Widget)new LanguageSelectionListWidget(this, this.client));
    }

    protected void addOptions() {
    }

    protected void initFooter() {
        DirectionalLayoutWidget directionalLayoutWidget = ((DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.vertical())).spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(LANGUAGE_WARNING_TEXT, this.textRenderer));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"options.font"), button -> this.client.setScreen((Screen)new FontOptionsScreen((Screen)this, this.gameOptions))).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.onDone()).build());
    }

    protected void refreshWidgetPositions() {
        super.refreshWidgetPositions();
        if (this.languageSelectionList != null) {
            this.languageSelectionList.position(this.width, this.layout);
        }
    }

    void onDone() {
        EntryListWidget.Entry entry;
        if (this.languageSelectionList != null && (entry = this.languageSelectionList.getSelectedOrNull()) instanceof LanguageSelectionListWidget.LanguageEntry) {
            LanguageSelectionListWidget.LanguageEntry languageEntry = (LanguageSelectionListWidget.LanguageEntry)entry;
            if (!languageEntry.languageCode.equals(this.languageManager.getLanguage())) {
                this.languageManager.setLanguage(languageEntry.languageCode);
                this.gameOptions.language = languageEntry.languageCode;
                this.client.reloadResources();
            }
        }
        this.client.setScreen(this.parent);
    }

    protected boolean allowRotatingPanorama() {
        return !(this.parent instanceof AccessibilityOnboardingScreen);
    }

    static /* synthetic */ TextRenderer method_60328(LanguageOptionsScreen languageOptionsScreen) {
        return languageOptionsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_61043(LanguageOptionsScreen languageOptionsScreen) {
        return languageOptionsScreen.textRenderer;
    }
}

