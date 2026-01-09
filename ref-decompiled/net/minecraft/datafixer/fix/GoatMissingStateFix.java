package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;

public class GoatMissingStateFix extends ChoiceFix {
   public GoatMissingStateFix(Schema outputSchema) {
      super(outputSchema, false, "EntityGoatMissingStateFix", TypeReferences.ENTITY, "minecraft:goat");
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), (goatDynamic) -> {
         return goatDynamic.set("HasLeftHorn", goatDynamic.createBoolean(true)).set("HasRightHorn", goatDynamic.createBoolean(true));
      });
   }
}
