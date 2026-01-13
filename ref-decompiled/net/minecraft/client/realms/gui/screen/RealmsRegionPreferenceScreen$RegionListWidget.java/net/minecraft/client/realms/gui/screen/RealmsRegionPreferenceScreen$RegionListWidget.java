/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RealmsRegionPreferenceScreen.RegionListWidget
extends AlwaysSelectedEntryListWidget<RegionEntry> {
    RealmsRegionPreferenceScreen.RegionListWidget() {
        super(RealmsRegionPreferenceScreen.this.client, RealmsRegionPreferenceScreen.this.width, RealmsRegionPreferenceScreen.this.height - 77, 40, 16);
        this.addEntry(new RegionEntry(RegionSelectionMethod.AUTOMATIC_PLAYER, null));
        this.addEntry(new RegionEntry(RegionSelectionMethod.AUTOMATIC_OWNER, null));
        RealmsRegionPreferenceScreen.this.availableRegions.keySet().stream().map(region -> new RegionEntry(RegionSelectionMethod.MANUAL, (RealmsRegion)((Object)region))).forEach(entry -> this.addEntry(entry));
    }

    @Override
    public void setSelected(@Nullable RegionEntry regionEntry) {
        super.setSelected(regionEntry);
        if (regionEntry != null) {
            RealmsRegionPreferenceScreen.this.currentRegion = regionEntry.region;
        }
        RealmsRegionPreferenceScreen.this.refreshDoneButton();
    }

    @Environment(value=EnvType.CLIENT)
    class RegionEntry
    extends AlwaysSelectedEntryListWidget.Entry<RegionEntry> {
        final RealmsSettingsTab.Region region;
        private final Text name;

        public RegionEntry(@Nullable RegionSelectionMethod selectionMethod, RealmsRegion region) {
            this(new RealmsSettingsTab.Region(selectionMethod, region));
        }

        public RegionEntry(RealmsSettingsTab.Region region) {
            this.region = region;
            this.name = region.preference() == RegionSelectionMethod.MANUAL ? (region.region() != null ? Text.translatable(region.region().translationKey) : Text.empty()) : Text.translatable(region.preference().translationKey);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.name);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawTextWithShadow(RealmsRegionPreferenceScreen.this.textRenderer, this.name, this.getContentX() + 5, this.getContentY() + 2, -1);
            if (this.region.region() != null && RealmsRegionPreferenceScreen.this.availableRegions.containsKey((Object)this.region.region())) {
                ServiceQuality serviceQuality = RealmsRegionPreferenceScreen.this.availableRegions.getOrDefault((Object)this.region.region(), ServiceQuality.UNKNOWN);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, serviceQuality.getIcon(), this.getContentRightEnd() - 18, this.getContentY() + 2, 10, 8);
            }
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            RegionListWidget.this.setSelected(this);
            if (doubled) {
                RegionListWidget.this.playDownSound(RegionListWidget.this.client.getSoundManager());
                RealmsRegionPreferenceScreen.this.onDone();
                return true;
            }
            return super.mouseClicked(click, doubled);
        }

        @Override
        public boolean keyPressed(KeyInput input) {
            if (input.isEnterOrSpace()) {
                RegionListWidget.this.playDownSound(RegionListWidget.this.client.getSoundManager());
                RealmsRegionPreferenceScreen.this.onDone();
                return true;
            }
            return super.keyPressed(input);
        }
    }
}
