/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

@Environment(value=EnvType.CLIENT)
class ExperimentalWarningScreen.DetailsScreen.PackListWidget
extends AlwaysSelectedEntryListWidget<ExperimentalWarningScreen.DetailsScreen.PackListWidgetEntry> {
    public ExperimentalWarningScreen.DetailsScreen.PackListWidget(ExperimentalWarningScreen.DetailsScreen detailsScreen, MinecraftClient client, Collection<ResourcePackProfile> enabledProfiles) {
        super(client, detailsScreen.width, detailsScreen.layout.getContentHeight(), detailsScreen.layout.getHeaderHeight(), (client.textRenderer.fontHeight + 2) * 3);
        for (ResourcePackProfile resourcePackProfile : enabledProfiles) {
            String string = FeatureFlags.printMissingFlags(FeatureFlags.VANILLA_FEATURES, resourcePackProfile.getRequestedFeatures());
            if (string.isEmpty()) continue;
            Text text = Texts.withStyle(resourcePackProfile.getDisplayName(), Style.EMPTY.withBold(true));
            MutableText text2 = Text.translatable("selectWorld.experimental.details.entry", string);
            this.addEntry(new ExperimentalWarningScreen.DetailsScreen.PackListWidgetEntry(detailsScreen, text, text2, MultilineText.create(detailsScreen.textRenderer, (Text)text2, this.getRowWidth())));
        }
    }

    @Override
    public int getRowWidth() {
        return this.width * 3 / 4;
    }
}
