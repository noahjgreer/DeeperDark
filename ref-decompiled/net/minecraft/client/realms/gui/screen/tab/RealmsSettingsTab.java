/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.tab.GridScreenTab
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.AxisGridWidget
 *  net.minecraft.client.gui.widget.AxisGridWidget$DisplayAxis
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.IconWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.ServiceQuality
 *  net.minecraft.client.realms.dto.RealmsRegion
 *  net.minecraft.client.realms.dto.RealmsRegionSelectionPreference
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$State
 *  net.minecraft.client.realms.dto.RegionSelectionMethod
 *  net.minecraft.client.realms.gui.RealmsPopups
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsRegionPreferenceScreen
 *  net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab$Region
 *  net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.realms.gui.screen.tab;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsRegionPreferenceScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsSettingsTab
extends GridScreenTab
implements RealmsUpdatableTab {
    private static final int field_60267 = 212;
    private static final int field_60268 = 2;
    private static final int field_60269 = 6;
    static final Text TITLE_TEXT = Text.translatable((String)"mco.configure.world.settings.title");
    private static final Text WORLD_NAME_TEXT = Text.translatable((String)"mco.configure.world.name");
    private static final Text DESCRIPTION_TEXT = Text.translatable((String)"mco.configure.world.description");
    private static final Text REGION_PREFERENCE_TEXT = Text.translatable((String)"mco.configure.world.region_preference");
    private static final Tooltip WORLD_NAME_VALIDATION_TOOLTIP = Tooltip.of((Text)Text.translatable((String)"mco.configure.world.name.validation.whitespace"));
    private final RealmsConfigureWorldScreen screen;
    private final MinecraftClient client;
    private RealmsServer server;
    private final Map<RealmsRegion, ServiceQuality> availableRegions;
    final ButtonWidget switchStateButton;
    private final TextFieldWidget descriptionTextField;
    private final TextFieldWidget worldNameTextField;
    private final TextWidget regionText;
    private final IconWidget serviceQualityIcon;
    private Region region;

    RealmsSettingsTab(RealmsConfigureWorldScreen screen, MinecraftClient client, RealmsServer server, Map<RealmsRegion, ServiceQuality> availableRegions) {
        super(TITLE_TEXT);
        this.screen = screen;
        this.client = client;
        this.server = server;
        this.availableRegions = availableRegions;
        GridWidget.Adder adder = this.grid.setRowSpacing(6).createAdder(1);
        adder.add((Widget)new TextWidget(WORLD_NAME_TEXT, screen.getTextRenderer()));
        this.worldNameTextField = new TextFieldWidget(client.textRenderer, 0, 0, 212, 20, (Text)Text.translatable((String)"mco.configure.world.name"));
        this.worldNameTextField.setMaxLength(32);
        this.worldNameTextField.setChangedListener(name -> {
            if (!this.isWorldNameValid()) {
                this.worldNameTextField.setEditableColor(-2142128);
                this.worldNameTextField.setTooltip(WORLD_NAME_VALIDATION_TOOLTIP);
                return;
            }
            this.worldNameTextField.setTooltip(null);
            this.worldNameTextField.setEditableColor(-2039584);
        });
        adder.add((Widget)this.worldNameTextField);
        adder.add((Widget)EmptyWidget.ofHeight((int)2));
        adder.add((Widget)new TextWidget(DESCRIPTION_TEXT, screen.getTextRenderer()));
        this.descriptionTextField = new TextFieldWidget(client.textRenderer, 0, 0, 212, 20, (Text)Text.translatable((String)"mco.configure.world.description"));
        this.descriptionTextField.setMaxLength(32);
        adder.add((Widget)this.descriptionTextField);
        adder.add((Widget)EmptyWidget.ofHeight((int)2));
        adder.add((Widget)new TextWidget(REGION_PREFERENCE_TEXT, screen.getTextRenderer()));
        Objects.requireNonNull(screen.getTextRenderer());
        AxisGridWidget axisGridWidget = new AxisGridWidget(0, 0, 212, 9, AxisGridWidget.DisplayAxis.HORIZONTAL);
        Objects.requireNonNull(screen.getTextRenderer());
        this.regionText = (TextWidget)axisGridWidget.add((Widget)new TextWidget(192, 9, (Text)Text.empty(), screen.getTextRenderer()));
        this.serviceQualityIcon = (IconWidget)axisGridWidget.add((Widget)IconWidget.create((int)10, (int)8, (Identifier)ServiceQuality.UNKNOWN.getIcon()));
        adder.add((Widget)axisGridWidget);
        adder.add((Widget)ButtonWidget.builder((Text)Text.translatable((String)"mco.configure.world.buttons.region_preference"), button -> this.showRegionPreferenceScreen()).dimensions(0, 0, 212, 20).build());
        adder.add((Widget)EmptyWidget.ofHeight((int)2));
        this.switchStateButton = (ButtonWidget)adder.add((Widget)ButtonWidget.builder((Text)Text.empty(), button -> {
            if (realmsServer.state == RealmsServer.State.OPEN) {
                client.setScreen((Screen)RealmsPopups.createCustomPopup((Screen)screen, (Text)Text.translatable((String)"mco.configure.world.close.question.title"), (Text)Text.translatable((String)"mco.configure.world.close.question.line1"), popup -> {
                    this.saveSettings();
                    screen.closeTheWorld();
                }));
            } else {
                this.saveSettings();
                screen.openTheWorld(false);
            }
        }).dimensions(0, 0, 212, 20).build());
        this.switchStateButton.active = false;
        this.update(server);
    }

    private static MutableText getRegionText(Region region) {
        return (region.preference().equals((Object)RegionSelectionMethod.MANUAL) && region.region() != null ? Text.translatable((String)region.region().translationKey) : Text.translatable((String)region.preference().translationKey)).formatted(Formatting.GRAY);
    }

    private static Identifier getQualityIcon(Region region, Map<RealmsRegion, ServiceQuality> qualityByRegion) {
        if (region.region() != null && qualityByRegion.containsKey(region.region())) {
            ServiceQuality serviceQuality = qualityByRegion.getOrDefault(region.region(), ServiceQuality.UNKNOWN);
            return serviceQuality.getIcon();
        }
        return ServiceQuality.UNKNOWN.getIcon();
    }

    private boolean isWorldNameValid() {
        String string = this.worldNameTextField.getText();
        String string2 = string.trim();
        return !string2.isEmpty() && string.length() == string2.length();
    }

    private void showRegionPreferenceScreen() {
        this.client.setScreen((Screen)new RealmsRegionPreferenceScreen((Screen)this.screen, (arg_0, arg_1) -> this.onRegionChanged(arg_0, arg_1), this.availableRegions, this.region));
    }

    private void onRegionChanged(RegionSelectionMethod selectionMethod, RealmsRegion region) {
        this.region = new Region(selectionMethod, region);
        this.refreshRegionText();
    }

    private void refreshRegionText() {
        this.regionText.setMessage((Text)RealmsSettingsTab.getRegionText((Region)this.region));
        this.serviceQualityIcon.setTexture(RealmsSettingsTab.getQualityIcon((Region)this.region, (Map)this.availableRegions));
        this.serviceQualityIcon.visible = this.region.preference == RegionSelectionMethod.MANUAL;
    }

    public void onLoaded(RealmsServer server) {
        this.update(server);
    }

    public void update(RealmsServer server) {
        this.server = server;
        if (server.regionSelectionPreference == null) {
            server.regionSelectionPreference = RealmsRegionSelectionPreference.DEFAULT;
        }
        if (server.regionSelectionPreference.selectionMethod == RegionSelectionMethod.MANUAL && server.regionSelectionPreference.preferredRegion == null) {
            Optional optional = this.availableRegions.keySet().stream().findFirst();
            optional.ifPresent(region -> {
                realmsServer.regionSelectionPreference.preferredRegion = region;
            });
        }
        String string = server.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
        this.switchStateButton.setMessage((Text)Text.translatable((String)string));
        this.switchStateButton.active = true;
        this.region = new Region(server.regionSelectionPreference.selectionMethod, server.regionSelectionPreference.preferredRegion);
        this.worldNameTextField.setText(Objects.requireNonNullElse(server.getName(), ""));
        this.descriptionTextField.setText(server.getDescription());
        this.refreshRegionText();
    }

    public void onUnloaded(RealmsServer server) {
        this.saveSettings();
    }

    public void saveSettings() {
        String string = this.worldNameTextField.getText().trim();
        if (this.server.regionSelectionPreference != null && Objects.equals(string, this.server.name) && Objects.equals(this.descriptionTextField.getText(), this.server.description) && this.region.preference() == this.server.regionSelectionPreference.selectionMethod && this.region.region() == this.server.regionSelectionPreference.preferredRegion) {
            return;
        }
        this.screen.saveSettings(string, this.descriptionTextField.getText(), this.region.preference(), this.region.region());
    }
}

