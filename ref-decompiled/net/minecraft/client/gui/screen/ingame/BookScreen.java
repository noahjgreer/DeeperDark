package net.minecraft.client.gui.screen.ingame;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BookScreen extends Screen {
   public static final int field_32328 = 16;
   public static final int field_32329 = 36;
   public static final int field_32330 = 30;
   private static final int field_52807 = 256;
   private static final int field_52808 = 256;
   private static final Text TITLE_TEXT = Text.translatable("book.view.title");
   public static final Contents EMPTY_PROVIDER = new Contents(List.of());
   public static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/gui/book.png");
   protected static final int MAX_TEXT_WIDTH = 114;
   protected static final int MAX_TEXT_HEIGHT = 128;
   protected static final int WIDTH = 192;
   protected static final int HEIGHT = 192;
   private Contents contents;
   private int pageIndex;
   private List cachedPage;
   private int cachedPageIndex;
   private Text pageIndexText;
   private PageTurnWidget nextPageButton;
   private PageTurnWidget previousPageButton;
   private final boolean pageTurnSound;

   public BookScreen(Contents pageProvider) {
      this(pageProvider, true);
   }

   public BookScreen() {
      this(EMPTY_PROVIDER, false);
   }

   private BookScreen(Contents contents, boolean playPageTurnSound) {
      super(TITLE_TEXT);
      this.cachedPage = Collections.emptyList();
      this.cachedPageIndex = -1;
      this.pageIndexText = ScreenTexts.EMPTY;
      this.contents = contents;
      this.pageTurnSound = playPageTurnSound;
   }

   public void setPageProvider(Contents pageProvider) {
      this.contents = pageProvider;
      this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getPageCount());
      this.updatePageButtons();
      this.cachedPageIndex = -1;
   }

   public boolean setPage(int index) {
      int i = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
      if (i != this.pageIndex) {
         this.pageIndex = i;
         this.updatePageButtons();
         this.cachedPageIndex = -1;
         return true;
      } else {
         return false;
      }
   }

   protected boolean jumpToPage(int page) {
      return this.setPage(page);
   }

   protected void init() {
      this.addCloseButton();
      this.addPageButtons();
   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinLines(super.getNarratedTitle(), this.getPageIndicatorText(), this.contents.getPage(this.pageIndex));
   }

   private Text getPageIndicatorText() {
      return Text.translatable("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
   }

   protected void addCloseButton() {
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.close();
      }).dimensions(this.width / 2 - 100, 196, 200, 20).build());
   }

   protected void addPageButtons() {
      int i = (this.width - 192) / 2;
      int j = true;
      this.nextPageButton = (PageTurnWidget)this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, (button) -> {
         this.goToNextPage();
      }, this.pageTurnSound));
      this.previousPageButton = (PageTurnWidget)this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, (button) -> {
         this.goToPreviousPage();
      }, this.pageTurnSound));
      this.updatePageButtons();
   }

   private int getPageCount() {
      return this.contents.getPageCount();
   }

   protected void goToPreviousPage() {
      if (this.pageIndex > 0) {
         --this.pageIndex;
      }

      this.updatePageButtons();
   }

   protected void goToNextPage() {
      if (this.pageIndex < this.getPageCount() - 1) {
         ++this.pageIndex;
      }

      this.updatePageButtons();
   }

   private void updatePageButtons() {
      this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
      this.previousPageButton.visible = this.pageIndex > 0;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else {
         switch (keyCode) {
            case 266:
               this.previousPageButton.onPress();
               return true;
            case 267:
               this.nextPageButton.onPress();
               return true;
            default:
               return false;
         }
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      int i = (this.width - 192) / 2;
      int j = true;
      if (this.cachedPageIndex != this.pageIndex) {
         StringVisitable stringVisitable = this.contents.getPage(this.pageIndex);
         this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
         this.pageIndexText = this.getPageIndicatorText();
      }

      this.cachedPageIndex = this.pageIndex;
      int k = this.textRenderer.getWidth((StringVisitable)this.pageIndexText);
      context.drawText(this.textRenderer, (Text)this.pageIndexText, i - k + 192 - 44, 18, -16777216, false);
      Objects.requireNonNull(this.textRenderer);
      int l = Math.min(128 / 9, this.cachedPage.size());

      for(int m = 0; m < l; ++m) {
         OrderedText orderedText = (OrderedText)this.cachedPage.get(m);
         TextRenderer var10001 = this.textRenderer;
         int var10003 = i + 36;
         Objects.requireNonNull(this.textRenderer);
         context.drawText(var10001, orderedText, var10003, 32 + m * 9, -16777216, false);
      }

      Style style = this.getTextStyleAt((double)mouseX, (double)mouseY);
      if (style != null) {
         context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
      }

   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.renderInGameBackground(context);
      context.drawTexture(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0) {
         Style style = this.getTextStyleAt(mouseX, mouseY);
         if (style != null && this.handleTextClick(style)) {
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   protected void handleClickEvent(MinecraftClient client, ClickEvent clickEvent) {
      ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)Objects.requireNonNull(client.player, "Player not available");
      Objects.requireNonNull(clickEvent);
      byte var5 = 0;
      Throwable var13;
      boolean var10001;
      switch (clickEvent.typeSwitch<invokedynamic>(clickEvent, var5)) {
         case 0:
            ClickEvent.ChangePage var6 = (ClickEvent.ChangePage)clickEvent;
            ClickEvent.ChangePage var15 = var6;

            int var16;
            try {
               var16 = var15.page();
            } catch (Throwable var12) {
               var13 = var12;
               var10001 = false;
               break;
            }

            int var17 = var16;
            this.jumpToPage(var17 - 1);
            return;
         case 1:
            ClickEvent.RunCommand var8 = (ClickEvent.RunCommand)clickEvent;
            ClickEvent.RunCommand var10000 = var8;

            String var14;
            try {
               var14 = var10000.command();
            } catch (Throwable var11) {
               var13 = var11;
               var10001 = false;
               break;
            }

            String var10 = var14;
            this.method_72151();
            handleRunCommand(clientPlayerEntity, var10, (Screen)null);
            return;
         default:
            handleClickEvent(clickEvent, client, this);
            return;
      }

      Throwable var4 = var13;
      throw new MatchException(var4.toString(), var4);
   }

   protected void method_72151() {
   }

   @Nullable
   public Style getTextStyleAt(double x, double y) {
      if (this.cachedPage.isEmpty()) {
         return null;
      } else {
         int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
         int j = MathHelper.floor(y - 2.0 - 30.0);
         if (i >= 0 && j >= 0) {
            Objects.requireNonNull(this.textRenderer);
            int k = Math.min(128 / 9, this.cachedPage.size());
            if (i <= 114) {
               Objects.requireNonNull(this.client.textRenderer);
               if (j < 9 * k + k) {
                  Objects.requireNonNull(this.client.textRenderer);
                  int l = j / 9;
                  if (l >= 0 && l < this.cachedPage.size()) {
                     OrderedText orderedText = (OrderedText)this.cachedPage.get(l);
                     return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
                  }

                  return null;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Contents(List pages) {
      public Contents(List list) {
         this.pages = list;
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public Text getPage(int index) {
         return index >= 0 && index < this.getPageCount() ? (Text)this.pages.get(index) : ScreenTexts.EMPTY;
      }

      @Nullable
      public static Contents create(ItemStack stack) {
         boolean bl = MinecraftClient.getInstance().shouldFilterText();
         WrittenBookContentComponent writtenBookContentComponent = (WrittenBookContentComponent)stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
         if (writtenBookContentComponent != null) {
            return new Contents(writtenBookContentComponent.getPages(bl));
         } else {
            WritableBookContentComponent writableBookContentComponent = (WritableBookContentComponent)stack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
            return writableBookContentComponent != null ? new Contents(writableBookContentComponent.stream(bl).map(Text::literal).toList()) : null;
         }
      }

      public List pages() {
         return this.pages;
      }
   }
}
