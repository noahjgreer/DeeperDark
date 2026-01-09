package net.minecraft.client.gui;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

@Environment(EnvType.CLIENT)
public interface ParentElement extends Element {
   List children();

   default Optional hoveredElement(double mouseX, double mouseY) {
      Iterator var5 = this.children().iterator();

      Element element;
      do {
         if (!var5.hasNext()) {
            return Optional.empty();
         }

         element = (Element)var5.next();
      } while(!element.isMouseOver(mouseX, mouseY));

      return Optional.of(element);
   }

   default boolean mouseClicked(double mouseX, double mouseY, int button) {
      Optional optional = this.hoveredElement(mouseX, mouseY);
      if (optional.isEmpty()) {
         return false;
      } else {
         Element element = (Element)optional.get();
         if (element.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(element);
            if (button == 0) {
               this.setDragging(true);
            }
         }

         return true;
      }
   }

   default boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0 && this.isDragging()) {
         this.setDragging(false);
         if (this.getFocused() != null) {
            return this.getFocused().mouseReleased(mouseX, mouseY, button);
         }
      }

      return false;
   }

   default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return this.getFocused() != null && this.isDragging() && button == 0 ? this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY) : false;
   }

   boolean isDragging();

   void setDragging(boolean dragging);

   default boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.hoveredElement(mouseX, mouseY).filter((element) -> {
         return element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }).isPresent();
   }

   default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
   }

   default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
   }

   default boolean charTyped(char chr, int modifiers) {
      return this.getFocused() != null && this.getFocused().charTyped(chr, modifiers);
   }

   @Nullable
   Element getFocused();

   void setFocused(@Nullable Element focused);

   default void setFocused(boolean focused) {
   }

   default boolean isFocused() {
      return this.getFocused() != null;
   }

   @Nullable
   default GuiNavigationPath getFocusedPath() {
      Element element = this.getFocused();
      return element != null ? GuiNavigationPath.of(this, element.getFocusedPath()) : null;
   }

   @Nullable
   default GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
      Element element = this.getFocused();
      if (element != null) {
         GuiNavigationPath guiNavigationPath = element.getNavigationPath(navigation);
         if (guiNavigationPath != null) {
            return GuiNavigationPath.of(this, guiNavigationPath);
         }
      }

      if (navigation instanceof GuiNavigation.Tab tab) {
         return this.computeNavigationPath(tab);
      } else if (navigation instanceof GuiNavigation.Arrow arrow) {
         return this.computeNavigationPath(arrow);
      } else {
         return null;
      }
   }

   @Nullable
   private GuiNavigationPath computeNavigationPath(GuiNavigation.Tab navigation) {
      boolean bl = navigation.forward();
      Element element = this.getFocused();
      List list = new ArrayList(this.children());
      Collections.sort(list, Comparator.comparingInt((child) -> {
         return child.getNavigationOrder();
      }));
      int i = list.indexOf(element);
      int j;
      if (element != null && i >= 0) {
         j = i + (bl ? 1 : 0);
      } else if (bl) {
         j = 0;
      } else {
         j = list.size();
      }

      ListIterator listIterator = list.listIterator(j);
      BooleanSupplier var10000;
      if (bl) {
         Objects.requireNonNull(listIterator);
         var10000 = listIterator::hasNext;
      } else {
         Objects.requireNonNull(listIterator);
         var10000 = listIterator::hasPrevious;
      }

      BooleanSupplier booleanSupplier = var10000;
      Supplier var12;
      if (bl) {
         Objects.requireNonNull(listIterator);
         var12 = listIterator::next;
      } else {
         Objects.requireNonNull(listIterator);
         var12 = listIterator::previous;
      }

      Supplier supplier = var12;

      GuiNavigationPath guiNavigationPath;
      do {
         if (!booleanSupplier.getAsBoolean()) {
            return null;
         }

         Element element2 = (Element)supplier.get();
         guiNavigationPath = element2.getNavigationPath(navigation);
      } while(guiNavigationPath == null);

      return GuiNavigationPath.of(this, guiNavigationPath);
   }

   @Nullable
   private GuiNavigationPath computeNavigationPath(GuiNavigation.Arrow navigation) {
      Element element = this.getFocused();
      if (element == null) {
         NavigationDirection navigationDirection = navigation.direction();
         ScreenRect screenRect = this.getBorder(navigationDirection.getOpposite());
         return GuiNavigationPath.of(this, this.computeChildPath(screenRect, navigationDirection, (Element)null, navigation));
      } else {
         ScreenRect screenRect2 = element.getNavigationFocus();
         return GuiNavigationPath.of(this, this.computeChildPath(screenRect2, navigation.direction(), element, navigation));
      }
   }

   @Nullable
   private GuiNavigationPath computeChildPath(ScreenRect focus, NavigationDirection direction, @Nullable Element focused, GuiNavigation navigation) {
      NavigationAxis navigationAxis = direction.getAxis();
      NavigationAxis navigationAxis2 = navigationAxis.getOther();
      NavigationDirection navigationDirection = navigationAxis2.getPositiveDirection();
      int i = focus.getBoundingCoordinate(direction.getOpposite());
      List list = new ArrayList();
      Iterator var10 = this.children().iterator();

      while(var10.hasNext()) {
         Element element = (Element)var10.next();
         if (element != focused) {
            ScreenRect screenRect = element.getNavigationFocus();
            if (screenRect.overlaps(focus, navigationAxis2)) {
               int j = screenRect.getBoundingCoordinate(direction.getOpposite());
               if (direction.isAfter(j, i)) {
                  list.add(element);
               } else if (j == i && direction.isAfter(screenRect.getBoundingCoordinate(direction), focus.getBoundingCoordinate(direction))) {
                  list.add(element);
               }
            }
         }
      }

      Comparator comparator = Comparator.comparing((elementx) -> {
         return elementx.getNavigationFocus().getBoundingCoordinate(direction.getOpposite());
      }, direction.getComparator());
      Comparator comparator2 = Comparator.comparing((elementx) -> {
         return elementx.getNavigationFocus().getBoundingCoordinate(navigationDirection.getOpposite());
      }, navigationDirection.getComparator());
      list.sort(comparator.thenComparing(comparator2));
      Iterator var17 = list.iterator();

      GuiNavigationPath guiNavigationPath;
      do {
         if (!var17.hasNext()) {
            return this.computeInitialChildPath(focus, direction, focused, navigation);
         }

         Element element2 = (Element)var17.next();
         guiNavigationPath = element2.getNavigationPath(navigation);
      } while(guiNavigationPath == null);

      return guiNavigationPath;
   }

   @Nullable
   private GuiNavigationPath computeInitialChildPath(ScreenRect focus, NavigationDirection direction, @Nullable Element focused, GuiNavigation navigation) {
      NavigationAxis navigationAxis = direction.getAxis();
      NavigationAxis navigationAxis2 = navigationAxis.getOther();
      List list = new ArrayList();
      ScreenPos screenPos = ScreenPos.of(navigationAxis, focus.getBoundingCoordinate(direction), focus.getCenter(navigationAxis2));
      Iterator var9 = this.children().iterator();

      while(var9.hasNext()) {
         Element element = (Element)var9.next();
         if (element != focused) {
            ScreenRect screenRect = element.getNavigationFocus();
            ScreenPos screenPos2 = ScreenPos.of(navigationAxis, screenRect.getBoundingCoordinate(direction.getOpposite()), screenRect.getCenter(navigationAxis2));
            if (direction.isAfter(screenPos2.getComponent(navigationAxis), screenPos.getComponent(navigationAxis))) {
               long l = Vector2i.distanceSquared(screenPos.x(), screenPos.y(), screenPos2.x(), screenPos2.y());
               list.add(Pair.of(element, l));
            }
         }
      }

      list.sort(Comparator.comparingDouble(Pair::getSecond));
      var9 = list.iterator();

      GuiNavigationPath guiNavigationPath;
      do {
         if (!var9.hasNext()) {
            return null;
         }

         Pair pair = (Pair)var9.next();
         guiNavigationPath = ((Element)pair.getFirst()).getNavigationPath(navigation);
      } while(guiNavigationPath == null);

      return guiNavigationPath;
   }
}
