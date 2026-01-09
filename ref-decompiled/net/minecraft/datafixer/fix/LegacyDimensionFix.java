package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class LegacyDimensionFix extends DataFix {
   public LegacyDimensionFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      TypeRewriteRule typeRewriteRule = this.fixTypeEverywhereTyped("PlayerLegacyDimensionFix", this.getInputSchema().getType(TypeReferences.PLAYER), (typed) -> {
         return typed.update(DSL.remainderFinder(), this::fixPlayer);
      });
      Type type = this.getInputSchema().getType(TypeReferences.SAVED_DATA_MAP_DATA);
      OpticFinder opticFinder = type.findField("data");
      TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped("MapLegacyDimensionFix", type, (typed) -> {
         return typed.updateTyped(opticFinder, (typedx) -> {
            return typedx.update(DSL.remainderFinder(), this::fixMap);
         });
      });
      return TypeRewriteRule.seq(typeRewriteRule, typeRewriteRule2);
   }

   private Dynamic fixMap(Dynamic dynamic) {
      return dynamic.update("dimension", this::fix);
   }

   private Dynamic fixPlayer(Dynamic dynamic) {
      return dynamic.update("Dimension", this::fix);
   }

   private Dynamic fix(Dynamic dynamic) {
      return (Dynamic)DataFixUtils.orElse(dynamic.asNumber().result().map((id) -> {
         Dynamic var10000;
         switch (id.intValue()) {
            case -1:
               var10000 = dynamic.createString("minecraft:the_nether");
               break;
            case 1:
               var10000 = dynamic.createString("minecraft:the_end");
               break;
            default:
               var10000 = dynamic.createString("minecraft:overworld");
         }

         return var10000;
      }), dynamic);
   }
}
