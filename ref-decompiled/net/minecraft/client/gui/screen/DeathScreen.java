package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DeathScreen extends Screen {
   private static final Identifier DRAFT_REPORT_ICON_TEXTURE = Identifier.ofVanilla("icon/draft_report");
   private int ticksSinceDeath;
   private final Text message;
   private final boolean isHardcore;
   private Text scoreText;
   private final List buttons = Lists.newArrayList();
   @Nullable
   private ButtonWidget titleScreenButton;

   public DeathScreen(@Nullable Text message, boolean isHardcore) {
      super(Text.translatable(isHardcore ? "deathScreen.title.hardcore" : "deathScreen.title"));
      this.message = message;
      this.isHardcore = isHardcore;
   }

   protected void init() {
      this.ticksSinceDeath = 0;
      this.buttons.clear();
      Text text = this.isHardcore ? Text.translatable("deathScreen.spectate") : Text.translatable("deathScreen.respawn");
      this.buttons.add((ButtonWidget)this.addDrawableChild(ButtonWidget.builder(text, (button) -> {
         this.client.player.requestRespawn();
         button.active = false;
      }).dimensions(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build()));
      this.titleScreenButton = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("deathScreen.titleScreen"), (button) -> {
         this.client.getAbuseReportContext().tryShowDraftScreen(this.client, this, this::onTitleScreenButtonClicked, true);
      }).dimensions(this.width / 2 - 100, this.height / 4 + 96, 200, 20).build());
      this.buttons.add(this.titleScreenButton);
      this.setButtonsActive(false);
      this.scoreText = Text.translatable("deathScreen.score.value", Text.literal(Integer.toString(this.client.player.getScore())).formatted(Formatting.YELLOW));
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void onTitleScreenButtonClicked() {
      if (this.isHardcore) {
         this.quitLevel();
      } else {
         ConfirmScreen confirmScreen = new TitleScreenConfirmScreen((confirmed) -> {
            if (confirmed) {
               this.quitLevel();
            } else {
               this.client.player.requestRespawn();
               this.client.setScreen((Screen)null);
            }

         }, Text.translatable("deathScreen.quit.confirm"), ScreenTexts.EMPTY, Text.translatable("deathScreen.titleScreen"), Text.translatable("deathScreen.respawn"));
         this.client.setScreen(confirmScreen);
         confirmScreen.disableButtons(20);
      }
   }

   private void quitLevel() {
      if (this.client.world != null) {
         this.client.world.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
      }

      this.client.disconnectWithSavingScreen();
      this.client.setScreen(new TitleScreen());
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.getMatrices().pushMatrix();
      context.getMatrices().scale(2.0F, 2.0F);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2 / 2, 30, -1);
      context.getMatrices().popMatrix();
      if (this.message != null) {
         context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.message, this.width / 2, 85, -1);
      }

      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.scoreText, this.width / 2, 100, -1);
      if (this.message != null && mouseY > 85) {
         Objects.requireNonNull(this.textRenderer);
         if (mouseY < 85 + 9) {
            Style style = this.getTextComponentUnderMouse(mouseX);
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
         }
      }

      if (this.titleScreenButton != null && this.client.getAbuseReportContext().hasDraft()) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DRAFT_REPORT_ICON_TEXTURE, this.titleScreenButton.getX() + this.titleScreenButton.getWidth() - 17, this.titleScreenButton.getY() + 3, 15, 15);
      }

   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      fillBackgroundGradient(context, this.width, this.height);
   }

   static void fillBackgroundGradient(DrawContext context, int width, int height) {
      context.fillGradient(0, 0, width, height, 1615855616, -1602211792);
   }

   @Nullable
   private Style getTextComponentUnderMouse(int mouseX) {
      if (this.message == null) {
         return null;
      } else {
         int i = this.client.textRenderer.getWidth((StringVisitable)this.message);
         int j = this.width / 2 - i / 2;
         int k = this.width / 2 + i / 2;
         return mouseX >= j && mouseX <= k ? this.client.textRenderer.getTextHandler().getStyleAt((StringVisitable)this.message, mouseX - j) : null;
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.message != null && mouseY > 85.0) {
         Objects.requireNonNull(this.textRenderer);
         if (mouseY < (double)(85 + 9)) {
            Style style = this.getTextComponentUnderMouse((int)mouseX);
            if (style != null) {
               ClickEvent var8 = style.getClickEvent();
               if (var8 instanceof ClickEvent.OpenUrl) {
                  ClickEvent.OpenUrl openUrl = (ClickEvent.OpenUrl)var8;
                  return handleOpenUri(this.client, this, openUrl.uri());
               }
            }
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean shouldPause() {
      return false;
   }

   public void tick() {
      super.tick();
      ++this.ticksSinceDeath;
      if (this.ticksSinceDeath == 20) {
         this.setButtonsActive(true);
      }

   }

   private void setButtonsActive(boolean active) {
      ButtonWidget buttonWidget;
      for(Iterator var2 = this.buttons.iterator(); var2.hasNext(); buttonWidget.active = active) {
         buttonWidget = (ButtonWidget)var2.next();
      }

   }

   @Environment(EnvType.CLIENT)
   public static class TitleScreenConfirmScreen extends ConfirmScreen {
      public TitleScreenConfirmScreen(BooleanConsumer booleanConsumer, Text text, Text text2, Text text3, Text text4) {
         super(booleanConsumer, text, text2, text3, text4);
      }

      public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         DeathScreen.fillBackgroundGradient(context, this.width, this.height);
      }
   }
}
