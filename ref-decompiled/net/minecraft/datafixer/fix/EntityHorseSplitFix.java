package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class EntityHorseSplitFix extends EntityTransformFix {
   public EntityHorseSplitFix(Schema schema, boolean bl) {
      super("EntityHorseSplitFix", schema, bl);
   }

   protected Pair transform(String choice, Typed entityTyped) {
      if (Objects.equals("EntityHorse", choice)) {
         Dynamic dynamic = (Dynamic)entityTyped.get(DSL.remainderFinder());
         int i = dynamic.get("Type").asInt(0);
         String var10000;
         switch (i) {
            case 1:
               var10000 = "Donkey";
               break;
            case 2:
               var10000 = "Mule";
               break;
            case 3:
               var10000 = "ZombieHorse";
               break;
            case 4:
               var10000 = "SkeletonHorse";
               break;
            default:
               var10000 = "Horse";
         }

         String string = var10000;
         Type type = (Type)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string);
         return Pair.of(string, Util.apply(entityTyped, type, (dynamicx) -> {
            return dynamicx.remove("Type");
         }));
      } else {
         return Pair.of(choice, entityTyped);
      }
   }
}
