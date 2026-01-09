package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;

public class RemoveFeatureTogglesFix extends DataFix {
   private final String name;
   private final Set featureToggleIds;

   public RemoveFeatureTogglesFix(Schema outputSchema, String name, Set featureToggleIds) {
      super(outputSchema, false);
      this.name = name;
      this.featureToggleIds = featureToggleIds;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(TypeReferences.LIGHTWEIGHT_LEVEL), (levelTyped) -> {
         return levelTyped.update(DSL.remainderFinder(), this::removeFeatureToggles);
      });
   }

   private Dynamic removeFeatureToggles(Dynamic dynamic) {
      List list = (List)dynamic.get("removed_features").asStream().collect(Collectors.toCollection(ArrayList::new));
      Dynamic dynamic2 = dynamic.update("enabled_features", (enabledFeatures) -> {
         Optional var10000 = enabledFeatures.asStreamOpt().result().map((stream) -> {
            return stream.filter((enabledFeature) -> {
               Optional optional = enabledFeature.asString().result();
               if (optional.isEmpty()) {
                  return true;
               } else {
                  boolean bl = this.featureToggleIds.contains(optional.get());
                  if (bl) {
                     list.add(dynamic.createString((String)optional.get()));
                  }

                  return !bl;
               }
            });
         });
         Objects.requireNonNull(dynamic);
         return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createList), enabledFeatures);
      });
      if (!list.isEmpty()) {
         dynamic2 = dynamic2.set("removed_features", dynamic.createList(list.stream()));
      }

      return dynamic2;
   }
}
