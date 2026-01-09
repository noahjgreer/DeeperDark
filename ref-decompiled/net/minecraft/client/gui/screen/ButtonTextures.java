package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ButtonTextures(Identifier enabled, Identifier disabled, Identifier enabledFocused, Identifier disabledFocused) {
   public ButtonTextures(Identifier unfocused, Identifier focused) {
      this(unfocused, unfocused, focused, focused);
   }

   public ButtonTextures(Identifier enabled, Identifier disabled, Identifier focused) {
      this(enabled, disabled, focused, disabled);
   }

   public ButtonTextures(Identifier identifier, Identifier identifier2, Identifier identifier3, Identifier identifier4) {
      this.enabled = identifier;
      this.disabled = identifier2;
      this.enabledFocused = identifier3;
      this.disabledFocused = identifier4;
   }

   public Identifier get(boolean enabled, boolean focused) {
      if (enabled) {
         return focused ? this.enabledFocused : this.enabled;
      } else {
         return focused ? this.disabledFocused : this.disabled;
      }
   }

   public Identifier enabled() {
      return this.enabled;
   }

   public Identifier disabled() {
      return this.disabled;
   }

   public Identifier enabledFocused() {
      return this.enabledFocused;
   }

   public Identifier disabledFocused() {
      return this.disabledFocused;
   }
}
