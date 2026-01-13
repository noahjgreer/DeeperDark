/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.screen.option.TelemetryEventWidget
 *  net.minecraft.client.gui.screen.option.TelemetryEventWidget$Contents
 *  net.minecraft.client.gui.screen.option.TelemetryEventWidget$ContentsBuilder
 *  net.minecraft.client.gui.widget.ScrollableTextFieldWidget
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  net.minecraft.client.session.telemetry.TelemetryEventType
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.DoubleConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.option.TelemetryEventWidget;
import net.minecraft.client.gui.widget.ScrollableTextFieldWidget;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TelemetryEventWidget
extends ScrollableTextFieldWidget {
    private static final int MARGIN_X = 32;
    private static final String REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
    private static final String OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
    private static final String DISABLED_TRANSLATION_KEY = "telemetry.event.optional.disabled";
    private static final Text PROPERTY_TITLE_TEXT = Text.translatable((String)"telemetry_info.property_title").formatted(Formatting.UNDERLINE);
    private final TextRenderer textRenderer;
    private Contents contents;
    private @Nullable DoubleConsumer scrollConsumer;

    public TelemetryEventWidget(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, (Text)Text.empty());
        this.textRenderer = textRenderer;
        this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
    }

    public void refresh(boolean optionalTelemetryEnabled) {
        this.contents = this.collectContents(optionalTelemetryEnabled);
        this.refreshScroll();
    }

    public void initContents() {
        this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
        this.refreshScroll();
    }

    private Contents collectContents(boolean optionalTelemetryEnabled) {
        ContentsBuilder contentsBuilder = new ContentsBuilder(this.getGridWidth());
        ArrayList<TelemetryEventType> list = new ArrayList<TelemetryEventType>(TelemetryEventType.getTypes());
        list.sort(Comparator.comparing(TelemetryEventType::isOptional));
        for (int i = 0; i < list.size(); ++i) {
            TelemetryEventType telemetryEventType = (TelemetryEventType)list.get(i);
            boolean bl = telemetryEventType.isOptional() && !optionalTelemetryEnabled;
            this.appendEventInfo(contentsBuilder, telemetryEventType, bl);
            if (i >= list.size() - 1) continue;
            Objects.requireNonNull(this.textRenderer);
            contentsBuilder.appendSpace(9);
        }
        return contentsBuilder.build();
    }

    public void setScrollConsumer(@Nullable DoubleConsumer scrollConsumer) {
        this.scrollConsumer = scrollConsumer;
    }

    public void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        if (this.scrollConsumer != null) {
            this.scrollConsumer.accept(this.getScrollY());
        }
    }

    protected int getContentsHeight() {
        return this.contents.grid().getHeight();
    }

    protected double getDeltaYPerScroll() {
        Objects.requireNonNull(this.textRenderer);
        return 9.0;
    }

    protected void renderContents(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = this.getTextY();
        int j = this.getTextX();
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)j, (float)i);
        this.contents.grid().forEachChild(widget -> widget.render(context, mouseX, mouseY, deltaTicks));
        context.getMatrices().popMatrix();
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.contents.narration());
    }

    private Text formatTitleText(Text title, boolean disabled) {
        if (disabled) {
            return title.copy().formatted(Formatting.GRAY);
        }
        return title;
    }

    private void appendEventInfo(ContentsBuilder builder, TelemetryEventType eventType, boolean disabled) {
        String string = eventType.isOptional() ? (disabled ? DISABLED_TRANSLATION_KEY : OPTIONAL_TRANSLATION_KEY) : REQUIRED_TRANSLATION_KEY;
        builder.appendText(this.textRenderer, this.formatTitleText((Text)Text.translatable((String)string, (Object[])new Object[]{eventType.getTitle()}), disabled));
        builder.appendText(this.textRenderer, (Text)eventType.getDescription().formatted(Formatting.GRAY));
        Objects.requireNonNull(this.textRenderer);
        builder.appendSpace(9 / 2);
        builder.appendTitle(this.textRenderer, this.formatTitleText(PROPERTY_TITLE_TEXT, disabled), 2);
        this.appendProperties(eventType, builder, disabled);
    }

    private void appendProperties(TelemetryEventType eventType, ContentsBuilder builder, boolean disabled) {
        for (TelemetryEventProperty telemetryEventProperty : eventType.getProperties()) {
            builder.appendTitle(this.textRenderer, this.formatTitleText((Text)telemetryEventProperty.getTitle(), disabled));
        }
    }

    private int getGridWidth() {
        return this.width - this.getPadding();
    }
}

