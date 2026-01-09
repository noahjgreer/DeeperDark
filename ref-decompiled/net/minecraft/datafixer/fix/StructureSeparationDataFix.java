package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class StructureSeparationDataFix extends DataFix {
   private static final String VILLAGE_STRUCTURE_ID = "minecraft:village";
   private static final String DESERT_PYRAMID_STRUCTURE_ID = "minecraft:desert_pyramid";
   private static final String IGLOO_STRUCTURE_ID = "minecraft:igloo";
   private static final String JUNGLE_PYRAMID_STRUCTURE_ID = "minecraft:jungle_pyramid";
   private static final String SWAMP_HUT_STRUCTURE_ID = "minecraft:swamp_hut";
   private static final String PILLAGER_OUTPOST_STRUCTURE_ID = "minecraft:pillager_outpost";
   private static final String END_CITY_STRUCTURE_ID = "minecraft:endcity";
   private static final String MANSION_STRUCTURE_ID = "minecraft:mansion";
   private static final String MONUMENT_STRUCTURE_ID = "minecraft:monument";
   private static final ImmutableMap STRUCTURE_SPACING = ImmutableMap.builder().put("minecraft:village", new Information(32, 8, 10387312)).put("minecraft:desert_pyramid", new Information(32, 8, 14357617)).put("minecraft:igloo", new Information(32, 8, 14357618)).put("minecraft:jungle_pyramid", new Information(32, 8, 14357619)).put("minecraft:swamp_hut", new Information(32, 8, 14357620)).put("minecraft:pillager_outpost", new Information(32, 8, 165745296)).put("minecraft:monument", new Information(32, 5, 10387313)).put("minecraft:endcity", new Information(20, 11, 10387313)).put("minecraft:mansion", new Information(80, 20, 10387319)).build();

   public StructureSeparationDataFix(Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("WorldGenSettings building", this.getInputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS), (worldGenSettingsTyped) -> {
         return worldGenSettingsTyped.update(DSL.remainderFinder(), StructureSeparationDataFix::updateWorldGenSettings);
      });
   }

   private static Dynamic createGeneratorSettings(long seed, DynamicLike worldGenSettingsDynamic, Dynamic settingsDynamic, Dynamic biomeSourceDynamic) {
      return worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:noise"), worldGenSettingsDynamic.createString("biome_source"), biomeSourceDynamic, worldGenSettingsDynamic.createString("seed"), worldGenSettingsDynamic.createLong(seed), worldGenSettingsDynamic.createString("settings"), settingsDynamic));
   }

   private static Dynamic createBiomeSource(Dynamic worldGenSettingsDynamic, long seed, boolean legacyBiomeInitLayer, boolean largeBiomes) {
      ImmutableMap.Builder builder = ImmutableMap.builder().put(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:vanilla_layered")).put(worldGenSettingsDynamic.createString("seed"), worldGenSettingsDynamic.createLong(seed)).put(worldGenSettingsDynamic.createString("large_biomes"), worldGenSettingsDynamic.createBoolean(largeBiomes));
      if (legacyBiomeInitLayer) {
         builder.put(worldGenSettingsDynamic.createString("legacy_biome_init_layer"), worldGenSettingsDynamic.createBoolean(legacyBiomeInitLayer));
      }

      return worldGenSettingsDynamic.createMap(builder.build());
   }

   private static Dynamic updateWorldGenSettings(Dynamic worldGenSettingsDynamic) {
      DynamicOps dynamicOps = worldGenSettingsDynamic.getOps();
      long l = worldGenSettingsDynamic.get("RandomSeed").asLong(0L);
      Optional optional = worldGenSettingsDynamic.get("generatorName").asString().map((generatorName) -> {
         return generatorName.toLowerCase(Locale.ROOT);
      }).result();
      Optional optional2 = (Optional)worldGenSettingsDynamic.get("legacy_custom_options").asString().result().map(Optional::of).orElseGet(() -> {
         return optional.equals(Optional.of("customized")) ? worldGenSettingsDynamic.get("generatorOptions").asString().result() : Optional.empty();
      });
      boolean bl = false;
      Dynamic dynamic;
      if (optional.equals(Optional.of("customized"))) {
         dynamic = createDefaultOverworldGeneratorSettings(worldGenSettingsDynamic, l);
      } else if (optional.isEmpty()) {
         dynamic = createDefaultOverworldGeneratorSettings(worldGenSettingsDynamic, l);
      } else {
         switch ((String)optional.get()) {
            case "flat":
               OptionalDynamic optionalDynamic = worldGenSettingsDynamic.get("generatorOptions");
               Map map = createFlatWorldStructureSettings(dynamicOps, optionalDynamic);
               dynamic = worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:flat"), worldGenSettingsDynamic.createString("settings"), worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("structures"), worldGenSettingsDynamic.createMap(map), worldGenSettingsDynamic.createString("layers"), (Dynamic)optionalDynamic.get("layers").result().orElseGet(() -> {
                  return worldGenSettingsDynamic.createList(Stream.of(worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("height"), worldGenSettingsDynamic.createInt(1), worldGenSettingsDynamic.createString("block"), worldGenSettingsDynamic.createString("minecraft:bedrock"))), worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("height"), worldGenSettingsDynamic.createInt(2), worldGenSettingsDynamic.createString("block"), worldGenSettingsDynamic.createString("minecraft:dirt"))), worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("height"), worldGenSettingsDynamic.createInt(1), worldGenSettingsDynamic.createString("block"), worldGenSettingsDynamic.createString("minecraft:grass_block")))));
               }), worldGenSettingsDynamic.createString("biome"), worldGenSettingsDynamic.createString(optionalDynamic.get("biome").asString("minecraft:plains"))))));
               break;
            case "debug_all_block_states":
               dynamic = worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:debug")));
               break;
            case "buffet":
               OptionalDynamic optionalDynamic2 = worldGenSettingsDynamic.get("generatorOptions");
               OptionalDynamic optionalDynamic3 = optionalDynamic2.get("chunk_generator");
               Optional optional3 = optionalDynamic3.get("type").asString().result();
               Dynamic dynamic2;
               if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
                  dynamic2 = worldGenSettingsDynamic.createString("minecraft:caves");
                  bl = true;
               } else if (Objects.equals(optional3, Optional.of("minecraft:floating_islands"))) {
                  dynamic2 = worldGenSettingsDynamic.createString("minecraft:floating_islands");
               } else {
                  dynamic2 = worldGenSettingsDynamic.createString("minecraft:overworld");
               }

               Dynamic dynamic3 = (Dynamic)optionalDynamic2.get("biome_source").result().orElseGet(() -> {
                  return worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:fixed")));
               });
               Dynamic dynamic4;
               if (dynamic3.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
                  String string = (String)dynamic3.get("options").get("biomes").asStream().findFirst().flatMap((biomeDynamic) -> {
                     return biomeDynamic.asString().result();
                  }).orElse("minecraft:ocean");
                  dynamic4 = dynamic3.remove("options").set("biome", worldGenSettingsDynamic.createString(string));
               } else {
                  dynamic4 = dynamic3;
               }

               dynamic = createGeneratorSettings(l, worldGenSettingsDynamic, dynamic2, dynamic4);
               break;
            default:
               boolean bl2 = ((String)optional.get()).equals("default");
               boolean bl3 = ((String)optional.get()).equals("default_1_1") || bl2 && worldGenSettingsDynamic.get("generatorVersion").asInt(0) == 0;
               boolean bl4 = ((String)optional.get()).equals("amplified");
               boolean bl5 = ((String)optional.get()).equals("largebiomes");
               dynamic = createGeneratorSettings(l, worldGenSettingsDynamic, worldGenSettingsDynamic.createString(bl4 ? "minecraft:amplified" : "minecraft:overworld"), createBiomeSource(worldGenSettingsDynamic, l, bl3, bl5));
         }
      }

      boolean bl6 = worldGenSettingsDynamic.get("MapFeatures").asBoolean(true);
      boolean bl7 = worldGenSettingsDynamic.get("BonusChest").asBoolean(false);
      ImmutableMap.Builder builder = ImmutableMap.builder();
      builder.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
      builder.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl6));
      builder.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl7));
      builder.put(dynamicOps.createString("dimensions"), createDimensionSettings(worldGenSettingsDynamic, l, dynamic, bl));
      optional2.ifPresent((legacyCustomOptions) -> {
         builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString(legacyCustomOptions));
      });
      return new Dynamic(dynamicOps, dynamicOps.createMap(builder.build()));
   }

   protected static Dynamic createDefaultOverworldGeneratorSettings(Dynamic worldGenSettingsDynamic, long seed) {
      return createGeneratorSettings(seed, worldGenSettingsDynamic, worldGenSettingsDynamic.createString("minecraft:overworld"), createBiomeSource(worldGenSettingsDynamic, seed, false, false));
   }

   protected static Object createDimensionSettings(Dynamic worldGenSettingsDynamic, long seed, Dynamic generatorSettingsDynamic, boolean caves) {
      DynamicOps dynamicOps = worldGenSettingsDynamic.getOps();
      return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("minecraft:overworld"), dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString("minecraft:overworld" + (caves ? "_caves" : "")), dynamicOps.createString("generator"), generatorSettingsDynamic.getValue())), dynamicOps.createString("minecraft:the_nether"), dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString("minecraft:the_nether"), dynamicOps.createString("generator"), createGeneratorSettings(seed, worldGenSettingsDynamic, worldGenSettingsDynamic.createString("minecraft:nether"), worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:multi_noise"), worldGenSettingsDynamic.createString("seed"), worldGenSettingsDynamic.createLong(seed), worldGenSettingsDynamic.createString("preset"), worldGenSettingsDynamic.createString("minecraft:nether")))).getValue())), dynamicOps.createString("minecraft:the_end"), dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString("minecraft:the_end"), dynamicOps.createString("generator"), createGeneratorSettings(seed, worldGenSettingsDynamic, worldGenSettingsDynamic.createString("minecraft:end"), worldGenSettingsDynamic.createMap(ImmutableMap.of(worldGenSettingsDynamic.createString("type"), worldGenSettingsDynamic.createString("minecraft:the_end"), worldGenSettingsDynamic.createString("seed"), worldGenSettingsDynamic.createLong(seed)))).getValue()))));
   }

   private static Map createFlatWorldStructureSettings(DynamicOps worldGenSettingsDynamicOps, OptionalDynamic generatorOptionsDynamic) {
      MutableInt mutableInt = new MutableInt(32);
      MutableInt mutableInt2 = new MutableInt(3);
      MutableInt mutableInt3 = new MutableInt(128);
      MutableBoolean mutableBoolean = new MutableBoolean(false);
      Map map = Maps.newHashMap();
      if (generatorOptionsDynamic.result().isEmpty()) {
         mutableBoolean.setTrue();
         map.put("minecraft:village", (Information)STRUCTURE_SPACING.get("minecraft:village"));
      }

      generatorOptionsDynamic.get("structures").flatMap(Dynamic::getMapValues).ifSuccess((map2) -> {
         map2.forEach((oldStructureName, dynamic) -> {
            dynamic.getMapValues().result().ifPresent((map2) -> {
               map2.forEach((propertyName, spacing) -> {
                  String string = oldStructureName.asString("");
                  String string2 = propertyName.asString("");
                  String string3 = spacing.asString("");
                  if ("stronghold".equals(string)) {
                     mutableBoolean.setTrue();
                     switch (string2) {
                        case "distance":
                           mutableInt.setValue(parseInt(string3, mutableInt.getValue(), 1));
                           return;
                        case "spread":
                           mutableInt2.setValue(parseInt(string3, mutableInt2.getValue(), 1));
                           return;
                        case "count":
                           mutableInt3.setValue(parseInt(string3, mutableInt3.getValue(), 1));
                           return;
                        default:
                     }
                  } else {
                     switch (string2) {
                        case "distance":
                           switch (string) {
                              case "village":
                                 insertStructureSettings(map, "minecraft:village", string3, 9);
                                 return;
                              case "biome_1":
                                 insertStructureSettings(map, "minecraft:desert_pyramid", string3, 9);
                                 insertStructureSettings(map, "minecraft:igloo", string3, 9);
                                 insertStructureSettings(map, "minecraft:jungle_pyramid", string3, 9);
                                 insertStructureSettings(map, "minecraft:swamp_hut", string3, 9);
                                 insertStructureSettings(map, "minecraft:pillager_outpost", string3, 9);
                                 return;
                              case "endcity":
                                 insertStructureSettings(map, "minecraft:endcity", string3, 1);
                                 return;
                              case "mansion":
                                 insertStructureSettings(map, "minecraft:mansion", string3, 1);
                                 return;
                              default:
                                 return;
                           }
                        case "separation":
                           if ("oceanmonument".equals(string)) {
                              Information information = (Information)map.getOrDefault("minecraft:monument", (Information)STRUCTURE_SPACING.get("minecraft:monument"));
                              int i = parseInt(string3, information.separation, 1);
                              map.put("minecraft:monument", new Information(i, information.separation, information.salt));
                           }

                           return;
                        case "spacing":
                           if ("oceanmonument".equals(string)) {
                              insertStructureSettings(map, "minecraft:monument", string3, 1);
                           }

                           return;
                        default:
                     }
                  }
               });
            });
         });
      });
      ImmutableMap.Builder builder = ImmutableMap.builder();
      builder.put(generatorOptionsDynamic.createString("structures"), generatorOptionsDynamic.createMap((Map)map.entrySet().stream().collect(Collectors.toMap((entry) -> {
         return generatorOptionsDynamic.createString((String)entry.getKey());
      }, (entry) -> {
         return ((Information)entry.getValue()).method_28288(worldGenSettingsDynamicOps);
      }))));
      if (mutableBoolean.isTrue()) {
         builder.put(generatorOptionsDynamic.createString("stronghold"), generatorOptionsDynamic.createMap(ImmutableMap.of(generatorOptionsDynamic.createString("distance"), generatorOptionsDynamic.createInt(mutableInt.getValue()), generatorOptionsDynamic.createString("spread"), generatorOptionsDynamic.createInt(mutableInt2.getValue()), generatorOptionsDynamic.createString("count"), generatorOptionsDynamic.createInt(mutableInt3.getValue()))));
      }

      return builder.build();
   }

   private static int parseInt(String string, int defaultValue) {
      return NumberUtils.toInt(string, defaultValue);
   }

   private static int parseInt(String string, int defaultValue, int minValue) {
      return Math.max(minValue, parseInt(string, defaultValue));
   }

   private static void insertStructureSettings(Map map, String structureId, String spacingStr, int minSpacing) {
      Information information = (Information)map.getOrDefault(structureId, (Information)STRUCTURE_SPACING.get(structureId));
      int i = parseInt(spacingStr, information.spacing, minSpacing);
      map.put(structureId, new Information(i, information.separation, information.salt));
   }

   static final class Information {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.INT.fieldOf("spacing").forGetter((information) -> {
            return information.spacing;
         }), Codec.INT.fieldOf("separation").forGetter((information) -> {
            return information.separation;
         }), Codec.INT.fieldOf("salt").forGetter((information) -> {
            return information.salt;
         })).apply(instance, Information::new);
      });
      final int spacing;
      final int separation;
      final int salt;

      public Information(int spacing, int separation, int salt) {
         this.spacing = spacing;
         this.separation = separation;
         this.salt = salt;
      }

      public Dynamic method_28288(DynamicOps dynamicOps) {
         return new Dynamic(dynamicOps, CODEC.encodeStart(dynamicOps, this).result().orElse(dynamicOps.emptyMap()));
      }
   }
}
