/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.pack;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class ExperimentalWarningScreen.DetailsScreen
extends Screen {
    private static final Text TITLE = Text.translatable("selectWorld.experimental.details.title");
    final ThreePartsLayoutWidget layout;
    private @Nullable PackListWidget packListWidget;

    ExperimentalWarningScreen.DetailsScreen() {
        super(TITLE);
        this.layout = new ThreePartsLayoutWidget(this);
    }

    @Override
    protected void init() {
        this.layout.addHeader(TITLE, this.textRenderer);
        this.packListWidget = this.layout.addBody(new PackListWidget(this, this.client, ExperimentalWarningScreen.this.enabledProfiles));
        this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        if (this.packListWidget != null) {
            this.packListWidget.position(this.width, this.layout);
        }
        this.layout.refreshPositions();
    }

    @Override
    public void close() {
        this.client.setScreen(ExperimentalWarningScreen.this);
    }

    @Environment(value=EnvType.CLIENT)
    class PackListWidget
    extends AlwaysSelectedEntryListWidget<PackListWidgetEntry> {
        public PackListWidget(ExperimentalWarningScreen.DetailsScreen detailsScreen, MinecraftClient client, Collection<ResourcePackProfile> enabledProfiles) {
            super(client, detailsScreen.width, detailsScreen.layout.getContentHeight(), detailsScreen.layout.getHeaderHeight(), (client.textRenderer.fontHeight + 2) * 3);
            for (ResourcePackProfile resourcePackProfile : enabledProfiles) {
                String string = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_FEATURES, resourcePackProfile.getRequestedFeatures());
                if (string.isEmpty()) continue;
                Text text = Texts.withStyle(resourcePackProfile.getDisplayName(), Style.EMPTY.withBold(true));
                MutableText text2 = Text.translatable("selectWorld.experimental.details.entry", string);
                this.addEntry(detailsScreen.new PackListWidgetEntry(text, text2, MultilineText.create(detailsScreen.textRenderer, (Text)text2, this.getRowWidth())));
            }
        }

        @Override
        public int getRowWidth() {
            return this.width * 3 / 4;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PackListWidgetEntry
    extends AlwaysSelectedEntryListWidget.Entry<PackListWidgetEntry> {
        private final Text displayName;
        private final Text details;
        private final MultilineText multilineDetails;

        PackListWidgetEntry(Text displayName, Text details, MultilineText multilineDetails) {
            this.displayName = displayName;
            this.details = details;
            this.multilineDetails = multilineDetails;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
            context.drawTextWithShadow(((ExperimentalWarningScreen.DetailsScreen)DetailsScreen.this).client.textRenderer, this.displayName, this.getContentX(), this.getContentY(), -1);
            this.multilineDetails.draw(Alignment.LEFT, this.getContentX(), this.getContentY() + 12, ((ExperimentalWarningScreen.DetailsScreen)DetailsScreen.this).textRenderer.fontHeight, drawnTextConsumer);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.displayName, this.details));
        }
    }
}
