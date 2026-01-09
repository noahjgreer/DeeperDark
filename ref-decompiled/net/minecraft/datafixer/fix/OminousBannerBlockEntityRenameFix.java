package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class OminousBannerBlockEntityRenameFix extends ChoiceFix {
   public OminousBannerBlockEntityRenameFix(Schema schema, boolean bl) {
      super(schema, bl, "OminousBannerBlockEntityRenameFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
   }

   protected Typed transform(Typed inputTyped) {
      OpticFinder opticFinder = inputTyped.getType().findField("CustomName");
      OpticFinder opticFinder2 = DSL.typeFinder(this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT));
      return inputTyped.updateTyped(opticFinder, (typed) -> {
         return typed.update(opticFinder2, (pair) -> {
            return pair.mapSecond((string) -> {
               return string.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            });
         });
      });
   }
}
