package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class RenameEntityNbtKeyFix extends ChoiceFix {
   private final Map oldToNewKeyNames;

   public RenameEntityNbtKeyFix(Schema outputSchema, String name, String entityId, Map oldToNewKeyNames) {
      super(outputSchema, false, name, TypeReferences.ENTITY, entityId);
      this.oldToNewKeyNames = oldToNewKeyNames;
   }

   public Dynamic fix(Dynamic dynamic) {
      Map.Entry entry;
      for(Iterator var2 = this.oldToNewKeyNames.entrySet().iterator(); var2.hasNext(); dynamic = dynamic.renameField((String)entry.getKey(), (String)entry.getValue())) {
         entry = (Map.Entry)var2.next();
      }

      return dynamic;
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), this::fix);
   }
}
