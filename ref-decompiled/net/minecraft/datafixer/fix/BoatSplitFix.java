package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class BoatSplitFix extends DataFix {
   public BoatSplitFix(Schema outputSchema) {
      super(outputSchema, true);
   }

   private static boolean isBoat(String id) {
      return id.equals("minecraft:boat");
   }

   private static boolean isChestBoat(String id) {
      return id.equals("minecraft:chest_boat");
   }

   private static boolean isBoatOrChestBoat(String id) {
      return isBoat(id) || isChestBoat(id);
   }

   private static String getNewBoatIdFromOldType(String type) {
      String var10000;
      switch (type) {
         case "spruce":
            var10000 = "minecraft:spruce_boat";
            break;
         case "birch":
            var10000 = "minecraft:birch_boat";
            break;
         case "jungle":
            var10000 = "minecraft:jungle_boat";
            break;
         case "acacia":
            var10000 = "minecraft:acacia_boat";
            break;
         case "cherry":
            var10000 = "minecraft:cherry_boat";
            break;
         case "dark_oak":
            var10000 = "minecraft:dark_oak_boat";
            break;
         case "mangrove":
            var10000 = "minecraft:mangrove_boat";
            break;
         case "bamboo":
            var10000 = "minecraft:bamboo_raft";
            break;
         default:
            var10000 = "minecraft:oak_boat";
      }

      return var10000;
   }

   private static String getNewChestBoatIdFromOldType(String type) {
      String var10000;
      switch (type) {
         case "spruce":
            var10000 = "minecraft:spruce_chest_boat";
            break;
         case "birch":
            var10000 = "minecraft:birch_chest_boat";
            break;
         case "jungle":
            var10000 = "minecraft:jungle_chest_boat";
            break;
         case "acacia":
            var10000 = "minecraft:acacia_chest_boat";
            break;
         case "cherry":
            var10000 = "minecraft:cherry_chest_boat";
            break;
         case "dark_oak":
            var10000 = "minecraft:dark_oak_chest_boat";
            break;
         case "mangrove":
            var10000 = "minecraft:mangrove_chest_boat";
            break;
         case "bamboo":
            var10000 = "minecraft:bamboo_chest_raft";
            break;
         default:
            var10000 = "minecraft:oak_chest_boat";
      }

      return var10000;
   }

   public TypeRewriteRule makeRule() {
      OpticFinder opticFinder = DSL.fieldFinder("id", IdentifierNormalizingSchema.getIdentifierType());
      Type type = this.getInputSchema().getType(TypeReferences.ENTITY);
      Type type2 = this.getOutputSchema().getType(TypeReferences.ENTITY);
      return this.fixTypeEverywhereTyped("BoatSplitFix", type, type2, (typed) -> {
         Optional optional = typed.getOptional(opticFinder);
         if (optional.isPresent() && isBoatOrChestBoat((String)optional.get())) {
            Dynamic dynamic = (Dynamic)typed.getOrCreate(DSL.remainderFinder());
            Optional optional2 = dynamic.get("Type").asString().result();
            String string;
            if (isChestBoat((String)optional.get())) {
               string = (String)optional2.map(BoatSplitFix::getNewChestBoatIdFromOldType).orElse("minecraft:oak_chest_boat");
            } else {
               string = (String)optional2.map(BoatSplitFix::getNewBoatIdFromOldType).orElse("minecraft:oak_boat");
            }

            return FixUtil.withType(type2, typed).update(DSL.remainderFinder(), (dynamicx) -> {
               return dynamicx.remove("Type");
            }).set(opticFinder, string);
         } else {
            return FixUtil.withType(type2, typed);
         }
      });
   }
}
