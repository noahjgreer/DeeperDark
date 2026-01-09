package net.minecraft.client.realms.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RealmsGenericErrorScreen extends RealmsScreen {
   private final Screen parent;
   private final ErrorMessages errorMessages;
   private MultilineText description;

   public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, Screen parent) {
      super(NarratorManager.EMPTY);
      this.description = MultilineText.EMPTY;
      this.parent = parent;
      this.errorMessages = getErrorMessages(realmsServiceException);
   }

   public RealmsGenericErrorScreen(Text description, Screen parent) {
      super(NarratorManager.EMPTY);
      this.description = MultilineText.EMPTY;
      this.parent = parent;
      this.errorMessages = getErrorMessages(description);
   }

   public RealmsGenericErrorScreen(Text title, Text description, Screen parent) {
      super(NarratorManager.EMPTY);
      this.description = MultilineText.EMPTY;
      this.parent = parent;
      this.errorMessages = getErrorMessages(title, description);
   }

   private static ErrorMessages getErrorMessages(RealmsServiceException exception) {
      RealmsError realmsError = exception.error;
      return getErrorMessages(Text.translatable("mco.errorMessage.realmsService.realmsError", realmsError.getErrorCode()), realmsError.getText());
   }

   private static ErrorMessages getErrorMessages(Text description) {
      return getErrorMessages(Text.translatable("mco.errorMessage.generic"), description);
   }

   private static ErrorMessages getErrorMessages(Text title, Text description) {
      return new ErrorMessages(title, description);
   }

   public void init() {
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.OK, (button) -> {
         this.close();
      }).dimensions(this.width / 2 - 100, this.height - 52, 200, 20).build());
      this.description = MultilineText.create(this.textRenderer, this.errorMessages.detail, this.width * 3 / 4);
   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   public Text getNarratedTitle() {
      return Text.empty().append(this.errorMessages.title).append(": ").append(this.errorMessages.detail);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.errorMessages.title, this.width / 2, 80, -1);
      MultilineText var10000 = this.description;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.client.textRenderer);
      var10000.drawCenterWithShadow(context, var10002, 100, 9, -2142128);
   }

   @Environment(EnvType.CLIENT)
   private static record ErrorMessages(Text title, Text detail) {
      final Text title;
      final Text detail;

      ErrorMessages(Text text, Text text2) {
         this.title = text;
         this.detail = text2;
      }

      public Text title() {
         return this.title;
      }

      public Text detail() {
         return this.detail;
      }
   }
}
