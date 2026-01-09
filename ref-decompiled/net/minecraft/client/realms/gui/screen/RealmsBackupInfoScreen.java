package net.minecraft.client.realms.gui.screen;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.realms.dto.Backup;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class RealmsBackupInfoScreen extends RealmsScreen {
   private static final Text TITLE = Text.translatable("mco.backup.info.title");
   private static final Text UNKNOWN = Text.translatable("mco.backup.unknown");
   private final Screen parent;
   final Backup backup;
   final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
   private BackupInfoList backupInfoList;

   public RealmsBackupInfoScreen(Screen parent, Backup backup) {
      super(TITLE);
      this.parent = parent;
      this.backup = backup;
   }

   public void init() {
      this.layout.addHeader(TITLE, this.textRenderer);
      this.backupInfoList = (BackupInfoList)this.layout.addBody(new BackupInfoList(this.client));
      this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
         this.close();
      }).build());
      this.refreshWidgetPositions();
      this.layout.forEachChild((child) -> {
         ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
      });
   }

   protected void refreshWidgetPositions() {
      this.backupInfoList.setDimensions(this.width, this.layout.getContentHeight());
      this.layout.refreshPositions();
   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   Text checkForSpecificMetadata(String key, String value) {
      String string = key.toLowerCase(Locale.ROOT);
      if (string.contains("game") && string.contains("mode")) {
         return this.gameModeMetadata(value);
      } else {
         return (Text)(string.contains("game") && string.contains("difficulty") ? this.gameDifficultyMetadata(value) : Text.literal(value));
      }
   }

   private Text gameDifficultyMetadata(String value) {
      try {
         return ((Difficulty)RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(value))).getTranslatableName();
      } catch (Exception var3) {
         return UNKNOWN;
      }
   }

   private Text gameModeMetadata(String value) {
      try {
         return ((GameMode)RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(value))).getSimpleTranslatableName();
      } catch (Exception var3) {
         return UNKNOWN;
      }
   }

   @Environment(EnvType.CLIENT)
   private class BackupInfoList extends AlwaysSelectedEntryListWidget {
      public BackupInfoList(final MinecraftClient client) {
         super(client, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.layout.getContentHeight(), RealmsBackupInfoScreen.this.layout.getHeaderHeight(), 36);
         if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup.changeList.forEach((key, value) -> {
               this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(key, value));
            });
         }

      }
   }

   @Environment(EnvType.CLIENT)
   class BackupInfoListEntry extends AlwaysSelectedEntryListWidget.Entry {
      private static final Text TEMPLATE_NAME_TEXT = Text.translatable("mco.backup.entry.templateName");
      private static final Text GAME_DIFFICULTY_TEXT = Text.translatable("mco.backup.entry.gameDifficulty");
      private static final Text NAME_TEXT = Text.translatable("mco.backup.entry.name");
      private static final Text GAME_SERVER_VERSION_TEXT = Text.translatable("mco.backup.entry.gameServerVersion");
      private static final Text UPLOADED_TEXT = Text.translatable("mco.backup.entry.uploaded");
      private static final Text ENABLED_PACK_TEXT = Text.translatable("mco.backup.entry.enabledPack");
      private static final Text DESCRIPTION_TEXT = Text.translatable("mco.backup.entry.description");
      private static final Text GAME_MODE_TEXT = Text.translatable("mco.backup.entry.gameMode");
      private static final Text SEED_TEXT = Text.translatable("mco.backup.entry.seed");
      private static final Text WORLD_TYPE_TEXT = Text.translatable("mco.backup.entry.worldType");
      private static final Text UNDEFINED_TEXT = Text.translatable("mco.backup.entry.undefined");
      private final String key;
      private final String value;

      public BackupInfoListEntry(final String key, final String value) {
         this.key = key;
         this.value = value;
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         context.drawTextWithShadow(RealmsBackupInfoScreen.this.textRenderer, this.getTextFromKey(this.key), x, y, -6250336);
         context.drawTextWithShadow(RealmsBackupInfoScreen.this.textRenderer, (Text)RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), x, y + 12, -1);
      }

      private Text getTextFromKey(String key) {
         Text var10000;
         switch (key) {
            case "template_name":
               var10000 = TEMPLATE_NAME_TEXT;
               break;
            case "game_difficulty":
               var10000 = GAME_DIFFICULTY_TEXT;
               break;
            case "name":
               var10000 = NAME_TEXT;
               break;
            case "game_server_version":
               var10000 = GAME_SERVER_VERSION_TEXT;
               break;
            case "uploaded":
               var10000 = UPLOADED_TEXT;
               break;
            case "enabled_packs":
               var10000 = ENABLED_PACK_TEXT;
               break;
            case "description":
               var10000 = DESCRIPTION_TEXT;
               break;
            case "game_mode":
               var10000 = GAME_MODE_TEXT;
               break;
            case "seed":
               var10000 = SEED_TEXT;
               break;
            case "world_type":
               var10000 = WORLD_TYPE_TEXT;
               break;
            default:
               var10000 = UNDEFINED_TEXT;
         }

         return var10000;
      }

      public Text getNarration() {
         return Text.translatable("narrator.select", this.key + " " + this.value);
      }
   }
}
