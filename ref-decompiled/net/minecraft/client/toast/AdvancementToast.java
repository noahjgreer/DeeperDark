package net.minecraft.client.toast;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class AdvancementToast implements Toast {
   private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
   public static final int DEFAULT_DURATION_MS = 5000;
   private final AdvancementEntry advancement;
   private Toast.Visibility visibility;

   public AdvancementToast(AdvancementEntry advancement) {
      this.visibility = Toast.Visibility.HIDE;
      this.advancement = advancement;
   }

   public Toast.Visibility getVisibility() {
      return this.visibility;
   }

   public void update(ToastManager manager, long time) {
      AdvancementDisplay advancementDisplay = (AdvancementDisplay)this.advancement.value().display().orElse((Object)null);
      if (advancementDisplay == null) {
         this.visibility = Toast.Visibility.HIDE;
      } else {
         this.visibility = (double)time >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
      }
   }

   @Nullable
   public SoundEvent getSoundEvent() {
      return this.isChallenge() ? SoundEvents.UI_TOAST_CHALLENGE_COMPLETE : null;
   }

   private boolean isChallenge() {
      Optional optional = this.advancement.value().display();
      return optional.isPresent() && ((AdvancementDisplay)optional.get()).getFrame().equals(AdvancementFrame.CHALLENGE);
   }

   public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
      AdvancementDisplay advancementDisplay = (AdvancementDisplay)this.advancement.value().display().orElse((Object)null);
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.getWidth(), this.getHeight());
      if (advancementDisplay != null) {
         List list = textRenderer.wrapLines(advancementDisplay.getTitle(), 125);
         int i = advancementDisplay.getFrame() == AdvancementFrame.CHALLENGE ? -30465 : -256;
         if (list.size() == 1) {
            context.drawText(textRenderer, (Text)advancementDisplay.getFrame().getToastText(), 30, 7, i, false);
            context.drawText(textRenderer, (OrderedText)((OrderedText)list.get(0)), 30, 18, -1, false);
         } else {
            int j = true;
            float f = 300.0F;
            int k;
            if (startTime < 1500L) {
               k = MathHelper.floor(MathHelper.clamp((float)(1500L - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F);
               context.drawText(textRenderer, (Text)advancementDisplay.getFrame().getToastText(), 30, 11, ColorHelper.withAlpha(k, i), false);
            } else {
               k = MathHelper.floor(MathHelper.clamp((float)(startTime - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F);
               int var10000 = this.getHeight() / 2;
               int var10001 = list.size();
               Objects.requireNonNull(textRenderer);
               int l = var10000 - var10001 * 9 / 2;

               for(Iterator var12 = list.iterator(); var12.hasNext(); l += 9) {
                  OrderedText orderedText = (OrderedText)var12.next();
                  context.drawText(textRenderer, (OrderedText)orderedText, 30, l, ColorHelper.withAlpha(k, -1), false);
                  Objects.requireNonNull(textRenderer);
               }
            }
         }

         context.drawItemWithoutEntity(advancementDisplay.getIcon(), 8, 8);
      }
   }
}
