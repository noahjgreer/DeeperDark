package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class NoticeScreen extends Screen {
   private static final int NOTICE_TEXT_Y = 90;
   private final Text notice;
   private MultilineText noticeLines;
   private final Runnable actionHandler;
   private final Text buttonText;
   private final boolean shouldCloseOnEsc;

   public NoticeScreen(Runnable actionHandler, Text title, Text notice) {
      this(actionHandler, title, notice, ScreenTexts.BACK, true);
   }

   public NoticeScreen(Runnable actionHandler, Text title, Text notice, Text buttonText, boolean shouldCloseOnEsc) {
      super(title);
      this.noticeLines = MultilineText.EMPTY;
      this.actionHandler = actionHandler;
      this.notice = notice;
      this.buttonText = buttonText;
      this.shouldCloseOnEsc = shouldCloseOnEsc;
   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinSentences(super.getNarratedTitle(), this.notice);
   }

   protected void init() {
      super.init();
      this.noticeLines = MultilineText.create(this.textRenderer, this.notice, this.width - 50);
      int var10000 = this.noticeLines.count();
      Objects.requireNonNull(this.textRenderer);
      int i = var10000 * 9;
      int j = MathHelper.clamp(90 + i + 12, this.height / 6 + 96, this.height - 24);
      int k = true;
      this.addDrawableChild(ButtonWidget.builder(this.buttonText, (button) -> {
         this.actionHandler.run();
      }).dimensions((this.width - 150) / 2, j, 150, 20).build());
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 70, -1);
      this.noticeLines.drawCenterWithShadow(context, this.width / 2, 90);
   }

   public boolean shouldCloseOnEsc() {
      return this.shouldCloseOnEsc;
   }
}
