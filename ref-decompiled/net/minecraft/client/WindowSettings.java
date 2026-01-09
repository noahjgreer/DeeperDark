package net.minecraft.client;

import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record WindowSettings(int width, int height, OptionalInt fullscreenWidth, OptionalInt fullscreenHeight, boolean fullscreen) {
   public WindowSettings(int width, int height, OptionalInt fullscreenWidth, OptionalInt fullscreenHeight, boolean fullscreen) {
      this.width = width;
      this.height = height;
      this.fullscreenWidth = fullscreenWidth;
      this.fullscreenHeight = fullscreenHeight;
      this.fullscreen = fullscreen;
   }

   public WindowSettings withDimensions(int width, int height) {
      return new WindowSettings(width, height, this.fullscreenWidth, this.fullscreenHeight, this.fullscreen);
   }

   public WindowSettings withFullscreen(boolean fullscreen) {
      return new WindowSettings(this.width, this.height, this.fullscreenWidth, this.fullscreenHeight, fullscreen);
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public OptionalInt fullscreenWidth() {
      return this.fullscreenWidth;
   }

   public OptionalInt fullscreenHeight() {
      return this.fullscreenHeight;
   }

   public boolean fullscreen() {
      return this.fullscreen;
   }
}
