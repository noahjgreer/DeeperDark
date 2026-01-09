package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class TextComponentHoverAndClickEventFix extends DataFix {
   public TextComponentHoverAndClickEventFix(Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT).findFieldType("hoverEvent");
      return this.method_66120(this.getInputSchema().getTypeRaw(TypeReferences.TEXT_COMPONENT), this.getOutputSchema().getType(TypeReferences.TEXT_COMPONENT), type);
   }

   private TypeRewriteRule method_66120(Type type, Type type2, Type type3) {
      Type type4 = DSL.named(TypeReferences.TEXT_COMPONENT.typeName(), DSL.or(DSL.or(DSL.string(), DSL.list(type)), DSL.and(DSL.optional(DSL.field("extra", DSL.list(type))), DSL.optional(DSL.field("separator", type)), DSL.optional(DSL.field("hoverEvent", type3)), DSL.remainderType())));
      if (!type4.equals(this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT))) {
         String var10002 = String.valueOf(type4);
         throw new IllegalStateException("Text component type did not match, expected " + var10002 + " but got " + String.valueOf(this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT)));
      } else {
         Type type5 = FixUtil.withTypeChanged(type4, type4, type2);
         return this.fixTypeEverywhere("TextComponentHoverAndClickEventFix", type4, type2, (dynamicOps) -> {
            return (pair) -> {
               boolean bl = (Boolean)((Either)pair.getSecond()).map((either) -> {
                  return false;
               }, (pairx) -> {
                  Pair pair2 = (Pair)((Pair)pairx.getSecond()).getSecond();
                  boolean bl = ((Either)pair2.getFirst()).left().isPresent();
                  boolean bl2 = ((Dynamic)pair2.getSecond()).get("clickEvent").result().isPresent();
                  return bl || bl2;
               });
               return !bl ? pair : Util.apply(FixUtil.withType(type5, pair, dynamicOps), type2, TextComponentHoverAndClickEventFix::method_66125).getValue();
            };
         });
      }
   }

   private static Dynamic method_66125(Dynamic dynamic) {
      return dynamic.renameAndFixField("hoverEvent", "hover_event", TextComponentHoverAndClickEventFix::method_66128).renameAndFixField("clickEvent", "click_event", TextComponentHoverAndClickEventFix::method_66130);
   }

   private static Dynamic method_66126(Dynamic dynamic, Dynamic dynamic2, String... strings) {
      String[] var3 = strings;
      int var4 = strings.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String string = var3[var5];
         dynamic = Dynamic.copyField(dynamic2, string, dynamic, string);
      }

      return dynamic;
   }

   private static Dynamic method_66128(Dynamic dynamic) {
      Dynamic var10000;
      Dynamic dynamic2;
      switch (dynamic.get("action").asString("")) {
         case "show_text":
            var10000 = dynamic.renameField("contents", "value");
            break;
         case "show_item":
            dynamic2 = dynamic.get("contents").orElseEmptyMap();
            Optional optional = dynamic2.asString().result();
            var10000 = optional.isPresent() ? dynamic.renameField("contents", "id") : method_66126(dynamic.remove("contents"), dynamic2, "id", "count", "components");
            break;
         case "show_entity":
            dynamic2 = dynamic.get("contents").orElseEmptyMap();
            var10000 = method_66126(dynamic.remove("contents"), dynamic2, "id", "type", "name").renameField("id", "uuid").renameField("type", "id");
            break;
         default:
            var10000 = dynamic;
      }

      return var10000;
   }

   @Nullable
   private static Dynamic method_66130(Dynamic dynamic) {
      String string = dynamic.get("action").asString("");
      String string2 = dynamic.get("value").asString("");
      Dynamic var10000;
      switch (string) {
         case "open_url":
            var10000 = !method_66127(string2) ? null : dynamic.renameField("value", "url");
            break;
         case "open_file":
            var10000 = dynamic.renameField("value", "path");
            break;
         case "run_command":
         case "suggest_command":
            var10000 = !method_66129(string2) ? null : dynamic.renameField("value", "command");
            break;
         case "change_page":
            Integer integer = (Integer)dynamic.get("value").result().map(TextComponentHoverAndClickEventFix::method_66131).orElse((Object)null);
            if (integer == null) {
               var10000 = null;
            } else {
               int i = Math.max(integer, 1);
               var10000 = dynamic.remove("value").set("page", dynamic.createInt(i));
            }
            break;
         default:
            var10000 = dynamic;
      }

      return var10000;
   }

   @Nullable
   private static Integer method_66131(Dynamic dynamic) {
      Optional optional = dynamic.asNumber().result();
      if (optional.isPresent()) {
         return ((Number)optional.get()).intValue();
      } else {
         try {
            return Integer.parseInt(dynamic.asString(""));
         } catch (Exception var3) {
            return null;
         }
      }
   }

   private static boolean method_66127(String string) {
      try {
         URI uRI = new URI(string);
         String string2 = uRI.getScheme();
         if (string2 == null) {
            return false;
         } else {
            String string3 = string2.toLowerCase(Locale.ROOT);
            return "http".equals(string3) || "https".equals(string3);
         }
      } catch (URISyntaxException var4) {
         return false;
      }
   }

   private static boolean method_66129(String string) {
      for(int i = 0; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (c == 167 || c < ' ' || c == 127) {
            return false;
         }
      }

      return true;
   }
}
