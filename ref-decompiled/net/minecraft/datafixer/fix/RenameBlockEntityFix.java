package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;

public class RenameBlockEntityFix extends DataFix {
   private final String name;
   private final UnaryOperator renamer;

   private RenameBlockEntityFix(Schema outputSchema, String name, UnaryOperator renamer) {
      super(outputSchema, true);
      this.name = name;
      this.renamer = renamer;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoice.TaggedChoiceType taggedChoiceType = this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
      TaggedChoice.TaggedChoiceType taggedChoiceType2 = this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
      return this.fixTypeEverywhere(this.name, taggedChoiceType, taggedChoiceType2, (ops) -> {
         return (pair) -> {
            return pair.mapFirst(this.renamer);
         };
      });
   }

   public static DataFix create(Schema outputSchema, String name, UnaryOperator renamer) {
      return new RenameBlockEntityFix(outputSchema, name, renamer);
   }
}
