package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class OptionsAmbientOcclusionFix extends DataFix {
   public OptionsAmbientOcclusionFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsAmbientOcclusionFix", this.getInputSchema().getType(TypeReferences.OPTIONS), (optionsTyped) -> {
         return optionsTyped.update(DSL.remainderFinder(), (options) -> {
            return (Dynamic)DataFixUtils.orElse(options.get("ao").asString().map((setting) -> {
               return options.set("ao", options.createString(fixValue(setting)));
            }).result(), options);
         });
      });
   }

   private static String fixValue(String oldValue) {
      String var10000;
      switch (oldValue) {
         case "0":
            var10000 = "false";
            break;
         case "1":
         case "2":
            var10000 = "true";
            break;
         default:
            var10000 = oldValue;
      }

      return var10000;
   }
}
