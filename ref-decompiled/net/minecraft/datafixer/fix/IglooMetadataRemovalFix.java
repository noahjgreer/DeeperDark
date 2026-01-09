package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class IglooMetadataRemovalFix extends DataFix {
   public IglooMetadataRemovalFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
      return this.fixTypeEverywhereTyped("IglooMetadataRemovalFix", type, (structureFeatureTyped) -> {
         return structureFeatureTyped.update(DSL.remainderFinder(), IglooMetadataRemovalFix::removeMetadata);
      });
   }

   private static Dynamic removeMetadata(Dynamic structureFeatureDynamic) {
      boolean bl = (Boolean)structureFeatureDynamic.get("Children").asStreamOpt().map((stream) -> {
         return stream.allMatch(IglooMetadataRemovalFix::isIgloo);
      }).result().orElse(false);
      return bl ? structureFeatureDynamic.set("id", structureFeatureDynamic.createString("Igloo")).remove("Children") : structureFeatureDynamic.update("Children", IglooMetadataRemovalFix::removeIgloos);
   }

   private static Dynamic removeIgloos(Dynamic structureFeatureDynamic) {
      DataResult var10000 = structureFeatureDynamic.asStreamOpt().map((stream) -> {
         return stream.filter((dynamic) -> {
            return !isIgloo(dynamic);
         });
      });
      Objects.requireNonNull(structureFeatureDynamic);
      return (Dynamic)var10000.map(structureFeatureDynamic::createList).result().orElse(structureFeatureDynamic);
   }

   private static boolean isIgloo(Dynamic structureFeatureDynamic) {
      return structureFeatureDynamic.get("id").asString("").equals("Iglu");
   }
}
