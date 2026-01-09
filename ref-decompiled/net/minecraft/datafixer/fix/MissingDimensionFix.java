package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class MissingDimensionFix extends DataFix {
   public MissingDimensionFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   protected static Type extract1(String field, Type type) {
      return DSL.and(DSL.field(field, type), DSL.remainderType());
   }

   protected static Type extract1Opt(String field, Type type) {
      return DSL.and(DSL.optional(DSL.field(field, type)), DSL.remainderType());
   }

   protected static Type extract2Opt(String field1, Type type1, String field2, Type type2) {
      return DSL.and(DSL.optional(DSL.field(field1, type1)), DSL.optional(DSL.field(field2, type2)), DSL.remainderType());
   }

   protected TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      Type type = DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:debug", DSL.remainderType(), "minecraft:flat", flatGeneratorType(schema), "minecraft:noise", extract2Opt("biome_source", DSL.taggedChoiceType("type", DSL.string(), ImmutableMap.of("minecraft:fixed", extract1("biome", schema.getType(TypeReferences.BIOME)), "minecraft:multi_noise", DSL.list(extract1("biome", schema.getType(TypeReferences.BIOME))), "minecraft:checkerboard", extract1("biomes", DSL.list(schema.getType(TypeReferences.BIOME))), "minecraft:vanilla_layered", DSL.remainderType(), "minecraft:the_end", DSL.remainderType())), "settings", DSL.or(DSL.string(), extract2Opt("default_block", schema.getType(TypeReferences.BLOCK_NAME), "default_fluid", schema.getType(TypeReferences.BLOCK_NAME))))));
      CompoundList.CompoundListType compoundListType = DSL.compoundList(IdentifierNormalizingSchema.getIdentifierType(), extract1("generator", type));
      Type type2 = DSL.and(compoundListType, DSL.remainderType());
      Type type3 = schema.getType(TypeReferences.WORLD_GEN_SETTINGS);
      FieldFinder fieldFinder = new FieldFinder("dimensions", type2);
      if (!type3.findFieldType("dimensions").equals(type2)) {
         throw new IllegalStateException();
      } else {
         OpticFinder opticFinder = compoundListType.finder();
         return this.fixTypeEverywhereTyped("MissingDimensionFix", type3, (worldGenSettingsTyped) -> {
            return worldGenSettingsTyped.updateTyped(fieldFinder, (dimensionsTyped) -> {
               return dimensionsTyped.updateTyped(opticFinder, (dimensionsListTyped) -> {
                  if (!(dimensionsListTyped.getValue() instanceof List)) {
                     throw new IllegalStateException("List exptected");
                  } else if (((List)dimensionsListTyped.getValue()).isEmpty()) {
                     Dynamic dynamic = (Dynamic)worldGenSettingsTyped.get(DSL.remainderFinder());
                     Dynamic dynamic2 = this.method_29912(dynamic);
                     return (Typed)DataFixUtils.orElse(compoundListType.readTyped(dynamic2).result().map(Pair::getFirst), dimensionsListTyped);
                  } else {
                     return dimensionsListTyped;
                  }
               });
            });
         });
      }
   }

   protected static Type flatGeneratorType(Schema schema) {
      return extract1Opt("settings", extract2Opt("biome", schema.getType(TypeReferences.BIOME), "layers", DSL.list(extract1Opt("block", schema.getType(TypeReferences.BLOCK_NAME)))));
   }

   private Dynamic method_29912(Dynamic worldGenSettingsDynamic) {
      long l = worldGenSettingsDynamic.get("seed").asLong(0L);
      return new Dynamic(worldGenSettingsDynamic.getOps(), StructureSeparationDataFix.createDimensionSettings(worldGenSettingsDynamic, l, StructureSeparationDataFix.createDefaultOverworldGeneratorSettings(worldGenSettingsDynamic, l), false));
   }
}
