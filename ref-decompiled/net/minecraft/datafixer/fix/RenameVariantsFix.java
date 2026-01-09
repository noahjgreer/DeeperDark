package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;

public class RenameVariantsFix extends ChoiceFix {
   private final Map oldToNewNames;

   public RenameVariantsFix(Schema outputSchema, String name, DSL.TypeReference type, String choiceName, Map oldToNewNames) {
      super(outputSchema, false, name, type, choiceName);
      this.oldToNewNames = oldToNewNames;
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), (dynamic) -> {
         return dynamic.update("variant", (variant) -> {
            return (Dynamic)DataFixUtils.orElse(variant.asString().map((variantName) -> {
               return variant.createString((String)this.oldToNewNames.getOrDefault(variantName, variantName));
            }).result(), variant);
         });
      });
   }
}
