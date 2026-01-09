package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class AddFlagIfNotPresentFix extends DataFix {
   private final String description;
   private final boolean value;
   private final String key;
   private final DSL.TypeReference typeReference;

   public AddFlagIfNotPresentFix(Schema outputSchema, DSL.TypeReference typeReference, String key, boolean value) {
      super(outputSchema, true);
      this.value = value;
      this.key = key;
      String var10001 = this.key;
      this.description = "AddFlagIfNotPresentFix_" + var10001 + "=" + this.value + " for " + outputSchema.getVersionKey();
      this.typeReference = typeReference;
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(this.typeReference);
      return this.fixTypeEverywhereTyped(this.description, type, (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.set(this.key, (Dynamic)DataFixUtils.orElseGet(dynamic.get(this.key).result(), () -> {
               return dynamic.createBoolean(this.value);
            }));
         });
      });
   }
}
