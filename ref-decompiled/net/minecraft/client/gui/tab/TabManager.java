package net.minecraft.client.gui.tab;

import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TabManager {
   private final Consumer tabLoadWidgetConsumer;
   private final Consumer tabUnloadWidgetConsumer;
   private final Consumer tabLoadTabConsumer;
   private final Consumer tabUnloadTabConsumer;
   @Nullable
   private Tab currentTab;
   @Nullable
   private ScreenRect tabArea;

   public TabManager(Consumer tabLoadWidgetConsumer, Consumer tabUnloadWidgetConsumer) {
      this(tabLoadWidgetConsumer, tabUnloadWidgetConsumer, (loadedTab) -> {
      }, (unloadedTab) -> {
      });
   }

   public TabManager(Consumer tabLoadWidgetConsumer, Consumer tabUnloadWidgetConsumer, Consumer tabLoadTabConsumer, Consumer tabUnloadTabConsumer) {
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
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         this.tabUnloadTabConsumer.accept(tab2);
         this.tabLoadTabConsumer.accept(this.currentTab);
      }

   }

   @Nullable
   public Tab getCurrentTab() {
      return this.currentTab;
   }
}
