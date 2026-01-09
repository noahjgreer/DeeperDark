package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class EntryListWidget extends ContainerWidget {
   private static final Identifier MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/menu_list_background.png");
   private static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");
   protected final MinecraftClient client;
   protected final int itemHeight;
   private final List children;
   protected boolean centerListVertically;
   private boolean renderHeader;
   protected int headerHeight;
   @Nullable
   private Entry selected;
   @Nullable
   private Entry hoveredEntry;

   public EntryListWidget(MinecraftClient client, int width, int height, int y, int itemHeight) {
      super(0, y, width, height, ScreenTexts.EMPTY);
      this.children = new Entries();
      this.centerListVertically = true;
      this.client = client;
      this.itemHeight = itemHeight;
   }

   public EntryListWidget(MinecraftClient client, int width, int height, int y, int itemHeight, int headerHeight) {
      this(client, width, height, y, itemHeight);
      this.renderHeader = true;
      this.headerHeight = headerHeight;
   }

   @Nullable
   public Entry getSelectedOrNull() {
      return this.selected;
   }

   public void setSelected(int index) {
      if (index == -1) {
         this.setSelected((Entry)null);
      } else if (this.getEntryCount() != 0) {
         this.setSelected(this.getEntry(index));
      }

   }

   public void setSelected(@Nullable Entry entry) {
      this.selected = entry;
   }

   public Entry getFirst() {
      return (Entry)this.children.get(0);
   }

   @Nullable
   public Entry getFocused() {
      return (Entry)super.getFocused();
   }

   public final List children() {
      return this.children;
   }

   protected void clearEntries() {
      this.children.clear();
      this.selected = null;
   }

   public void replaceEntries(Collection newEntries) {
      this.clearEntries();
      this.children.addAll(newEntries);
   }

   protected Entry getEntry(int index) {
      return (Entry)this.children().get(index);
   }

   protected int addEntry(Entry entry) {
      this.children.add(entry);
      return this.children.size() - 1;
   }

   protected void addEntryToTop(Entry entry) {
      double d = (double)this.getMaxScrollY() - this.getScrollY();
      this.children.add(0, entry);
      this.setScrollY((double)this.getMaxScrollY() - d);
   }

   protected boolean removeEntryWithoutScrolling(Entry entry) {
      double d = (double)this.getMaxScrollY() - this.getScrollY();
      boolean bl = this.removeEntry(entry);
      this.setScrollY((double)this.getMaxScrollY() - d);
      return bl;
   }

   protected int getEntryCount() {
      return this.children().size();
   }

   protected boolean isSelectedEntry(int index) {
      return Objects.equals(this.getSelectedOrNull(), this.children().get(index));
   }

   @Nullable
   protected final Entry getEntryAtPosition(double x, double y) {
      int i = this.getRowWidth() / 2;
      int j = this.getX() + this.width / 2;
      int k = j - i;
      int l = j + i;
      int m = MathHelper.floor(y - (double)this.getY()) - this.headerHeight + (int)this.getScrollY() - 4;
      int n = m / this.itemHeight;
      return x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount() ? (Entry)this.children().get(n) : null;
   }

   public void position(int width, ThreePartsLayoutWidget layout) {
      this.position(width, layout.getContentHeight(), layout.getHeaderHeight());
   }

   public void position(int width, int height, int y) {
      this.setDimensions(width, height);
      this.setPosition(0, y);
      this.refreshScroll();
   }

   protected int getContentsHeightWithPadding() {
      return this.getEntryCount() * this.itemHeight + this.headerHeight + 4;
   }

   protected void renderHeader(DrawContext context, int x, int y) {
   }

   protected void renderDecorations(DrawContext context, int mouseX, int mouseY) {
   }

   public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.hoveredEntry = this.isMouseOver((double)mouseX, (double)mouseY) ? this.getEntryAtPosition((double)mouseX, (double)mouseY) : null;
      this.drawMenuListBackground(context);
      this.enableScissor(context);
      if (this.renderHeader) {
         int i = this.getRowLeft();
         int j = this.getY() + 4 - (int)this.getScrollY();
         this.renderHeader(context, i, j);
      }

      this.renderList(context, mouseX, mouseY, deltaTicks);
      context.disableScissor();
      this.drawHeaderAndFooterSeparators(context);
      this.drawScrollbar(context);
      this.renderDecorations(context, mouseX, mouseY);
   }

   protected void drawHeaderAndFooterSeparators(DrawContext context) {
      Identifier identifier = this.client.world == null ? Screen.HEADER_SEPARATOR_TEXTURE : Screen.INWORLD_HEADER_SEPARATOR_TEXTURE;
      Identifier identifier2 = this.client.world == null ? Screen.FOOTER_SEPARATOR_TEXTURE : Screen.INWORLD_FOOTER_SEPARATOR_TEXTURE;
      context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
      context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier2, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
   }

   protected void drawMenuListBackground(DrawContext context) {
      Identifier identifier = this.client.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
      context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), (float)this.getRight(), (float)(this.getBottom() + (int)this.getScrollY()), this.getWidth(), this.getHeight(), 32, 32);
   }

   protected void enableScissor(DrawContext context) {
      context.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
   }

   protected void centerScrollOn(Entry entry) {
      this.setScrollY((double)(this.children().indexOf(entry) * this.itemHeight + this.itemHeight / 2 - this.height / 2));
   }

   protected void ensureVisible(Entry entry) {
      int i = this.getRowTop(this.children().indexOf(entry));
      int j = i - this.getY() - 4 - this.itemHeight;
      if (j < 0) {
         this.scroll(j);
      }

      int k = this.getBottom() - i - this.itemHeight - this.itemHeight;
      if (k < 0) {
         this.scroll(-k);
      }

   }

   private void scroll(int amount) {
      this.setScrollY(this.getScrollY() + (double)amount);
   }

   protected double getDeltaYPerScroll() {
      return (double)this.itemHeight / 2.0;
   }

   protected int getScrollbarX() {
      return this.getRowRight() + 6 + 2;
   }

   public Optional hoveredElement(double mouseX, double mouseY) {
      return Optional.ofNullable(this.getEntryAtPosition(mouseX, mouseY));
   }

   public void setFocused(@Nullable Element focused) {
      Entry entry = this.getFocused();
      if (entry != focused && entry instanceof ParentElement parentElement) {
         parentElement.setFocused((Element)null);
      }

      super.setFocused(focused);
      int i = this.children.indexOf(focused);
      if (i >= 0) {
         Entry entry2 = (Entry)this.children.get(i);
         this.setSelected(entry2);
         if (this.client.getNavigationType().isKeyboard()) {
            this.ensureVisible(entry2);
         }
      }

   }

   @Nullable
   protected Entry getNeighboringEntry(NavigationDirection direction) {
      return this.getNeighboringEntry(direction, (entry) -> {
         return true;
      });
   }

   @Nullable
   protected Entry getNeighboringEntry(NavigationDirection direction, Predicate predicate) {
      return this.getNeighboringEntry(direction, predicate, this.getSelectedOrNull());
   }

   @Nullable
   protected Entry getNeighboringEntry(NavigationDirection direction, Predicate predicate, @Nullable Entry selected) {
      byte var10000;
      switch (direction) {
         case RIGHT:
         case LEFT:
            var10000 = 0;
            break;
         case UP:
            var10000 = -1;
            break;
         case DOWN:
            var10000 = 1;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      int i = var10000;
      if (!this.children().isEmpty() && i != 0) {
         int j;
         if (selected == null) {
            j = i > 0 ? 0 : this.children().size() - 1;
         } else {
            j = this.children().indexOf(selected) + i;
         }

         for(int k = j; k >= 0 && k < this.children.size(); k += i) {
            Entry entry = (Entry)this.children().get(k);
            if (predicate.test(entry)) {
               return entry;
            }
         }
      }

      return null;
   }

   protected void renderList(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      int i = this.getRowLeft();
      int j = this.getRowWidth();
      int k = this.itemHeight - 4;
      int l = this.getEntryCount();

      for(int m = 0; m < l; ++m) {
         int n = this.getRowTop(m);
         int o = this.getRowBottom(m);
         if (o >= this.getY() && n <= this.getBottom()) {
            this.renderEntry(context, mouseX, mouseY, deltaTicks, m, i, n, j, k);
         }
      }

   }

   protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
      Entry entry = this.getEntry(index);
      entry.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
      if (this.isSelectedEntry(index)) {
         int i = this.isFocused() ? -1 : -8355712;
         this.drawSelectionHighlight(context, y, entryWidth, entryHeight, i, -16777216);
      }

      entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
   }

   protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
      int i = this.getX() + (this.width - entryWidth) / 2;
      int j = this.getX() + (this.width + entryWidth) / 2;
      context.fill(i, y - 2, j, y + entryHeight + 2, borderColor);
      context.fill(i + 1, y - 1, j - 1, y + entryHeight + 1, fillColor);
   }

   public int getRowLeft() {
      return this.getX() + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   public int getRowTop(int index) {
      return this.getY() + 4 - (int)this.getScrollY() + index * this.itemHeight + this.headerHeight;
   }

   public int getRowBottom(int index) {
      return this.getRowTop(index) + this.itemHeight;
   }

   public int getRowWidth() {
      return 220;
   }

   public Selectable.SelectionType getType() {
      if (this.isFocused()) {
         return Selectable.SelectionType.FOCUSED;
      } else {
         return this.hoveredEntry != null ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
      }
   }

   @Nullable
   protected Entry remove(int index) {
      Entry entry = (Entry)this.children.get(index);
      return this.removeEntry((Entry)this.children.get(index)) ? entry : null;
   }

   protected boolean removeEntry(Entry entry) {
      boolean bl = this.children.remove(entry);
      if (bl && entry == this.getSelectedOrNull()) {
         this.setSelected((Entry)null);
      }

      return bl;
   }

   @Nullable
   protected Entry getHoveredEntry() {
      return this.hoveredEntry;
   }

   void setEntryParentList(Entry entry) {
      entry.parentList = this;
   }

   protected void appendNarrations(NarrationMessageBuilder builder, Entry entry) {
      List list = this.children();
      if (list.size() > 1) {
         int i = list.indexOf(entry);
         if (i != -1) {
            builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.list", i + 1, list.size()));
         }
      }

   }

   // $FF: synthetic method
   @Nullable
   public Element getFocused() {
      return this.getFocused();
   }

   @Environment(EnvType.CLIENT)
   private class Entries extends AbstractList {
      private final List entries = Lists.newArrayList();

      Entries() {
      }

      public Entry get(int i) {
         return (Entry)this.entries.get(i);
      }

      public int size() {
         return this.entries.size();
      }

      public Entry set(int i, Entry entry) {
         Entry entry2 = (Entry)this.entries.set(i, entry);
         EntryListWidget.this.setEntryParentList(entry);
         return entry2;
      }

      public void add(int i, Entry entry) {
         this.entries.add(i, entry);
         EntryListWidget.this.setEntryParentList(entry);
      }

      public Entry remove(int i) {
         return (Entry)this.entries.remove(i);
      }

      // $FF: synthetic method
      public Object remove(final int index) {
         return this.remove(index);
      }

      // $FF: synthetic method
      public void add(final int index, final Object entry) {
         this.add(index, (Entry)entry);
      }

      // $FF: synthetic method
      public Object set(final int index, final Object entry) {
         return this.set(index, (Entry)entry);
      }

      // $FF: synthetic method
      public Object get(final int index) {
         return this.get(index);
      }
   }

   @Environment(EnvType.CLIENT)
   protected abstract static class Entry implements Element {
      /** @deprecated */
      @Deprecated
      EntryListWidget parentList;

      public void setFocused(boolean focused) {
      }

      public boolean isFocused() {
         return this.parentList.getFocused() == this;
      }

      public abstract void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress);

      public void drawBorder(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
      }

      public boolean isMouseOver(double mouseX, double mouseY) {
         return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
      }
   }
}
