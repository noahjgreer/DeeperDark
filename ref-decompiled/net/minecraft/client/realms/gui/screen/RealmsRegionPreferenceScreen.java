package net.minecraft.client.realms.gui.screen;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RealmsRegionPreferenceScreen extends Screen {
   private static final Text TITLE_TEXT = Text.translatable("mco.configure.world.region_preference.title");
   private static final int field_60254 = 8;
   private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
   private final Screen parent;
   private final BiConsumer onRegionChanged;
   final Map availableRegions;
   @Nullable
   private RegionListWidget regionList;
   RealmsSettingsTab.Region currentRegion;
   @Nullable
   private ButtonWidget doneButton;

   public RealmsRegionPreferenceScreen(Screen parent, BiConsumer onRegionChanged, Map availableRegions, RealmsSettingsTab.Region textSupplier) {
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
      DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(8));
      directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
      directionalLayoutWidget.add(new TextWidget(this.getTitle(), this.textRenderer));
      this.regionList = (RegionListWidget)this.layout.addBody(new RegionListWidget());
      DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
      this.doneButton = (ButtonWidget)directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.onRegionChanged.accept(this.currentRegion.preference(), this.currentRegion.region());
         this.close();
      }).build());
      directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.close();
      }).build());
      this.regionList.setSelected((RegionListWidget.RegionEntry)this.regionList.children().stream().filter((region) -> {
         return Objects.equals(region.region, this.currentRegion);
      }).findFirst().orElse((Object)null));
      this.layout.forEachChild((child) -> {
         ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
      });
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      this.layout.refreshPositions();
      this.regionList.position(this.width, this.layout);
   }

   void refreshDoneButton() {
      this.doneButton.active = this.regionList.getSelectedOrNull() != null;
   }

   @Environment(EnvType.CLIENT)
   class RegionListWidget extends AlwaysSelectedEntryListWidget {
      RegionListWidget() {
         super(RealmsRegionPreferenceScreen.this.client, RealmsRegionPreferenceScreen.this.width, RealmsRegionPreferenceScreen.this.height - 77, 40, 16);
         this.addEntry(new RegionEntry(RegionSelectionMethod.AUTOMATIC_PLAYER, (RealmsRegion)null));
         this.addEntry(new RegionEntry(RegionSelectionMethod.AUTOMATIC_OWNER, (RealmsRegion)null));
         RealmsRegionPreferenceScreen.this.availableRegions.keySet().stream().map((region) -> {
            return new RegionEntry(RegionSelectionMethod.MANUAL, region);
         }).forEach((entry) -> {
            this.addEntry(entry);
         });
      }

      public void setSelected(@Nullable RegionEntry regionEntry) {
         super.setSelected(regionEntry);
         if (regionEntry != null) {
            RealmsRegionPreferenceScreen.this.currentRegion = regionEntry.region;
         }

         RealmsRegionPreferenceScreen.this.refreshDoneButton();
      }

      @Environment(EnvType.CLIENT)
      private class RegionEntry extends AlwaysSelectedEntryListWidget.Entry {
         final RealmsSettingsTab.Region region;
         private final Text name;

         public RegionEntry(final RegionSelectionMethod selectionMethod, @Nullable final RealmsRegion region) {
            this(new RealmsSettingsTab.Region(selectionMethod, region));
         }

         public RegionEntry(final RealmsSettingsTab.Region region) {
            this.region = region;
            if (region.preference() == RegionSelectionMethod.MANUAL) {
               if (region.region() != null) {
                  this.name = Text.translatable(region.region().translationKey);
               } else {
                  this.name = Text.empty();
               }
            } else {
               this.name = Text.translatable(region.preference().translationKey);
            }

         }

         public Text getNarration() {
            return Text.translatable("narrator.select", this.name);
         }

         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            context.drawTextWithShadow(RealmsRegionPreferenceScreen.this.textRenderer, (Text)this.name, x + 5, y + 2, -1);
            if (this.region.region() != null && RealmsRegionPreferenceScreen.this.availableRegions.containsKey(this.region.region())) {
               ServiceQuality serviceQuality = (ServiceQuality)RealmsRegionPreferenceScreen.this.availableRegions.getOrDefault(this.region.region(), ServiceQuality.UNKNOWN);
               context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, serviceQuality.getIcon(), x + entryWidth - 18, y + 2, 10, 8);
            }

         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            RegionListWidget.this.setSelected(this);
            return super.mouseClicked(mouseX, mouseY, button);
         }
      }
   }
}
