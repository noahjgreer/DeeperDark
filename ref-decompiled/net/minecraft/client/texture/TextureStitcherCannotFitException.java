package net.minecraft.client.texture;

import java.util.Collection;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitcherCannotFitException extends RuntimeException {
   private final Collection sprites;

   public TextureStitcherCannotFitException(TextureStitcher.Stitchable sprite, Collection sprites) {
      super(String.format(Locale.ROOT, "Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", sprite.getId(), sprite.getWidth(), sprite.getHeight()));
      this.sprites = sprites;
   }

   public Collection getSprites() {
      return this.sprites;
   }
}
