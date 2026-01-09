package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Util;

public class ItemVariantComponentizationFix extends DataFix {
   public ItemVariantComponentizationFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   public final TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
      OpticFinder opticFinder2 = type.findField("components");
      return this.fixTypeEverywhereTyped("ItemStack bucket_entity_data variants to separate components", type, (typed) -> {
         Typed var10000;
         switch ((String)typed.getOptional(opticFinder).map(Pair::getSecond).orElse("")) {
            case "minecraft:salmon_bucket":
               var10000 = typed.updateTyped(opticFinder2, ItemVariantComponentizationFix::fixSalmonBucket);
               break;
            case "minecraft:axolotl_bucket":
               var10000 = typed.updateTyped(opticFinder2, ItemVariantComponentizationFix::fixAxolotlBucket);
               break;
            case "minecraft:tropical_fish_bucket":
               var10000 = typed.updateTyped(opticFinder2, ItemVariantComponentizationFix::fixTropicalFishBucket);
               break;
            case "minecraft:painting":
               var10000 = typed.updateTyped(opticFinder2, (typedx) -> {
                  return Util.apply(typedx, typedx.getType(), ItemVariantComponentizationFix::fixPainting);
               });
               break;
            default:
               var10000 = typed;
         }

         return var10000;
      });
   }

   private static String getTropicalFishBaseColorName(int variant) {
      return FixUtil.getColorName(variant >> 16 & 255);
   }

   private static String getTropicalFishPatternColorName(int variant) {
      return FixUtil.getColorName(variant >> 24 & 255);
   }

   private static String getTropicalFishPatternName(int variant) {
      String var10000;
      switch (variant & 65535) {
         case 1:
            var10000 = "flopper";
            break;
         case 256:
            var10000 = "sunstreak";
            break;
         case 257:
            var10000 = "stripey";
            break;
         case 512:
            var10000 = "snooper";
            break;
         case 513:
            var10000 = "glitter";
            break;
         case 768:
            var10000 = "dasher";
            break;
         case 769:
            var10000 = "blockfish";
            break;
         case 1024:
            var10000 = "brinely";
            break;
         case 1025:
            var10000 = "betty";
            break;
         case 1280:
            var10000 = "spotty";
            break;
         case 1281:
            var10000 = "clayfish";
            break;
         default:
            var10000 = "kob";
      }

      return var10000;
   }

   private static Dynamic fixTropicalFishBucket(Dynamic dynamic, Dynamic dynamic2) {
      Optional optional = dynamic2.get("BucketVariantTag").asNumber().result();
      if (optional.isEmpty()) {
         return dynamic;
      } else {
         int i = ((Number)optional.get()).intValue();
         String string = getTropicalFishPatternName(i);
         String string2 = getTropicalFishBaseColorName(i);
         String string3 = getTropicalFishPatternColorName(i);
         return dynamic.update("minecraft:bucket_entity_data", (dynamicx) -> {
            return dynamicx.remove("BucketVariantTag");
         }).set("minecraft:tropical_fish/pattern", dynamic.createString(string)).set("minecraft:tropical_fish/base_color", dynamic.createString(string2)).set("minecraft:tropical_fish/pattern_color", dynamic.createString(string3));
      }
   }

   private static Dynamic fixAxolotlBucket(Dynamic dynamic, Dynamic dynamic2) {
      Optional optional = dynamic2.get("Variant").asNumber().result();
      if (optional.isEmpty()) {
         return dynamic;
      } else {
         String var10000;
         switch (((Number)optional.get()).intValue()) {
            case 1:
               var10000 = "wild";
               break;
            case 2:
               var10000 = "gold";
               break;
            case 3:
               var10000 = "cyan";
               break;
            case 4:
               var10000 = "blue";
               break;
            default:
               var10000 = "lucy";
         }

         String string = var10000;
         return dynamic.update("minecraft:bucket_entity_data", (dynamicx) -> {
            return dynamicx.remove("Variant");
         }).set("minecraft:axolotl/variant", dynamic.createString(string));
      }
   }

   private static Dynamic fixSalmonBucket(Dynamic dynamic, Dynamic dynamic2) {
      Optional optional = dynamic2.get("type").result();
      return optional.isEmpty() ? dynamic : dynamic.update("minecraft:bucket_entity_data", (dynamicx) -> {
         return dynamicx.remove("type");
      }).set("minecraft:salmon/size", (Dynamic)optional.get());
   }

   private static Dynamic fixPainting(Dynamic dynamic) {
      Optional optional = dynamic.get("minecraft:entity_data").result();
      if (optional.isEmpty()) {
         return dynamic;
      } else if (((Dynamic)optional.get()).get("id").asString().result().filter((string) -> {
         return string.equals("minecraft:painting");
      }).isEmpty()) {
         return dynamic;
      } else {
         Optional optional2 = ((Dynamic)optional.get()).get("variant").result();
         Dynamic dynamic2 = ((Dynamic)optional.get()).remove("variant");
         if (dynamic2.remove("id").equals(dynamic2.emptyMap())) {
            dynamic = dynamic.remove("minecraft:entity_data");
         } else {
            dynamic = dynamic.set("minecraft:entity_data", dynamic2);
         }

         if (optional2.isPresent()) {
            dynamic = dynamic.set("minecraft:painting/variant", (Dynamic)optional2.get());
         }

         return dynamic;
      }
   }

   @FunctionalInterface
   private interface class_10622 extends Function {
      default Typed apply(Typed typed) {
         return typed.update(DSL.remainderFinder(), this::fixRemainder);
      }

      default Dynamic fixRemainder(Dynamic dynamic) {
         return (Dynamic)dynamic.get("minecraft:bucket_entity_data").result().map((dynamic2) -> {
            return this.fixRemainder(dynamic, dynamic2);
         }).orElse(dynamic);
      }

      Dynamic fixRemainder(Dynamic dynamic, Dynamic dynamic2);
   }
}
