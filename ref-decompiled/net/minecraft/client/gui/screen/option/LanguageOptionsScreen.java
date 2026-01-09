package net.minecraft.client.gui.screen.option;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class LanguageOptionsScreen extends GameOptionsScreen {
   private static final Text LANGUAGE_WARNING_TEXT = Text.translatable("options.languageAccuracyWarning").withColor(-4539718);
   private static final int field_49497 = 53;
   private LanguageSelectionListWidget languageSelectionList;
   final LanguageManager languageManager;

   public LanguageOptionsScreen(Screen parent, GameOptions options, LanguageManager languageManager) {
      super(parent, options, Text.translatable("options.language.title"));
      this.languageManager = languageManager;
      this.layout.setFooterHeight(53);
   }

   protected void initBody() {
      this.languageSelectionList = (LanguageSelectionListWidget)this.layout.addBody(new LanguageSelectionListWidget(this.client));
   }

   protected void addOptions() {
   }

   protected void initFooter() {
      DirectionalLayoutWidget directionalLayoutWidget = ((DirectionalLayoutWidget)this.layout.addFooter(DirectionalLayoutWidget.vertical())).spacing(8);
      directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
      directionalLayoutWidget.add(new TextWidget(LANGUAGE_WARNING_TEXT, this.textRenderer));
      DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
      directionalLayoutWidget2.add(ButtonWidget.builder(Text.translatable("options.font"), (button) -> {
         this.client.setScreen(new FontOptionsScreen(this, this.gameOptions));
      }).build());
      directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.onDone();
      }).build());
   }

   protected void refreshWidgetPositions() {
      super.refreshWidgetPositions();
      this.languageSelectionList.position(this.width, this.layout);
   }

   void onDone() {
      LanguageSelectionListWidget.LanguageEntry languageEntry = (LanguageSelectionListWidget.LanguageEntry)this.languageSelectionList.getSelectedOrNull();
      if (languageEntry != null && !languageEntry.languageCode.equals(this.languageManager.getLanguage())) {
         this.languageManager.setLanguage(languageEntry.languageCode);
         this.gameOptions.language = languageEntry.languageCode;
         this.client.reloadResources();
      }

      this.client.setScreen(this.parent);
   }

   @Environment(EnvType.CLIENT)
   private class LanguageSelectionListWidget extends AlwaysSelectedEntryListWidget {
      public LanguageSelectionListWidget(final MinecraftClient client) {
         super(client, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height - 33 - 53, 33, 18);
         String string = LanguageOptionsScreen.this.languageManager.getLanguage();
         LanguageOptionsScreen.this.languageManager.getAllLanguages().forEach((languageCode, languageDefinition) -> {
            LanguageEntry languageEntry = new LanguageEntry(languageCode, languageDefinition);
            this.addEntry(languageEntry);
            if (string.equals(languageCode)) {
               this.setSelected(languageEntry);
            }

         });
         if (this.getSelectedOrNull() != null) {
            this.centerScrollOn((LanguageEntry)this.getSelectedOrNull());
         }

      }

      public int getRowWidth() {
         return super.getRowWidth() + 50;
      }

      @Environment(EnvType.CLIENT)
      public class LanguageEntry extends AlwaysSelectedEntryListWidget.Entry {
         final String languageCode;
         private final Text languageDefinition;
         private long clickTime;

         public LanguageEntry(final String languageCode, final LanguageDefinition languageDefinition) {
            this.languageCode = languageCode;
            this.languageDefinition = languageDefinition.getDisplayText();
         }

         public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
            TextRenderer var10001 = LanguageOptionsScreen.this.textRenderer;
            Text var10002 = this.languageDefinition;
            int var10003 = LanguageSelectionListWidget.this.width / 2;
            int var10004 = y + entryHeight / 2;
            Objects.requireNonNull(LanguageOptionsScreen.this.textRenderer);
            context.drawCenteredTextWithShadow(var10001, (Text)var10002, var10003, var10004 - 9 / 2, -1);
         }

         public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (KeyCodes.isToggle(keyCode)) {
               this.onPressed();
               LanguageOptionsScreen.this.onDone();
               return true;
            } else {
               return super.keyPressed(keyCode, scanCode, modifiers);
            }
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.onPressed();
            if (Util.getMeasuringTimeMs() - this.clickTime < 250L) {
               LanguageOptionsScreen.this.onDone();
            }

            this.clickTime = Util.getMeasuringTimeMs();
            return super.mouseClicked(mouseX, mouseY, button);
         }

         private void onPressed() {
            LanguageSelectionListWidget.this.setSelected(this);
         }

         public Text getNarration() {
            return Text.translatable("narrator.select", this.languageDefinition);
         }
      }
   }
}
