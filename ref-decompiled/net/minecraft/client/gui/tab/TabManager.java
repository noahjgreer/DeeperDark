/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.tab.Tab
 *  net.minecraft.client.gui.tab.TabManager
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.sound.SoundEvents
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.tab;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TabManager {
    private final Consumer<ClickableWidget> tabLoadWidgetConsumer;
    private final Consumer<ClickableWidget> tabUnloadWidgetConsumer;
    private final Consumer<Tab> tabLoadTabConsumer;
    private final Consumer<Tab> tabUnloadTabConsumer;
    private @Nullable Tab currentTab;
    private @Nullable ScreenRect tabArea;

    public TabManager(Consumer<ClickableWidget> tabLoadWidgetConsumer, Consumer<ClickableWidget> tabUnloadWidgetConsumer) {
        this(tabLoadWidgetConsumer, tabUnloadWidgetConsumer, loadedTab -> {}, unloadedTab -> {});
    }

    public TabManager(Consumer<ClickableWidget> tabLoadWidgetConsumer, Consumer<ClickableWidget> tabUnloadWidgetConsumer, Consumer<Tab> tabLoadTabConsumer, Consumer<Tab> tabUnloadTabConsumer) {
        this.tabLoadWidgetConsumer = tabLoadWidgetConsumer;
        this.tabUnloadWidgetConsumer = tabUnloadWidgetConsumer;
        this.tabLoadTabConsumer = tabLoadTabConsumer;
        this.tabUnloadTabConsumer = tabUnloadTabConsumer;
    }

    public void setTabArea(ScreenRect tabArea) {
        this.tabArea = tabArea;
        Tab tab = this.getCurrentTab();
        if (tab != null) {
            tab.refreshGrid(tabArea);
        }
    }

    public void setCurrentTab(Tab tab, boolean clickSound) {
        if (!Objects.equals(this.currentTab, tab)) {
            if (this.currentTab != null) {
                this.currentTab.forEachChild(this.tabUnloadWidgetConsumer);
            }
            Tab tab2 = this.currentTab;
            this.currentTab = tab;
            tab.forEachChild(this.tabLoadWidgetConsumer);
            if (this.tabArea != null) {
                tab.refreshGrid(this.tabArea);
            }
            if (clickSound) {
                MinecraftClient.getInstance().getSoundManager().play((SoundInstance)PositionedSoundInstance.ui((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, (float)1.0f));
            }
            this.tabUnloadTabConsumer.accept(tab2);
            this.tabLoadTabConsumer.accept(this.currentTab);
        }
    }

    public @Nullable Tab getCurrentTab() {
        return this.currentTab;
    }
}

