package net.minecraft.block;

import net.minecraft.util.Identifier;

public record DecoratedPotPattern(Identifier assetId) {
   public DecoratedPotPattern(Identifier identifier) {
      this.assetId = identifier;
   }

   public Identifier assetId() {
      return this.assetId;
   }
}
