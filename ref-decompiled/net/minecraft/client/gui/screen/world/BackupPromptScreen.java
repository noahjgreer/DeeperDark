package net.minecraft.client.gui.screen.world;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BackupPromptScreen extends Screen {
   private static final Text SKIP_BUTTON_TEXT = Text.translatable("selectWorld.backupJoinSkipButton");
   public static final Text CONFIRM_BUTTON_TEXT = Text.translatable("selectWorld.backupJoinConfirmButton");
   private final Runnable onCancel;
   protected final Callback callback;
   private final Text subtitle;
   private final boolean showEraseCacheCheckbox;
   private MultilineText wrappedText;
   final Text firstButtonText;
   protected int field_32236;
   private CheckboxWidget eraseCacheCheckbox;

   public BackupPromptScreen(Runnable onCancel, Callback callback, Text title, Text subtitle, boolean showEraseCacheCheckbox) {
      this(onCancel, callback, title, subtitle, CONFIRM_BUTTON_TEXT, showEraseCacheCheckbox);
   }

   public BackupPromptScreen(Runnable onCancel, Callback callback, Text title, Text subtitle, Text firstButtonText, boolean showEraseCacheCheckbox) {
      super(title);
      this.wrappedText = MultilineText.EMPTY;
      this.onCancel = onCancel;
      this.callback = callback;
      this.subtitle = subtitle;
      this.showEraseCacheCheckbox = showEraseCacheCheckbox;
      this.firstButtonText = firstButtonText;
   }

   protected void init() {
      super.init();
      this.wrappedText = MultilineText.create(this.textRenderer, this.subtitle, this.width - 50);
      int var10000 = this.wrappedText.count() + 1;
      Objects.requireNonNull(this.textRenderer);
      int i = var10000 * 9;
      this.eraseCacheCheckbox = CheckboxWidget.builder(Text.translatable("selectWorld.backupEraseCache"), this.textRenderer).pos(this.width / 2 - 155 + 80, 76 + i).build();
      if (this.showEraseCacheCheckbox) {
         this.addDrawableChild(this.eraseCacheCheckbox);
      }

      this.addDrawableChild(ButtonWidget.builder(this.firstButtonText, (button) -> {
         this.callback.proceed(true, this.eraseCacheCheckbox.isChecked());
      }).dimensions(this.width / 2 - 155, 100 + i, 150, 20).build());
      this.addDrawableChild(ButtonWidget.builder(SKIP_BUTTON_TEXT, (button) -> {
         this.callback.proceed(false, this.eraseCacheCheckbox.isChecked());
      }).dimensions(this.width / 2 - 155 + 160, 100 + i, 150, 20).build());
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.onCancel.run();
      }).dimensions(this.width / 2 - 155 + 80, 124 + i, 150, 20).build());
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 50, -1);
      this.wrappedText.drawCenterWithShadow(context, this.width / 2, 70);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.onCancel.run();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   @Environment(EnvType.CLIENT)
   public interface Callback {
      void proceed(boolean backup, boolean eraseCache);
   }
}
