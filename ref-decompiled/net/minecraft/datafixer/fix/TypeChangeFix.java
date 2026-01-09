package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.FixUtil;

public class TypeChangeFix extends ChoiceFix {
   public TypeChangeFix(Schema outputSchema, String name, DSL.TypeReference type, String choiceName) {
      super(outputSchema, true, name, type, choiceName);
   }

   protected Typed transform(Typed inputTyped) {
      Type type = this.getOutputSchema().getChoiceType(this.type, this.choiceName);
      return FixUtil.withType(type, inputTyped);
   }
}
