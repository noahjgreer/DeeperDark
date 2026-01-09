package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.ProgressListener;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ProgressScreen extends Screen implements ProgressListener {
   @Nullable
   private Text title;
   @Nullable
   private Text task;
   private int progress;
   private boolean done;
   private final boolean closeAfterFinished;

   public ProgressScreen(boolean closeAfterFinished) {
      super(NarratorManager.EMPTY);
      this.closeAfterFinished = closeAfterFinished;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean hasUsageText() {
      return false;
   }

   public void setTitle(Text title) {
      this.setTitleAndTask(title);
   }

   public void setTitleAndTask(Text title) {
      this.title = title;
      this.setTask(Text.translatable("menu.working"));
   }

   public void setTask(Text task) {
      this.task = task;
      this.progressStagePercentage(0);
   }

   public void progressStagePercentage(int percentage) {
      this.progress = percentage;
   }

   public void setDone() {
      this.done = true;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (this.done) {
         if (this.closeAfterFinished) {
            this.client.setScreen((Screen)null);
         }

      } else {
         super.render(context, mouseX, mouseY, deltaTicks);
         if (this.title != null) {
            context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 70, -1);
         }

         if (this.task != null && this.progress != 0) {
            context.drawCenteredTextWithShadow(this.textRenderer, (Text)Text.empty().append(this.task).append(" " + this.progress + "%"), this.width / 2, 90, -1);
         }

      }
   }
}
