package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class EntityMinecartIdentifiersFix extends EntityTransformFix {
   public EntityMinecartIdentifiersFix(Schema outputSchema) {
      super("EntityMinecartIdentifiersFix", outputSchema, true);
   }

   protected Pair transform(String choice, Typed entityTyped) {
      if (!choice.equals("Minecart")) {
         return Pair.of(choice, entityTyped);
      } else {
         int i = ((Dynamic)entityTyped.getOrCreate(DSL.remainderFinder())).get("Type").asInt(0);
         String var10000;
         switch (i) {
            case 1:
               var10000 = "MinecartChest";
               break;
            case 2:
               var10000 = "MinecartFurnace";
               break;
            default:
               var10000 = "MinecartRideable";
         }

         String string = var10000;
         Type type = (Type)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string);
         return Pair.of(string, Util.apply(entityTyped, type, (entityDynamic) -> {
            return entityDynamic.remove("Type");
         }));
      }
   }
}
