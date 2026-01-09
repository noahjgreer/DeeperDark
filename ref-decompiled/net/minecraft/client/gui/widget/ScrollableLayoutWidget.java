package net.minecraft.client.gui.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.screen.ScreenTexts;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ScrollableLayoutWidget implements LayoutWidget {
   private static final int field_60715 = 4;
   private static final int field_61059 = 10;
   final LayoutWidget layout;
   private final Container container;
   private int width;
   private int height;

   public ScrollableLayoutWidget(MinecraftClient client, LayoutWidget layout, int height) {
      this.layout = layout;
      this.container = new Container(client, 0, height);
   }

   public void setWidth(int width) {
      this.width = width;
      this.container.setWidth(Math.max(this.layout.getWidth(), width));
   }

   public void setHeight(int height) {
      this.height = height;
      this.container.setHeight(Math.min(this.layout.getHeight(), height));
      this.container.refreshScroll();
   }

   public void refreshPositions() {
      this.layout.refreshPositions();
      int i = this.layout.getWidth();
      this.container.setWidth(Math.max(i + 20, this.width));
      this.container.setHeight(Math.min(this.layout.getHeight(), this.height));
      this.container.refreshScroll();
   }

   public void forEachElement(Consumer consumer) {
      consumer.accept(this.container);
   }

   public void setX(int x) {
      this.container.setX(x);
   }

   public void setY(int y) {
      this.container.setY(y);
   }

   public int getX() {
      return this.container.getX();
   }

   public int getY() {
      return this.container.getY();
   }

   public int getWidth() {
      return this.container.getWidth();
   }

   public int getHeight() {
      return this.container.getHeight();
   }

   @Environment(EnvType.CLIENT)
   private class Container extends ContainerWidget {
      private final MinecraftClient client;
      private final List children = new ArrayList();

      public Container(final MinecraftClient client, final int width, final int height) {
         super(0, 0, width, height, ScreenTexts.EMPTY);
         this.client = client;
         LayoutWidget var10000 = ScrollableLayoutWidget.this.layout;
         List var10001 = this.children;
         Objects.requireNonNull(var10001);
         var10000.forEachChild(var10001::add);
      }

      protected int getContentsHeightWithPadding() {
         return ScrollableLayoutWidget.this.layout.getHeight();
      }

      protected double getDeltaYPerScroll() {
         return 10.0;
      }

      protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         context.enableScissor(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height);
         Iterator var5 = this.children.iterator();

         while(var5.hasNext()) {
            ClickableWidget clickableWidget = (ClickableWidget)var5.next();
            clickableWidget.render(context, mouseX, mouseY, deltaTicks);
         }

         context.disableScissor();
         this.drawScrollbar(context);
      }

      protected void appendClickableNarrations(NarrationMessageBuilder builder) {
      }

      public ScreenRect getBorder(NavigationDirection direction) {
         return new ScreenRect(this.getX(), this.getY(), this.width, this.getContentsHeightWithPadding());
      }

      public void setFocused(@Nullable Element focused) {
         super.setFocused(focused);
         if (focused != null && this.client.getNavigationType().isKeyboard()) {
            ScreenRect screenRect = this.getNavigationFocus();
            ScreenRect screenRect2 = focused.getNavigationFocus();
            int i = screenRect2.getTop() - screenRect.getTop();
            int j = screenRect2.getBottom() - screenRect.getBottom();
            if (i < 0) {
               this.setScrollY(this.getScrollY() + (double)i - 14.0);
            } else if (j > 0) {
               this.setScrollY(this.getScrollY() + (double)j + 14.0);
            }

         }
      }

      public void setX(int x) {
         super.setX(x);
         ScrollableLayoutWidget.this.layout.setX(x + 10);
      }

      public void setY(int y) {
         super.setY(y);
         ScrollableLayoutWidget.this.layout.setY(y - (int)this.getScrollY());
      }

      public void setScrollY(double scrollY) {
         super.setScrollY(scrollY);
         ScrollableLayoutWidget.this.layout.setY(this.getNavigationFocus().getTop() - (int)this.getScrollY());
      }

      public List children() {
         return this.children;
      }

      public Collection getNarratedParts() {
         return this.children;
      }
   }
}
