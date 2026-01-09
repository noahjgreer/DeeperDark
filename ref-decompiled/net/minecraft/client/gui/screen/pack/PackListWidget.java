package net.minecraft.client.gui.screen.pack;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

@Environment(EnvType.CLIENT)
public class PackListWidget extends AlwaysSelectedEntryListWidget {
   static final Identifier SELECT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/select_highlighted");
   static final Identifier SELECT_TEXTURE = Identifier.ofVanilla("transferable_list/select");
   static final Identifier UNSELECT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/unselect_highlighted");
   static final Identifier UNSELECT_TEXTURE = Identifier.ofVanilla("transferable_list/unselect");
   static final Identifier MOVE_UP_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_up_highlighted");
   static final Identifier MOVE_UP_TEXTURE = Identifier.ofVanilla("transferable_list/move_up");
   static final Identifier MOVE_DOWN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("transferable_list/move_down_highlighted");
   static final Identifier MOVE_DOWN_TEXTURE = Identifier.ofVanilla("transferable_list/move_down");
   static final Text INCOMPATIBLE = Text.translatable("pack.incompatible");
   static final Text INCOMPATIBLE_CONFIRM = Text.translatable("pack.incompatible.confirm.title");
   private final Text title;
   final PackScreen screen;

   public PackListWidget(MinecraftClient client, PackScreen screen, int width, int height, Text title) {
      Objects.requireNonNull(client.textRenderer);
      super(client, width, height, 33, 36, (int)(9.0F * 1.5F));
      this.screen = screen;
      this.title = title;
      this.centerListVertically = false;
   }

   protected void renderHeader(DrawContext context, int x, int y) {
      Text text = Text.empty().append(this.title).formatted(Formatting.UNDERLINE, Formatting.BOLD);
      context.drawTextWithShadow(this.client.textRenderer, (Text)text, x + this.width / 2 - this.client.textRenderer.getWidth((StringVisitable)text) / 2, Math.min(this.getY() + 3, y), -1);
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarX() {
      return this.getRight() - 6;
   }

   protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
      if (this.overflows()) {
         int i = true;
         int j = this.getRowLeft() - 2;
         int k = this.getRight() - 6 - 1;
         int l = y - 2;
         int m = y + entryHeight + 2;
         context.fill(j, l, k, m, borderColor);
         context.fill(j + 1, l + 1, k - 1, m - 1, fillColor);
      } else {
         super.drawSelectionHighlight(context, y, entryWidth, entryHeight, borderColor, fillColor);
      }

   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.getSelectedOrNull() != null) {
         switch (keyCode) {
            case 32:
            case 257:
               ((ResourcePackEntry)this.getSelectedOrNull()).toggle();
               return true;
            default:
               if (Screen.hasShiftDown()) {
                  switch (keyCode) {
                     case 264:
                        ((ResourcePackEntry)this.getSelectedOrNull()).moveTowardEnd();
                        return true;
                     case 265:
                        ((ResourcePackEntry)this.getSelectedOrNull()).moveTowardStart();
                        return true;
                  }
               }
         }
      }

      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   @Environment(EnvType.CLIENT)
   public static class ResourcePackEntry extends AlwaysSelectedEntryListWidget.Entry {
      private static final int field_32403 = 157;
      private static final int field_32404 = 157;
      private static final String ELLIPSIS = "...";
      private final PackListWidget widget;
      protected final MinecraftClient client;
      private final ResourcePackOrganizer.Pack pack;
      private final OrderedText displayName;
      private final MultilineText description;
      private final OrderedText incompatibleText;
      private final MultilineText compatibilityNotificationText;

      public ResourcePackEntry(MinecraftClient client, PackListWidget widget, ResourcePackOrganizer.Pack pack) {
         this.client = client;
         this.pack = pack;
         this.widget = widget;
         this.displayName = trimTextToWidth(client, pack.getDisplayName());
         this.description = createMultilineText(client, pack.getDecoratedDescription());
         this.incompatibleText = trimTextToWidth(client, PackListWidget.INCOMPATIBLE);
         this.compatibilityNotificationText = createMultilineText(client, pack.getCompatibility().getNotification());
      }

      private static OrderedText trimTextToWidth(MinecraftClient client, Text text) {
         int i = client.textRenderer.getWidth((StringVisitable)text);
         if (i > 157) {
            StringVisitable stringVisitable = StringVisitable.concat(client.textRenderer.trimToWidth((StringVisitable)text, 157 - client.textRenderer.getWidth("...")), StringVisitable.plain("..."));
            return Language.getInstance().reorder(stringVisitable);
         } else {
            return text.asOrderedText();
         }
      }

