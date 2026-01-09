package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsTermsScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Text TITLE = Text.translatable("mco.terms.title");
   private static final Text SENTENCE_ONE_TEXT = Text.translatable("mco.terms.sentence.1");
   private static final Text SENTENCE_TWO_TEXT;
   private final Screen parent;
   private final RealmsServer realmsServer;
   private boolean onLink;

   public RealmsTermsScreen(Screen parent, RealmsServer realmsServer) {
      super(TITLE);
      this.parent = parent;
      this.realmsServer = realmsServer;
   }

   public void init() {
      int i = this.width / 4 - 2;
      this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.terms.buttons.agree"), (button) -> {
         this.agreedToTos();
      }).dimensions(this.width / 4, row(12), i, 20).build());
      this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.terms.buttons.disagree"), (button) -> {
         this.client.setScreen(this.parent);
      }).dimensions(this.width / 2 + 4, row(12), i, 20).build());
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.client.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   private void agreedToTos() {
      RealmsClient realmsClient = RealmsClient.create();

      try {
         realmsClient.agreeToTos();
         this.client.setScreen(new RealmsLongRunningMcoTaskScreen(this.parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(this.parent, this.realmsServer)}));
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn't agree to TOS", var3);
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.onLink) {
         this.client.keyboard.setClipboard(Urls.REALMS_TERMS.toString());
         Util.getOperatingSystem().open(Urls.REALMS_TERMS);
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinSentences(super.getNarratedTitle(), SENTENCE_ONE_TEXT).append(ScreenTexts.SPACE).append(SENTENCE_TWO_TEXT);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 17, -1);
      context.drawTextWithShadow(this.textRenderer, (Text)SENTENCE_ONE_TEXT, this.width / 2 - 120, row(5), -1);
      int i = this.textRenderer.getWidth((StringVisitable)SENTENCE_ONE_TEXT);
      int j = this.width / 2 - 121 + i;
      int k = row(5);
      int l = j + this.textRenderer.getWidth((StringVisitable)SENTENCE_TWO_TEXT) + 1;
      int var10000 = k + 1;
      Objects.requireNonNull(this.textRenderer);
      int m = var10000 + 9;
      this.onLink = j <= mouseX && mouseX <= l && k <= mouseY && mouseY <= m;
      context.drawTextWithShadow(this.textRenderer, SENTENCE_TWO_TEXT, this.width / 2 - 120 + i, row(5), this.onLink ? -9670204 : -13408581);
   }

   static {
      SENTENCE_TWO_TEXT = ScreenTexts.space().append((Text)Text.translatable("mco.terms.sentence.2").fillStyle(Style.EMPTY.withUnderline(true)));
   }
}
