/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.ServiceQuality
 *  net.minecraft.client.realms.dto.RealmsRegion
 *  net.minecraft.client.realms.dto.RegionSelectionMethod
 *  net.minecraft.client.realms.gui.screen.RealmsRegionPreferenceScreen
 *  net.minecraft.client.realms.gui.screen.RealmsRegionPreferenceScreen$RegionListWidget
 *  net.minecraft.client.realms.gui.screen.RealmsRegionPreferenceScreen$RegionListWidget$RegionEntry
 *  net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab$Region
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.gui.screen.RealmsRegionPreferenceScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsRegionPreferenceScreen
extends Screen {
    private static final Text TITLE_TEXT = Text.translatable((String)"mco.configure.world.region_preference.title");
    private static final int field_60254 = 8;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private final Screen parent;
    private final BiConsumer<RegionSelectionMethod, RealmsRegion> onRegionChanged;
    final Map<RealmsRegion, ServiceQuality> availableRegions;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable RegionListWidget regionList;
    RealmsSettingsTab.Region currentRegion;
    private @Nullable ButtonWidget doneButton;

    public RealmsRegionPreferenceScreen(Screen parent, BiConsumer<RegionSelectionMethod, RealmsRegion> onRegionChanged, Map<RealmsRegion, ServiceQuality> availableRegions, RealmsSettingsTab.Region textSupplier) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.onRegionChanged = onRegionChanged;
        this.availableRegions = availableRegions;
        this.currentRegion = textSupplier;
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.getTitle(), this.textRenderer));
        this.regionList = (RegionListWidget)this.layout.addBody((Widget)new RegionListWidget(this));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        this.doneButton = (ButtonWidget)directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.onDone()).build());
        directionalLayoutWidget2.add((Widget)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).build());
        this.regionList.setSelected((RegionListWidget.RegionEntry)this.regionList.children().stream().filter(region -> Objects.equals(region.region, this.currentRegion)).findFirst().orElse(null));
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        if (this.regionList != null) {
            this.regionList.position(this.width, this.layout);
        }
    }

    void onDone() {
        if (this.currentRegion.region() != null) {
            this.onRegionChanged.accept(this.currentRegion.preference(), this.currentRegion.region());
        }
        this.close();
    }

    void refreshDoneButton() {
        if (this.doneButton != null && this.regionList != null) {
            this.doneButton.active = this.regionList.getSelectedOrNull() != null;
        }
    }

    static /* synthetic */ MinecraftClient method_71222(RealmsRegionPreferenceScreen realmsRegionPreferenceScreen) {
        return realmsRegionPreferenceScreen.client;
    }

    static /* synthetic */ TextRenderer method_71225(RealmsRegionPreferenceScreen realmsRegionPreferenceScreen) {
        return realmsRegionPreferenceScreen.textRenderer;
    }
}