      private static MultilineText createMultilineText(MinecraftClient client, Text text) {
         return MultilineText.create(client.textRenderer, 157, 2, text);
      }

      public Text getNarration() {
         return Text.translatable("narrator.select", this.pack.getDisplayName());
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         ResourcePackCompatibility resourcePackCompatibility = this.pack.getCompatibility();
         if (!resourcePackCompatibility.isCompatible()) {
            int i = x + entryWidth - 3 - (this.widget.overflows() ? 7 : 0);
            context.fill(x - 1, y - 1, i, y + entryHeight + 1, -8978432);
         }

         context.drawTexture(RenderPipelines.GUI_TEXTURED, this.pack.getIconId(), x, y, 0.0F, 0.0F, 32, 32, 32, 32);
         OrderedText orderedText = this.displayName;
         MultilineText multilineText = this.description;
         if (this.isSelectable() && ((Boolean)this.client.options.getTouchscreen().getValue() || hovered || this.widget.getSelectedOrNull() == this && this.widget.isFocused())) {
            context.fill(x, y, x + 32, y + 32, -1601138544);
            int j = mouseX - x;
            int k = mouseY - y;
            if (!this.pack.getCompatibility().isCompatible()) {
               orderedText = this.incompatibleText;
               multilineText = this.compatibilityNotificationText;
            }

            if (this.pack.canBeEnabled()) {
               if (j < 32) {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.SELECT_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
               } else {
                  context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.SELECT_TEXTURE, x, y, 32, 32);
               }
            } else {
               if (this.pack.canBeDisabled()) {
                  if (j < 16) {
                     context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.UNSELECT_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                  } else {
                     context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.UNSELECT_TEXTURE, x, y, 32, 32);
                  }
               }

               if (this.pack.canMoveTowardStart()) {
                  if (j < 32 && j > 16 && k < 16) {
                     context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.MOVE_UP_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                  } else {
                     context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.MOVE_UP_TEXTURE, x, y, 32, 32);
                  }
               }

               if (this.pack.canMoveTowardEnd()) {
                  if (j < 32 && j > 16 && k > 16) {
                     context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.MOVE_DOWN_HIGHLIGHTED_TEXTURE, x, y, 32, 32);
                  } else {
                     context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PackListWidget.MOVE_DOWN_TEXTURE, x, y, 32, 32);
                  }
               }
            }
         }

         context.drawTextWithShadow(this.client.textRenderer, (OrderedText)orderedText, x + 32 + 2, y + 1, -1);
         multilineText.drawWithShadow(context, x + 32 + 2, y + 12, 10, -8355712);
      }

      public String getName() {
         return this.pack.getName();
      }

      private boolean isSelectable() {
         return !this.pack.isPinned() || !this.pack.isAlwaysEnabled();
      }

      public void toggle() {
         if (this.pack.canBeEnabled() && this.enable()) {
            this.widget.screen.switchFocusedList(this.widget);
         } else if (this.pack.canBeDisabled()) {
            this.pack.disable();
            this.widget.screen.switchFocusedList(this.widget);
         }

      }

      void moveTowardStart() {
         if (this.pack.canMoveTowardStart()) {
            this.pack.moveTowardStart();
         }

      }

      void moveTowardEnd() {
         if (this.pack.canMoveTowardEnd()) {
            this.pack.moveTowardEnd();
         }

      }

      private boolean enable() {
         if (this.pack.getCompatibility().isCompatible()) {
            this.pack.enable();
            return true;
         } else {
            Text text = this.pack.getCompatibility().getConfirmMessage();
            this.client.setScreen(new ConfirmScreen((confirmed) -> {
               this.client.setScreen(this.widget.screen);
               if (confirmed) {
                  this.pack.enable();
               }

            }, PackListWidget.INCOMPATIBLE_CONFIRM, text));
            return false;
         }
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         double d = mouseX - (double)this.widget.getRowLeft();
         double e = mouseY - (double)this.widget.getRowTop(this.widget.children().indexOf(this));
         if (this.isSelectable() && d <= 32.0) {
            this.widget.screen.clearSelection();
            if (this.pack.canBeEnabled()) {
               this.enable();
               return true;
            }

            if (d < 16.0 && this.pack.canBeDisabled()) {
               this.pack.disable();
               return true;
            }

            if (d > 16.0 && e < 16.0 && this.pack.canMoveTowardStart()) {
               this.pack.moveTowardStart();
               return true;
            }

            if (d > 16.0 && e > 16.0 && this.pack.canMoveTowardEnd()) {
               this.pack.moveTowardEnd();
               return true;
            }
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }
}
