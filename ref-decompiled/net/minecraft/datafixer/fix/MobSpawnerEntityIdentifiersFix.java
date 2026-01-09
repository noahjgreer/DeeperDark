package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class MobSpawnerEntityIdentifiersFix extends DataFix {
   public MobSpawnerEntityIdentifiersFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   private Dynamic fixSpawner(Dynamic spawnerDynamic) {
      if (!"MobSpawner".equals(spawnerDynamic.get("id").asString(""))) {
         return spawnerDynamic;
      } else {
         Optional optional = spawnerDynamic.get("EntityId").asString().result();
         if (optional.isPresent()) {
            Dynamic dynamic = (Dynamic)DataFixUtils.orElse(spawnerDynamic.get("SpawnData").result(), spawnerDynamic.emptyMap());
            dynamic = dynamic.set("id", dynamic.createString(((String)optional.get()).isEmpty() ? "Pig" : (String)optional.get()));
            spawnerDynamic = spawnerDynamic.set("SpawnData", dynamic);
            spawnerDynamic = spawnerDynamic.remove("EntityId");
         }

         Optional optional2 = spawnerDynamic.get("SpawnPotentials").asStreamOpt().result();
         if (optional2.isPresent()) {
            spawnerDynamic = spawnerDynamic.set("SpawnPotentials", spawnerDynamic.createList(((Stream)optional2.get()).map((spawnPotentialsDynamic) -> {
               Optional optional = spawnPotentialsDynamic.get("Type").asString().result();
               if (optional.isPresent()) {
                  Dynamic dynamic = ((Dynamic)DataFixUtils.orElse(spawnPotentialsDynamic.get("Properties").result(), spawnPotentialsDynamic.emptyMap())).set("id", spawnPotentialsDynamic.createString((String)optional.get()));
                  return spawnPotentialsDynamic.set("Entity", dynamic).remove("Type").remove("Properties");
               } else {
                  return spawnPotentialsDynamic;
               }
            })));
         }

         return spawnerDynamic;
      }
   }

   public TypeRewriteRule makeRule() {
      Type type = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
      return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), type, (untaggedSpawnerTyped) -> {
         Dynamic dynamic = (Dynamic)untaggedSpawnerTyped.get(DSL.remainderFinder());
         dynamic = dynamic.set("id", dynamic.createString("MobSpawner"));
         DataResult dataResult = type.readTyped(this.fixSpawner(dynamic));
         return dataResult.result().isEmpty() ? untaggedSpawnerTyped : (Typed)((Pair)dataResult.result().get()).getFirst();
      });
   }
}
