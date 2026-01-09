package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.task.WorldCreationTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Text TITLE = Text.translatable("mco.upload.select.world.title");
   private static final Text LOADING_ERROR_TEXT = Text.translatable("selectWorld.unable_to_load");
   static final Text WORLD_LANG = Text.translatable("selectWorld.world");
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   @Nullable
   private final WorldCreationTask creationTask;
   private final RealmsCreateWorldScreen parent;
   private final long worldId;
   private final int slotId;
   ButtonWidget uploadButton;
   List levelList = Lists.newArrayList();
   int selectedWorld = -1;
   WorldSelectionList worldSelectionList;

   public RealmsSelectFileToUploadScreen(@Nullable WorldCreationTask creationTask, long worldId, int slotId, RealmsCreateWorldScreen parent) {
      super(TITLE);
      this.creationTask = creationTask;
      this.parent = parent;
      this.worldId = worldId;
      this.slotId = slotId;
   }

   private void loadLevelList() {
      LevelStorage.LevelList levelList = this.client.getLevelStorage().getLevelList();
      this.levelList = (List)((List)this.client.getLevelStorage().loadSummaries(levelList).join()).stream().filter(LevelSummary::isImmediatelyLoadable).collect(Collectors.toList());
      Iterator var2 = this.levelList.iterator();

      while(var2.hasNext()) {
         LevelSummary levelSummary = (LevelSummary)var2.next();
         this.worldSelectionList.addEntry(levelSummary);
      }

   }

   public void init() {
      this.worldSelectionList = (WorldSelectionList)this.addDrawableChild(new WorldSelectionList());

      try {
         this.loadLevelList();
      } catch (Exception var2) {
         LOGGER.error("Couldn't load level list", var2);
         this.client.setScreen(new RealmsGenericErrorScreen(LOADING_ERROR_TEXT, Text.of(var2.getMessage()), this.parent));
         return;
      }

      this.uploadButton = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.upload.button.name"), (button) -> {
         this.upload();
      }).dimensions(this.width / 2 - 154, this.height - 32, 153, 20).build());
      this.uploadButton.active = this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size();
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
         this.client.setScreen(this.parent);
      }).dimensions(this.width / 2 + 6, this.height - 32, 153, 20).build());
      this.addLabel(new RealmsLabel(Text.translatable("mco.upload.select.world.subtitle"), this.width / 2, row(-1), -6250336));
      if (this.levelList.isEmpty()) {
         this.addLabel(new RealmsLabel(Text.translatable("mco.upload.select.world.none"), this.width / 2, this.height / 2 - 20, -1));
      }

   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
   }

   private void upload() {
      if (this.selectedWorld != -1) {
         LevelSummary levelSummary = (LevelSummary)this.levelList.get(this.selectedWorld);
         this.client.setScreen(new RealmsUploadScreen(this.creationTask, this.worldId, this.slotId, this.parent, levelSummary));
      }

   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 13, -1);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.client.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   static Text getGameModeName(LevelSummary summary) {
      return summary.getGameMode().getTranslatableName();
   }

   static String getLastPlayed(LevelSummary summary) {
      return DATE_FORMAT.format(new Date(summary.getLastPlayed()));
   }

   @Environment(EnvType.CLIENT)
   class WorldSelectionList extends AlwaysSelectedEntryListWidget {
      public WorldSelectionList() {
         super(MinecraftClient.getInstance(), RealmsSelectFileToUploadScreen.this.width, RealmsSelectFileToUploadScreen.this.height - 40 - RealmsSelectFileToUploadScreen.row(0), RealmsSelectFileToUploadScreen.row(0), 36);
      }

      public void addEntry(LevelSummary summary) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldListEntry(summary));
      }

      public void setSelected(@Nullable WorldListEntry worldListEntry) {
         super.setSelected(worldListEntry);
         RealmsSelectFileToUploadScreen.this.selectedWorld = this.children().indexOf(worldListEntry);
         RealmsSelectFileToUploadScreen.this.uploadButton.active = RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getEntryCount();
      }

      public int getRowWidth() {
         return (int)((double)this.width * 0.6);
      }
   }

   @Environment(EnvType.CLIENT)
   private class WorldListEntry extends AlwaysSelectedEntryListWidget.Entry {
      private final LevelSummary summary;
      private final String displayName;
      private final Text nameAndLastPlayed;
      private final Text details;

      public WorldListEntry(final LevelSummary summary) {
         this.summary = summary;
         this.displayName = summary.getDisplayName();
         this.nameAndLastPlayed = Text.translatable("mco.upload.entry.id", summary.getName(), RealmsSelectFileToUploadScreen.getLastPlayed(summary));
         this.details = summary.getDetails();
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         this.renderItem(context, index, x, y);
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.setSelected(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.summary));
         return super.mouseClicked(mouseX, mouseY, button);
      }

      protected void renderItem(DrawContext context, int index, int x, int y) {
         String string;
         if (this.displayName.isEmpty()) {
            String var10000 = String.valueOf(RealmsSelectFileToUploadScreen.WORLD_LANG);
            string = var10000 + " " + (index + 1);
         } else {
            string = this.displayName;
         }

         context.drawTextWithShadow(RealmsSelectFileToUploadScreen.this.textRenderer, (String)string, x + 2, y + 1, -1);
         context.drawTextWithShadow(RealmsSelectFileToUploadScreen.this.textRenderer, this.nameAndLastPlayed, x + 2, y + 12, -8355712);
         context.drawTextWithShadow(RealmsSelectFileToUploadScreen.this.textRenderer, this.details, x + 2, y + 12 + 10, -8355712);
      }

      public Text getNarration() {
         Text text = ScreenTexts.joinLines(Text.literal(this.summary.getDisplayName()), Text.literal(RealmsSelectFileToUploadScreen.getLastPlayed(this.summary)), RealmsSelectFileToUploadScreen.getGameModeName(this.summary));
         return Text.translatable("narrator.select", text);
      }
   }
}
