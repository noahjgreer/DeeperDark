package net.minecraft.client.gui.screen.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.nbt.NbtCrashException;
import net.minecraft.nbt.NbtException;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class EditWorldScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Text ENTER_NAME_TEXT;
   private static final Text RESET_ICON_TEXT;
   private static final Text OPEN_FOLDER_TEXT;
   private static final Text BACKUP_TEXT;
   private static final Text BACKUP_FOLDER_TEXT;
   private static final Text OPTIMIZE_TEXT;
   private static final Text CONFIRM_TITLE_TEXT;
   private static final Text CONFIRM_DESCRIPTION_TEXT;
   private static final Text CONFIRM_PROCEED_TEXT;
   private static final Text SAVE_TEXT;
   private static final int field_46893 = 200;
   private static final int field_46894 = 4;
   private static final int field_46895 = 98;
   private final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(5);
   private final BooleanConsumer callback;
   private final LevelStorage.Session storageSession;
   private final TextFieldWidget nameFieldWidget;

   public static EditWorldScreen create(MinecraftClient client, LevelStorage.Session session, BooleanConsumer callback) throws IOException {
      LevelSummary levelSummary = session.getLevelSummary(session.readLevelProperties());
      return new EditWorldScreen(client, session, levelSummary.getDisplayName(), callback);
   }

   private EditWorldScreen(MinecraftClient client, LevelStorage.Session session, String levelName, BooleanConsumer callback) {
      super(Text.translatable("selectWorld.edit.title"));
      this.callback = callback;
      this.storageSession = session;
      TextRenderer textRenderer = client.textRenderer;
      this.layout.add(new EmptyWidget(200, 20));
      this.layout.add(new TextWidget(ENTER_NAME_TEXT, textRenderer));
      this.nameFieldWidget = (TextFieldWidget)this.layout.add(new TextFieldWidget(textRenderer, 200, 20, ENTER_NAME_TEXT));
      this.nameFieldWidget.setText(levelName);
      DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
      ButtonWidget buttonWidget = (ButtonWidget)directionalLayoutWidget.add(ButtonWidget.builder(SAVE_TEXT, (button) -> {
         this.commit(this.nameFieldWidget.getText());
      }).width(98).build());
      directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.close();
      }).width(98).build());
      this.nameFieldWidget.setChangedListener((name) -> {
         buttonWidget.active = !StringHelper.isBlank(name);
      });
      ((ButtonWidget)this.layout.add(ButtonWidget.builder(RESET_ICON_TEXT, (buttonWidgetx) -> {
         session.getIconFile().ifPresent((path) -> {
            FileUtils.deleteQuietly(path.toFile());
         });
         buttonWidgetx.active = false;
      }).width(200).build())).active = session.getIconFile().filter((path) -> {
         return Files.isRegularFile(path, new LinkOption[0]);
      }).isPresent();
      this.layout.add(ButtonWidget.builder(OPEN_FOLDER_TEXT, (button) -> {
         Util.getOperatingSystem().open(session.getDirectory(WorldSavePath.ROOT));
      }).width(200).build());
      this.layout.add(ButtonWidget.builder(BACKUP_TEXT, (button) -> {
         boolean bl = backupLevel(session);
         this.callback.accept(!bl);
      }).width(200).build());
      this.layout.add(ButtonWidget.builder(BACKUP_FOLDER_TEXT, (button) -> {
         LevelStorage levelStorage = client.getLevelStorage();
         Path path = levelStorage.getBackupsDirectory();

         try {
            PathUtil.createDirectories(path);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }

         Util.getOperatingSystem().open(path);
      }).width(200).build());
      this.layout.add(ButtonWidget.builder(OPTIMIZE_TEXT, (button) -> {
         client.setScreen(new BackupPromptScreen(() -> {
            client.setScreen(this);
         }, (backup, eraseCache) -> {
            if (backup) {
               backupLevel(session);
            }

            client.setScreen(OptimizeWorldScreen.create(client, this.callback, client.getDataFixer(), session, eraseCache));
         }, CONFIRM_TITLE_TEXT, CONFIRM_DESCRIPTION_TEXT, CONFIRM_PROCEED_TEXT, true));
      }).width(200).build());
      this.layout.add(new EmptyWidget(200, 20));
      this.layout.add(directionalLayoutWidget);
      this.layout.forEachChild((child) -> {
         ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
      });
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.nameFieldWidget);
   }

   protected void init() {
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      this.layout.refreshPositions();
      SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
   }

   public void close() {
      this.callback.accept(false);
   }

   private void commit(String levelName) {
      try {
         this.storageSession.save(levelName);
      } catch (NbtException | NbtCrashException | IOException var3) {
         LOGGER.error("Failed to access world '{}'", this.storageSession.getDirectoryName(), var3);
         SystemToast.addWorldAccessFailureToast(this.client, this.storageSession.getDirectoryName());
      }

      this.callback.accept(true);
   }

   public static boolean backupLevel(LevelStorage.Session storageSession) {
      long l = 0L;
      IOException iOException = null;

      try {
         l = storageSession.createBackup();
      } catch (IOException var6) {
         iOException = var6;
      }

      MutableText text;
      MutableText text2;
      if (iOException != null) {
         text = Text.translatable("selectWorld.edit.backupFailed");
         text2 = Text.literal(iOException.getMessage());
         MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
         return false;
      } else {
         text = Text.translatable("selectWorld.edit.backupCreated", storageSession.getDirectoryName());
         text2 = Text.translatable("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
         MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
         return true;
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 15, -1);
   }

   static {
      ENTER_NAME_TEXT = Text.translatable("selectWorld.enterName").formatted(Formatting.GRAY);
      RESET_ICON_TEXT = Text.translatable("selectWorld.edit.resetIcon");
      OPEN_FOLDER_TEXT = Text.translatable("selectWorld.edit.openFolder");
      BACKUP_TEXT = Text.translatable("selectWorld.edit.backup");
      BACKUP_FOLDER_TEXT = Text.translatable("selectWorld.edit.backupFolder");
      OPTIMIZE_TEXT = Text.translatable("selectWorld.edit.optimize");
      CONFIRM_TITLE_TEXT = Text.translatable("optimizeWorld.confirm.title");
      CONFIRM_DESCRIPTION_TEXT = Text.translatable("optimizeWorld.confirm.description");
      CONFIRM_PROCEED_TEXT = Text.translatable("optimizeWorld.confirm.proceed");
      SAVE_TEXT = Text.translatable("selectWorld.edit.save");
   }
}
