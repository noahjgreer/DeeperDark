package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.client.gui.navigation.NavigationDirection;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface Element extends Navigable {
   long MAX_DOUBLE_CLICK_INTERVAL = 250L;

   default void mouseMoved(double mouseX, double mouseY) {
   }

   default boolean mouseClicked(double mouseX, double mouseY, int button) {
      return false;
   }

   default boolean mouseReleased(double mouseX, double mouseY, int button) {
      return false;
   }

   default boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      return false;
   }

   default boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return false;
   }

   default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   default boolean charTyped(char chr, int modifiers) {
      return false;
   }

   @Nullable
   default GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
      return null;
   }

   default boolean isMouseOver(double mouseX, double mouseY) {
      return false;
   }

   void setFocused(boolean focused);

   boolean isFocused();

   @Nullable
   default GuiNavigationPath getFocusedPath() {
      return this.isFocused() ? GuiNavigationPath.of(this) : null;
   }

   default ScreenRect getNavigationFocus() {
      return ScreenRect.empty();
   }

   default ScreenRect getBorder(NavigationDirection direction) {
      return this.getNavigationFocus().getBorder(direction);
   }
}
