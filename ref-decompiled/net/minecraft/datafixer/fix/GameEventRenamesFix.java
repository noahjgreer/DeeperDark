package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class GameEventRenamesFix extends DataFix {
   private final String name;
   private final DSL.TypeReference typeReference;
   private final UnaryOperator renamer;

   public GameEventRenamesFix(Schema outputSchema, String name, DSL.TypeReference typeReference, UnaryOperator renamer) {
      super(outputSchema, false);
      this.name = name;
      this.typeReference = typeReference;
      this.renamer = renamer;
   }

   protected TypeRewriteRule makeRule() {
      Type type = DSL.named(this.typeReference.typeName(), IdentifierNormalizingSchema.getIdentifierType());
      if (!Objects.equals(type, this.getInputSchema().getType(this.typeReference))) {
         throw new IllegalStateException("\"" + this.typeReference.typeName() + "\" is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, type, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(this.renamer);
            };
         });
      }
   }
}
