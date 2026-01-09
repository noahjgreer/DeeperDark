package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class BedBlockEntityFix extends DataFix {
   public BedBlockEntityFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   public TypeRewriteRule makeRule() {
      Type type = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type type2 = type.findFieldType("Level");
      Type type3 = type2.findFieldType("TileEntities");
      if (!(type3 instanceof List.ListType listType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         return this.fix(type2, listType);
      }
   }

   private TypeRewriteRule fix(Type level, List.ListType blockEntities) {
      Type type = blockEntities.getElement();
      OpticFinder opticFinder = DSL.fieldFinder("Level", level);
      OpticFinder opticFinder2 = DSL.fieldFinder("TileEntities", blockEntities);
      int i = true;
      return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), (dynamicOps) -> {
         return (pair) -> {
            return pair;
         };
      }), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(TypeReferences.CHUNK), (typed) -> {
         Typed typed2 = typed.getTyped(opticFinder);
         Dynamic dynamic = (Dynamic)typed2.get(DSL.remainderFinder());
         int i = dynamic.get("xPos").asInt(0);
         int j = dynamic.get("zPos").asInt(0);
         java.util.List list = Lists.newArrayList((Iterable)typed2.getOrCreate(opticFinder2));
         java.util.List list2 = dynamic.get("Sections").asList(Function.identity());
         Iterator var10 = list2.iterator();

         while(var10.hasNext()) {
            Dynamic dynamic2 = (Dynamic)var10.next();
            int k = dynamic2.get("Y").asInt(0);
            Streams.mapWithIndex(dynamic2.get("Blocks").asIntStream(), (blockData, index) -> {
               if (416 == (blockData & 255) << 4) {
                  int l = (int)index;
                  int m = l & 15;
                  int n = l >> 8 & 15;
                  int o = l >> 4 & 15;
                  Map map = Maps.newHashMap();
                  map.put(dynamic2.createString("id"), dynamic2.createString("minecraft:bed"));
                  map.put(dynamic2.createString("x"), dynamic2.createInt(m + (i << 4)));
                  map.put(dynamic2.createString("y"), dynamic2.createInt(n + (k << 4)));
                  map.put(dynamic2.createString("z"), dynamic2.createInt(o + (j << 4)));
                  map.put(dynamic2.createString("color"), dynamic2.createShort((short)14));
                  return map;
               } else {
                  return null;
               }
            }).forEachOrdered((map) -> {
               if (map != null) {
                  list.add(((Pair)type.read(dynamic2.createMap(map)).result().orElseThrow(() -> {
                     return new IllegalStateException("Could not parse newly created bed block entity.");
                  })).getFirst());
               }

            });
         }

         return !list.isEmpty() ? typed.set(opticFinder, typed2.set(opticFinder2, list)) : typed;
      }));
   }
}
