package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class BookEditScreen extends Screen {
   public static final int MAX_TEXT_WIDTH = 114;
   public static final int MAX_TEXT_HEIGHT = 126;
   public static final int WIDTH = 192;
   public static final int HEIGHT = 192;
   public static final int field_52805 = 256;
   public static final int field_52806 = 256;
   private static final Text TITLE_TEXT = Text.translatable("book.edit.title");
   private final PlayerEntity player;
   private final ItemStack stack;
   private final BookSigningScreen signingScreen;
   private int currentPage;
   private final List pages = Lists.newArrayList();
   private PageTurnWidget nextPageButton;
   private PageTurnWidget previousPageButton;
   private final Hand hand;
   private Text pageIndicatorText;
   private EditBoxWidget editBox;

   public BookEditScreen(PlayerEntity player, ItemStack stack, Hand hand, WritableBookContentComponent writableBookContent) {
      super(TITLE_TEXT);
      this.pageIndicatorText = ScreenTexts.EMPTY;
      this.player = player;
      this.stack = stack;
      this.hand = hand;
      Stream var10000 = writableBookContent.stream(MinecraftClient.getInstance().shouldFilterText());
      List var10001 = this.pages;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      if (this.pages.isEmpty()) {
         this.pages.add("");
      }

      this.signingScreen = new BookSigningScreen(this, player, hand, this.pages);
   }

   private int countPages() {
      return this.pages.size();
   }

   protected void init() {
      int i = (this.width - 192) / 2;
      int j = true;
      int k = true;
      this.editBox = EditBoxWidget.builder().hasOverlay(false).textColor(-16777216).cursorColor(-16777216).hasBackground(false).textShadow(false).x((this.width - 114) / 2 - 8).y(28).build(this.textRenderer, 122, 134, ScreenTexts.EMPTY);
      this.editBox.setMaxLength(1024);
      EditBoxWidget var10000 = this.editBox;
      Objects.requireNonNull(this.textRenderer);
      var10000.setMaxLines(126 / 9);
      this.editBox.setChangeListener((page) -> {
         this.pages.set(this.currentPage, page);
      });
      this.addDrawableChild(this.editBox);
      this.updatePage();
      this.pageIndicatorText = this.getPageIndicatorText();
      this.previousPageButton = (PageTurnWidget)this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, (button) -> {
         this.openPreviousPage();
      }, true));
      this.nextPageButton = (PageTurnWidget)this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, (button) -> {
         this.openNextPage();
      }, true));
      this.addDrawableChild(ButtonWidget.builder(Text.translatable("book.signButton"), (button) -> {
         this.client.setScreen(this.signingScreen);
      }).dimensions(this.width / 2 - 100, 196, 98, 20).build());
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.client.setScreen((Screen)null);
         this.finalizeBook();
      }).dimensions(this.width / 2 + 2, 196, 98, 20).build());
      this.updatePreviousPageButtonVisibility();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.editBox);
   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinSentences(super.getNarratedTitle(), this.getPageIndicatorText());
   }

   private Text getPageIndicatorText() {
      return Text.translatable("book.pageIndicator", this.currentPage + 1, this.countPages());
   }

   private void openPreviousPage() {
      if (this.currentPage > 0) {
         --this.currentPage;
         this.updatePage();
      }

      this.updatePreviousPageButtonVisibility();
   }

   private void openNextPage() {
      if (this.currentPage < this.countPages() - 1) {
         ++this.currentPage;
      } else {
         this.appendNewPage();
         if (this.currentPage < this.countPages() - 1) {
            ++this.currentPage;
         }
      }

      this.updatePage();
      this.updatePreviousPageButtonVisibility();
   }

   private void updatePage() {
      this.editBox.setText((String)this.pages.get(this.currentPage), true);
      this.pageIndicatorText = this.getPageIndicatorText();
   }

   private void updatePreviousPageButtonVisibility() {
      this.previousPageButton.visible = this.currentPage > 0;
   }

   private void removeEmptyPages() {
      ListIterator listIterator = this.pages.listIterator(this.pages.size());

      while(listIterator.hasPrevious() && ((String)listIterator.previous()).isEmpty()) {
         listIterator.remove();
      }

   }

   private void finalizeBook() {
      this.removeEmptyPages();
      this.writeNbtData();
      int i = this.hand == Hand.MAIN_HAND ? this.player.getInventory().getSelectedSlot() : 40;
      this.client.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(i, this.pages, Optional.empty()));
   }

   private void writeNbtData() {
      this.stack.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, new WritableBookContentComponent(this.pages.stream().map(RawFilteredPair::of).toList()));
   }

   private void appendNewPage() {
      if (this.countPages() < 100) {
         this.pages.add("");
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      switch (keyCode) {
         case 266:
            this.previousPageButton.onPress();
            return true;
         case 267:
            this.nextPageButton.onPress();
            return true;
         default:
            return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      int i = (this.width - 192) / 2;
      int j = true;
      int k = this.textRenderer.getWidth((StringVisitable)this.pageIndicatorText);
      context.drawText(this.textRenderer, (Text)this.pageIndicatorText, i - k + 192 - 44, 18, -16777216, false);
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.renderInGameBackground(context);
      context.drawTexture(RenderPipelines.GUI_TEXTURED, BookScreen.BOOK_TEXTURE, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }
}
