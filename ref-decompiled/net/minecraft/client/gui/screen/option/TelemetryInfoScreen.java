/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.TelemetryEventWidget
 *  net.minecraft.client.gui.screen.option.TelemetryInfoScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CheckboxWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Urls
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import java.net.URI;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.TelemetryEventWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TelemetryInfoScreen
extends Screen {
    private static final Text TITLE_TEXT = Text.translatable((String)"telemetry_info.screen.title");
    private static final Text DESCRIPTION_TEXT = Text.translatable((String)"telemetry_info.screen.description").withColor(-4539718);
    private static final Text PRIVACY_STATEMENT_TEXT = Text.translatable((String)"telemetry_info.button.privacy_statement");
    private static final Text GIVE_FEEDBACK_TEXT = Text.translatable((String)"telemetry_info.button.give_feedback");
    private static final Text SHOW_DATA_TEXT = Text.translatable((String)"telemetry_info.button.show_data");
    private static final Text OPT_IN_DESCRIPTION_TEXT = Text.translatable((String)"telemetry_info.opt_in.description").withColor(-2039584);
    private static final int MARGIN = 8;
    private static final boolean OPTIONAL_TELEMETRY_ENABLED_BY_API = MinecraftClient.getInstance().isOptionalTelemetryEnabledByApi();
    private final Screen parent;
    private final GameOptions options;
    private final ThreePartsLayoutWidget layout;
    private @Nullable TelemetryEventWidget telemetryEventWidget;
    private @Nullable MultilineTextWidget textWidget;
    private @Nullable CheckboxWidget optInCheckbox;
    private double scroll;

    public TelemetryInfoScreen(Screen parent, GameOptions options) {
        super(TITLE_TEXT);
        Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
        this.layout = new ThreePartsLayoutWidget((Screen)this, 16 + 9 * 5 + 20, OPTIONAL_TELEMETRY_ENABLED_BY_API ? 33 + CheckboxWidget.getCheckboxSize((TextRenderer)MinecraftClient.getInstance().textRenderer) : 33);
        this.parent = parent;
        this.options = options;
    }

    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), DESCRIPTION_TEXT});
    }

    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(TITLE_TEXT, this.textRenderer));
        this.textWidget = (MultilineTextWidget)directionalLayoutWidget.add((Widget)new MultilineTextWidget(DESCRIPTION_TEXT, this.textRenderer).setCentered(true));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)PRIVACY_STATEMENT_TEXT, arg_0 -> this.openPrivacyStatementPage(arg_0)).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)GIVE_FEEDBACK_TEXT, arg_0 -> this.openFeedbackPage(arg_0)).build());
        DirectionalLayoutWidget directionalLayoutWidget3 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.vertical().spacing(4));
        directionalLayoutWidget3.getMainPositioner().alignHorizontalCenter();
        if (OPTIONAL_TELEMETRY_ENABLED_BY_API) {
            this.optInCheckbox = (CheckboxWidget)directionalLayoutWidget3.add((Widget)CheckboxWidget.builder((Text)OPT_IN_DESCRIPTION_TEXT, (TextRenderer)this.textRenderer).maxWidth(this.width - 40).option(this.options.getTelemetryOptInExtra()).callback((arg_0, arg_1) -> this.updateOptIn(arg_0, arg_1)).build());
        }
        DirectionalLayoutWidget directionalLayoutWidget4 = (DirectionalLayoutWidget)directionalLayoutWidget3.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget4.add((Widget)ButtonWidget.builder((Text)SHOW_DATA_TEXT, arg_0 -> this.openLogDirectory(arg_0)).build());
        directionalLayoutWidget4.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).build());
        DirectionalLayoutWidget directionalLayoutWidget5 = (DirectionalLayoutWidget)this.layout.addBody((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        this.telemetryEventWidget = (TelemetryEventWidget)directionalLayoutWidget5.add((Widget)new TelemetryEventWidget(0, 0, this.width - 40, this.layout.getContentHeight(), this.textRenderer));
        this.telemetryEventWidget.setScrollConsumer(scroll -> {
            this.scroll = scroll;
        });
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        if (this.telemetryEventWidget != null) {
            this.telemetryEventWidget.setScrollY(this.scroll);
            this.telemetryEventWidget.setWidth(this.width - 40);
            this.telemetryEventWidget.setHeight(this.layout.getContentHeight());
            this.telemetryEventWidget.initContents();
        }
        if (this.textWidget != null) {
            this.textWidget.setMaxWidth(this.width - 16);
        }
        if (this.optInCheckbox != null) {
            this.optInCheckbox.setMaxWidth(this.width - 40, this.textRenderer);
        }
        this.layout.refreshPositions();
    }

    protected void setInitialFocus() {
        if (this.telemetryEventWidget != null) {
            this.setInitialFocus((Element)this.telemetryEventWidget);
        }
    }

    private void updateOptIn(ClickableWidget checkbox, boolean checked) {
        if (this.telemetryEventWidget != null) {
            this.telemetryEventWidget.refresh(checked);
        }
    }

    private void openPrivacyStatementPage(ButtonWidget button) {
        ConfirmLinkScreen.open((Screen)this, (URI)Urls.PRIVACY_STATEMENT);
    }

    private void openFeedbackPage(ButtonWidget button) {
        ConfirmLinkScreen.open((Screen)this, (URI)Urls.JAVA_FEEDBACK);
    }

    private void openLogDirectory(ButtonWidget button) {
        Util.getOperatingSystem().open(this.client.getTelemetryManager().getLogManager());
    }

    public void close() {
        this.client.setScreen(this.parent);
    }
}

