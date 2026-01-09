package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;

public class SimpleBlockPropertyRenameFix extends BlockPropertyRenameFix {
   private final String targetId;
   private final String oldPropertyName;
   private final String newPropertyName;
   private final UnaryOperator valueConverter;

   public SimpleBlockPropertyRenameFix(Schema outputSchema, String name, String targetId, String oldPropertyName, String newPropertyName, UnaryOperator valueConverter) {
      super(outputSchema, name);
      this.targetId = targetId;
      this.oldPropertyName = oldPropertyName;
      this.newPropertyName = newPropertyName;
      this.valueConverter = valueConverter;
   }

   protected boolean shouldFix(String id) {
      return id.equals(this.targetId);
   }

   protected Dynamic fix(String id, Dynamic properties) {
      return properties.renameAndFixField(this.oldPropertyName, this.newPropertyName, (value) -> {
         return value.createString((String)this.valueConverter.apply(value.asString("")));
      });
   }
}
