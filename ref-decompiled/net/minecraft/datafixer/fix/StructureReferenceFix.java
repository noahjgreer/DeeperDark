package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class StructureReferenceFix extends DataFix {
   public StructureReferenceFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
      return this.fixTypeEverywhereTyped("Structure Reference Fix", type, (structureFeatureTyped) -> {
         return structureFeatureTyped.update(DSL.remainderFinder(), StructureReferenceFix::updateReferences);
      });
   }

   private static Dynamic updateReferences(Dynamic structureFeatureDynamic) {
      return structureFeatureDynamic.update("references", (referencesDynamic) -> {
         return referencesDynamic.createInt((Integer)referencesDynamic.asNumber().map(Number::intValue).result().filter((references) -> {
            return references > 0;
         }).orElse(1));
      });
   }
}
