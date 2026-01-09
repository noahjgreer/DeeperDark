package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TaskScreen extends Screen {
   private static final int TITLE_TEXT_Y = 80;
   private static final int DESCRIPTION_TEXT_Y = 120;
   private static final int DESCRIPTION_TEXT_WIDTH = 360;
   @Nullable
   private final Text descriptionText;
   private final Text closeButtonText;
   private final Runnable closeCallback;
   @Nullable
   private MultilineText description;
   private ButtonWidget button;
   private int buttonCooldown;

   public static TaskScreen createRunningScreen(Text title, Text closeButtonText, Runnable closeCallback) {
      return new TaskScreen(title, (Text)null, closeButtonText, closeCallback, 0);
   }

   public static TaskScreen createResultScreen(Text title, Text descriptionText, Text closeButtonText, Runnable closeCallback) {
      return new TaskScreen(title, descriptionText, closeButtonText, closeCallback, 20);
   }

   protected TaskScreen(Text title, @Nullable Text descriptionText, Text closeButtonText, Runnable closeCallback, int buttonCooldown) {
      super(title);
      this.descriptionText = descriptionText;
      this.closeButtonText = closeButtonText;
      this.closeCallback = closeCallback;
      this.buttonCooldown = buttonCooldown;
   }

   protected void init() {
      super.init();
      if (this.descriptionText != null) {
         this.description = MultilineText.create(this.textRenderer, this.descriptionText, 360);
      }

      int i = true;
      int j = true;
      int k = this.description != null ? this.description.count() : 1;
      int var10000 = Math.max(k, 5);
      Objects.requireNonNull(this.textRenderer);
      int l = var10000 * 9;
      int m = Math.min(120 + l, this.height - 40);
      this.button = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(this.closeButtonText, (buttonWidget) -> {
         this.close();
      }).dimensions((this.width - 150) / 2, m, 150, 20).build());
   }

   public void tick() {
      if (this.buttonCooldown > 0) {
         --this.buttonCooldown;
      }

      this.button.active = this.buttonCooldown == 0;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 80, -1);
      if (this.description == null) {
         String string = LoadingDisplay.get(Util.getMeasuringTimeMs());
         context.drawCenteredTextWithShadow(this.textRenderer, (String)string, this.width / 2, 120, -6250336);
      } else {
         this.description.drawCenterWithShadow(context, this.width / 2, 120);
      }

   }

   public boolean shouldCloseOnEsc() {
      return this.description != null && this.button.active;
   }

   public void close() {
      this.closeCallback.run();
   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinSentences(this.title, this.descriptionText != null ? this.descriptionText : ScreenTexts.EMPTY);
   }
}
