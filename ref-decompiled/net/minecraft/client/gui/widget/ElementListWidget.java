package net.minecraft.client.gui.widget;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class ElementListWidget extends EntryListWidget {
   public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l) {
      super(minecraftClient, i, j, k, l);
   }

   public ElementListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
      super(minecraftClient, i, j, k, l, m);
   }

   @Nullable
   public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
      if (this.getEntryCount() == 0) {
         return null;
      } else if (!(navigation instanceof GuiNavigation.Arrow)) {
         return super.getNavigationPath(navigation);
      } else {
         GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
         Entry entry = (Entry)this.getFocused();
         if (arrow.direction().getAxis() == NavigationAxis.HORIZONTAL && entry != null) {
            return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)entry.getNavigationPath(navigation));
         } else {
            int i = -1;
            NavigationDirection navigationDirection = arrow.direction();
            if (entry != null) {
               i = entry.children().indexOf(entry.getFocused());
            }

            if (i == -1) {
               switch (navigationDirection) {
                  case LEFT:
                     i = Integer.MAX_VALUE;
                     navigationDirection = NavigationDirection.DOWN;
                     break;
                  case RIGHT:
                     i = 0;
                     navigationDirection = NavigationDirection.DOWN;
                     break;
                  default:
                     i = 0;
               }
            }

            Entry entry2 = entry;

            GuiNavigationPath guiNavigationPath;
            do {
               entry2 = (Entry)this.getNeighboringEntry(navigationDirection, (element) -> {
                  return !element.children().isEmpty();
               }, entry2);
               if (entry2 == null) {
                  return null;
               }

               guiNavigationPath = entry2.getNavigationPath(arrow, i);
            } while(guiNavigationPath == null);

            return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)guiNavigationPath);
         }
      }
   }

   public void setFocused(@Nullable Element focused) {
      if (this.getFocused() != focused) {
         super.setFocused(focused);
         if (focused == null) {
            this.setSelected((EntryListWidget.Entry)null);
         }

      }
   }

   public Selectable.SelectionType getType() {
      return this.isFocused() ? Selectable.SelectionType.FOCUSED : super.getType();
   }

   protected boolean isSelectedEntry(int index) {
      return false;
   }

   public void appendClickableNarrations(NarrationMessageBuilder builder) {
      Entry entry = (Entry)this.getHoveredEntry();
      if (entry != null) {
         entry.appendNarrations(builder.nextMessage());
         this.appendNarrations(builder, entry);
      } else {
         Entry entry2 = (Entry)this.getFocused();
         if (entry2 != null) {
            entry2.appendNarrations(builder.nextMessage());
            this.appendNarrations(builder, entry2);
         }
      }

      builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.component_list.usage"));
   }

   @Environment(EnvType.CLIENT)
   public abstract static class Entry extends EntryListWidget.Entry implements ParentElement {
      @Nullable
      private Element focused;
      @Nullable
      private Selectable focusedSelectable;
      private boolean dragging;

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(boolean dragging) {
         this.dragging = dragging;
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         return ParentElement.super.mouseClicked(mouseX, mouseY, button);
      }

      public void setFocused(@Nullable Element focused) {
         if (this.focused != null) {
            this.focused.setFocused(false);
         }

         if (focused != null) {
            focused.setFocused(true);
         }

         this.focused = focused;
      }

      @Nullable
      public Element getFocused() {
         return this.focused;
      }

      @Nullable
      public GuiNavigationPath getNavigationPath(GuiNavigation navigation, int index) {
         if (this.children().isEmpty()) {
            return null;
         } else {
            GuiNavigationPath guiNavigationPath = ((Element)this.children().get(Math.min(index, this.children().size() - 1))).getNavigationPath(navigation);
            return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)guiNavigationPath);
         }
      }

      @Nullable
      public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
         if (navigation instanceof GuiNavigation.Arrow) {
            GuiNavigation.Arrow arrow = (GuiNavigation.Arrow)navigation;
            byte var10000;
            switch (arrow.direction()) {
               case LEFT:
                  var10000 = -1;
                  break;
               case RIGHT:
                  var10000 = 1;
                  break;
               case UP:
               case DOWN:
                  var10000 = 0;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            int i = var10000;
            if (i == 0) {
               return null;
            }

            int j = MathHelper.clamp(i + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

            for(int k = j; k >= 0 && k < this.children().size(); k += i) {
               Element element = (Element)this.children().get(k);
               GuiNavigationPath guiNavigationPath = element.getNavigationPath(navigation);
               if (guiNavigationPath != null) {
                  return GuiNavigationPath.of((ParentElement)this, (GuiNavigationPath)guiNavigationPath);
               }
            }
         }

         return ParentElement.super.getNavigationPath(navigation);
      }

      public abstract List selectableChildren();

      void appendNarrations(NarrationMessageBuilder builder) {
         List list = this.selectableChildren();
         Screen.SelectedElementNarrationData selectedElementNarrationData = Screen.findSelectedElementData(list, this.focusedSelectable);
         if (selectedElementNarrationData != null) {
            if (selectedElementNarrationData.selectType.isFocused()) {
               this.focusedSelectable = selectedElementNarrationData.selectable;
            }

            if (list.size() > 1) {
               builder.put(NarrationPart.POSITION, (Text)Text.translatable("narrator.position.object_list", selectedElementNarrationData.index + 1, list.size()));
               if (selectedElementNarrationData.selectType == Selectable.SelectionType.FOCUSED) {
                  builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.component_list.usage"));
               }
            }

            selectedElementNarrationData.selectable.appendNarrations(builder.nextMessage());
         }

      }
   }
}
