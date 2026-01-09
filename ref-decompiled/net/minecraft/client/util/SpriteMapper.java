package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record SpriteMapper(Identifier sheet, String prefix) {
   public SpriteMapper(Identifier identifier, String string) {
      this.sheet = identifier;
      this.prefix = string;
   }

   public SpriteIdentifier map(Identifier id) {
      return new SpriteIdentifier(this.sheet, id.withPrefixedPath(this.prefix + "/"));
   }

   public SpriteIdentifier mapVanilla(String id) {
      return this.map(Identifier.ofVanilla(id));
   }

   public Identifier sheet() {
      return this.sheet;
   }

   public String prefix() {
      return this.prefix;
   }
}
