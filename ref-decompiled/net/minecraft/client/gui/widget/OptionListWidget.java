package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class OptionListWidget extends ElementListWidget {
   private static final int field_49481 = 310;
   private static final int field_49482 = 25;
   private final GameOptionsScreen optionsScreen;

   public OptionListWidget(MinecraftClient client, int width, GameOptionsScreen optionsScreen) {
      super(client, width, optionsScreen.layout.getContentHeight(), optionsScreen.layout.getHeaderHeight(), 25);
      this.centerListVertically = false;
      this.optionsScreen = optionsScreen;
   }

   public void addSingleOptionEntry(SimpleOption option) {
      this.addEntry(OptionListWidget.OptionWidgetEntry.create(this.client.options, option, this.optionsScreen));
   }

   public void addAll(SimpleOption... options) {
      for(int i = 0; i < options.length; i += 2) {
         SimpleOption simpleOption = i < options.length - 1 ? options[i + 1] : null;
         this.addEntry(OptionListWidget.OptionWidgetEntry.create(this.client.options, options[i], simpleOption, this.optionsScreen));
      }

   }

   public void addAll(List widgets) {
      for(int i = 0; i < widgets.size(); i += 2) {
         this.addWidgetEntry((ClickableWidget)widgets.get(i), i < widgets.size() - 1 ? (ClickableWidget)widgets.get(i + 1) : null);
      }

   }

   public void addWidgetEntry(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget) {
      this.addEntry(OptionListWidget.WidgetEntry.create(firstWidget, secondWidget, this.optionsScreen));
   }

   public int getRowWidth() {
      return 310;
   }

   @Nullable
   public ClickableWidget getWidgetFor(SimpleOption option) {
      Iterator var2 = this.children().iterator();

      while(var2.hasNext()) {
         WidgetEntry widgetEntry = (WidgetEntry)var2.next();
         if (widgetEntry instanceof OptionWidgetEntry optionWidgetEntry) {
            ClickableWidget clickableWidget = (ClickableWidget)optionWidgetEntry.optionWidgets.get(option);
            if (clickableWidget != null) {
               return clickableWidget;
            }
         }
      }

      return null;
   }

   public void applyAllPendingValues() {
      Iterator var1 = this.children().iterator();

      while(true) {
         WidgetEntry widgetEntry;
         do {
            if (!var1.hasNext()) {
               return;
            }

            widgetEntry = (WidgetEntry)var1.next();
         } while(!(widgetEntry instanceof OptionWidgetEntry));

         OptionWidgetEntry optionWidgetEntry = (OptionWidgetEntry)widgetEntry;
         Iterator var4 = optionWidgetEntry.optionWidgets.values().iterator();

         while(var4.hasNext()) {
            ClickableWidget clickableWidget = (ClickableWidget)var4.next();
            if (clickableWidget instanceof SimpleOption.OptionSliderWidgetImpl optionSliderWidgetImpl) {
               optionSliderWidgetImpl.applyPendingValue();
            }
         }
      }
   }

   public Optional getHoveredWidget(double mouseX, double mouseY) {
      Iterator var5 = this.children().iterator();

      while(var5.hasNext()) {
         WidgetEntry widgetEntry = (WidgetEntry)var5.next();
         Iterator var7 = widgetEntry.children().iterator();

         while(var7.hasNext()) {
            Element element = (Element)var7.next();
            if (element.isMouseOver(mouseX, mouseY)) {
               return Optional.of(element);
            }
         }
      }

      return Optional.empty();
   }

   @Environment(EnvType.CLIENT)
   protected static class OptionWidgetEntry extends WidgetEntry {
      final Map optionWidgets;

      private OptionWidgetEntry(Map widgets, GameOptionsScreen optionsScreen) {
         super(ImmutableList.copyOf(widgets.values()), optionsScreen);
         this.optionWidgets = widgets;
      }

      public static OptionWidgetEntry create(GameOptions gameOptions, SimpleOption option, GameOptionsScreen optionsScreen) {
         return new OptionWidgetEntry(ImmutableMap.of(option, option.createWidget(gameOptions, 0, 0, 310)), optionsScreen);
      }

      public static OptionWidgetEntry create(GameOptions gameOptions, SimpleOption firstOption, @Nullable SimpleOption secondOption, GameOptionsScreen optionsScreen) {
         ClickableWidget clickableWidget = firstOption.createWidget(gameOptions);
         return secondOption == null ? new OptionWidgetEntry(ImmutableMap.of(firstOption, clickableWidget), optionsScreen) : new OptionWidgetEntry(ImmutableMap.of(firstOption, clickableWidget, secondOption, secondOption.createWidget(gameOptions)), optionsScreen);
      }
   }

   @Environment(EnvType.CLIENT)
   protected static class WidgetEntry extends ElementListWidget.Entry {
      private final List widgets;
      private final Screen screen;
      private static final int WIDGET_X_SPACING = 160;

      WidgetEntry(List widgets, Screen screen) {
         this.widgets = ImmutableList.copyOf(widgets);
         this.screen = screen;
      }

      public static WidgetEntry create(List widgets, Screen screen) {
         return new WidgetEntry(widgets, screen);
      }

      public static WidgetEntry create(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget, Screen screen) {
         return secondWidget == null ? new WidgetEntry(ImmutableList.of(firstWidget), screen) : new WidgetEntry(ImmutableList.of(firstWidget, secondWidget), screen);
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         int i = 0;
         int j = this.screen.width / 2 - 155;

         for(Iterator var13 = this.widgets.iterator(); var13.hasNext(); i += 160) {
            ClickableWidget clickableWidget = (ClickableWidget)var13.next();
            clickableWidget.setPosition(j + i, y);
            clickableWidget.render(context, mouseX, mouseY, tickProgress);
         }

      }

      public List children() {
         return this.widgets;
      }

      public List selectableChildren() {
         return this.widgets;
      }
   }
}
