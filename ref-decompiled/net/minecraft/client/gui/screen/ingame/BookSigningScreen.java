package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.StringHelper;

@Environment(EnvType.CLIENT)
public class BookSigningScreen extends Screen {
   private static final Text EDIT_TITLE_TEXT = Text.translatable("book.editTitle");
   private static final Text FINALIZE_WARNING_TEXT = Text.translatable("book.finalizeWarning");
   private static final Text TITLE_TEXT = Text.translatable("book.sign.title");
   private static final Text TITLE_BOX_TEXT = Text.translatable("book.sign.titlebox");
   private final BookEditScreen editScreen;
   private final PlayerEntity player;
   private final List pages;
   private final Hand hand;
   private final Text bylineText;
   private TextFieldWidget bookTitleTextField;
   private String bookTitle = "";

   public BookSigningScreen(BookEditScreen editScreen, PlayerEntity player, Hand hand, List pages) {
      super(TITLE_TEXT);
      this.editScreen = editScreen;
      this.player = player;
      this.hand = hand;
      this.pages = pages;
      this.bylineText = Text.translatable("book.byAuthor", player.getName()).formatted(Formatting.DARK_GRAY);
   }

   protected void init() {
      ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("book.finalizeButton"), (button) -> {
         this.onFinalize();
         this.client.setScreen((Screen)null);
      }).dimensions(this.width / 2 - 100, 196, 98, 20).build();
      buttonWidget.active = false;
      this.bookTitleTextField = (TextFieldWidget)this.addDrawableChild(new TextFieldWidget(this.client.textRenderer, (this.width - 114) / 2 - 3, 50, 114, 20, TITLE_BOX_TEXT));
      this.bookTitleTextField.setMaxLength(15);
      this.bookTitleTextField.setDrawsBackground(false);
      this.bookTitleTextField.setCentered(true);
      this.bookTitleTextField.setEditableColor(-16777216);
      this.bookTitleTextField.setTextShadow(false);
      this.bookTitleTextField.setChangedListener((bookTitle) -> {
         buttonWidget.active = !StringHelper.isBlank(bookTitle);
      });
      this.bookTitleTextField.setText(this.bookTitle);
      this.addDrawableChild(buttonWidget);
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.bookTitle = this.bookTitleTextField.getText();
         this.client.setScreen(this.editScreen);
      }).dimensions(this.width / 2 + 2, 196, 98, 20).build());
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.bookTitleTextField);
   }

   private void onFinalize() {
      int i = this.hand == Hand.MAIN_HAND ? this.player.getInventory().getSelectedSlot() : 40;
      this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(i, this.pages, Optional.of(this.bookTitleTextField.getText().trim())));
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.bookTitleTextField.isFocused() || this.bookTitleTextField.getText().isEmpty() || keyCode != 257 && keyCode != 335) {
         return super.keyPressed(keyCode, scanCode, modifiers);
      } else {
         this.onFinalize();
         this.client.setScreen((Screen)null);
         return true;
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      int i = (this.width - 192) / 2;
      int j = true;
      int k = this.textRenderer.getWidth((StringVisitable)EDIT_TITLE_TEXT);
      context.drawText(this.textRenderer, (Text)EDIT_TITLE_TEXT, i + 36 + (114 - k) / 2, 34, -16777216, false);
      int l = this.textRenderer.getWidth((StringVisitable)this.bylineText);
      context.drawText(this.textRenderer, (Text)this.bylineText, i + 36 + (114 - l) / 2, 60, -16777216, false);
      context.drawWrappedText(this.textRenderer, FINALIZE_WARNING_TEXT, i + 36, 82, 114, -16777216, false);
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.renderInGameBackground(context);
      context.drawTexture(RenderPipelines.GUI_TEXTURED, BookScreen.BOOK_TEXTURE, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }
}
