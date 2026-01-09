package net.minecraft.datafixer.fix;

import com.google.gson.JsonElement;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;

public class LegacyHoverEventFix extends DataFix {
   public LegacyHoverEventFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT).findFieldType("hoverEvent");
      return this.method_66084(this.getInputSchema().getTypeRaw(TypeReferences.TEXT_COMPONENT), type);
   }

   private TypeRewriteRule method_66084(Type type, Type type2) {
      Type type3 = DSL.named(TypeReferences.TEXT_COMPONENT.typeName(), DSL.or(DSL.or(DSL.string(), DSL.list(type)), DSL.and(DSL.optional(DSL.field("extra", DSL.list(type))), DSL.optional(DSL.field("separator", type)), DSL.optional(DSL.field("hoverEvent", type2)), DSL.remainderType())));
      if (!type3.equals(this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT))) {
         String var10002 = String.valueOf(type3);
         throw new IllegalStateException("Text component type did not match, expected " + var10002 + " but got " + String.valueOf(this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT)));
      } else {
         return this.fixTypeEverywhere("LegacyHoverEventFix", type3, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond((either) -> {
                  return either.mapRight((pair) -> {
                     return pair.mapSecond((pairx) -> {
                        return pairx.mapSecond((pair) -> {
                           Dynamic dynamic = (Dynamic)pair.getSecond();
                           Optional optional = dynamic.get("hoverEvent").result();
                           if (optional.isEmpty()) {
                              return pair;
                           } else {
                              Optional optional2 = ((Dynamic)optional.get()).get("value").result();
                              if (optional2.isEmpty()) {
                                 return pair;
                              } else {
                                 String string = (String)((Either)pair.getFirst()).left().map(Pair::getFirst).orElse("");
                                 Pair pair2 = (Pair)this.method_66089(type2, string, (Dynamic)optional.get());
                                 return pair.mapFirst((either) -> {
                                    return Either.left(pair2);
                                 });
                              }
                           }
                        });
                     });
                  });
               });
            };
         });
      }
   }

   private Object method_66089(Type type, String string, Dynamic dynamic) {
      return "show_text".equals(string) ? method_66087(type, dynamic) : method_66092(type, dynamic);
   }

   private static Object method_66087(Type type, Dynamic dynamic) {
      Dynamic dynamic2 = dynamic.renameField("value", "contents");
      return Util.readTyped(type, dynamic2).getValue();
   }

   private static Object method_66092(Type type, Dynamic dynamic) {
      JsonElement jsonElement = (JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue();
      Dynamic dynamic2 = new Dynamic(JavaOps.INSTANCE, Map.of("action", "show_text", "contents", Map.of("text", "Legacy hoverEvent: " + JsonHelper.toSortedString(jsonElement))));
      return Util.readTyped(type, dynamic2).getValue();
   }
}
