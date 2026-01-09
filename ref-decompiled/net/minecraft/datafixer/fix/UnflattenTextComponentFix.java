package net.minecraft.datafixer.fix;

import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class UnflattenTextComponentFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public UnflattenTextComponentFix(Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT);
      Type type2 = this.getOutputSchema().getType(TypeReferences.TEXT_COMPONENT);
      return this.method_66142(type, type2);
   }

   private TypeRewriteRule method_66142(Type type, Type type2) {
      return this.fixTypeEverywhere("UnflattenTextComponentFix", type, type2, (dynamicOps) -> {
         return (pair) -> {
            return Util.readTyped(type2, method_66145(dynamicOps, (String)pair.getSecond()), true).getValue();
         };
      });
   }

   private static Dynamic method_66145(DynamicOps dynamicOps, String string) {
      try {
         JsonElement jsonElement = LenientJsonParser.parse(string);
         if (!jsonElement.isJsonNull()) {
            return new Dynamic(dynamicOps, JsonOps.INSTANCE.convertTo(dynamicOps, jsonElement));
         }
      } catch (Exception var3) {
         LOGGER.error("Failed to unflatten text component json: {}", string, var3);
      }

      return new Dynamic(dynamicOps, dynamicOps.createString(string));
   }
}
