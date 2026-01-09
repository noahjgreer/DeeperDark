package net.minecraft.datafixer.schema;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;
import org.slf4j.Logger;

public class Schema99 extends Schema {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final Map BLOCKS_TO_BLOCK_ENTITIES = (Map)DataFixUtils.make(Maps.newHashMap(), (map) -> {
      map.put("minecraft:furnace", "Furnace");
      map.put("minecraft:lit_furnace", "Furnace");
      map.put("minecraft:chest", "Chest");
      map.put("minecraft:trapped_chest", "Chest");
      map.put("minecraft:ender_chest", "EnderChest");
      map.put("minecraft:jukebox", "RecordPlayer");
      map.put("minecraft:dispenser", "Trap");
      map.put("minecraft:dropper", "Dropper");
      map.put("minecraft:sign", "Sign");
      map.put("minecraft:mob_spawner", "MobSpawner");
      map.put("minecraft:noteblock", "Music");
      map.put("minecraft:brewing_stand", "Cauldron");
      map.put("minecraft:enhanting_table", "EnchantTable");
      map.put("minecraft:command_block", "CommandBlock");
      map.put("minecraft:beacon", "Beacon");
      map.put("minecraft:skull", "Skull");
      map.put("minecraft:daylight_detector", "DLDetector");
      map.put("minecraft:hopper", "Hopper");
      map.put("minecraft:banner", "Banner");
      map.put("minecraft:flower_pot", "FlowerPot");
      map.put("minecraft:repeating_command_block", "CommandBlock");
      map.put("minecraft:chain_command_block", "CommandBlock");
      map.put("minecraft:standing_sign", "Sign");
      map.put("minecraft:wall_sign", "Sign");
      map.put("minecraft:piston_head", "Piston");
      map.put("minecraft:daylight_detector_inverted", "DLDetector");
      map.put("minecraft:unpowered_comparator", "Comparator");
      map.put("minecraft:powered_comparator", "Comparator");
      map.put("minecraft:wall_banner", "Banner");
      map.put("minecraft:standing_banner", "Banner");
      map.put("minecraft:structure_block", "Structure");
      map.put("minecraft:end_portal", "Airportal");
      map.put("minecraft:end_gateway", "EndGateway");
      map.put("minecraft:shield", "Banner");
   });
   public static final Map field_49718 = Map.of("minecraft:armor_stand", "ArmorStand", "minecraft:painting", "Painting");
   protected static final Hook.HookFunction field_5747 = new Hook.HookFunction() {
      public Object apply(DynamicOps ops, Object value) {
         return Schema99.updateBlockEntityTags(new Dynamic(ops, value), Schema99.BLOCKS_TO_BLOCK_ENTITIES, Schema99.field_49718);
      }
   };

   public Schema99(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   protected static void targetInTile(Schema schema, Map map, String entityId) {
      schema.register(map, entityId, () -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
      });
   }

   protected static void targetDisplayTile(Schema schema, Map map, String entityId) {
      schema.register(map, entityId, () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema));
      });
   }

   protected static void targetItems(Schema schema, Map map, String entityId) {
      schema.register(map, entityId, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
   }

   public Map registerEntities(Schema schema) {
      Map map = Maps.newHashMap();
      schema.register(map, "Item", (name) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.registerSimple(map, "XPOrb");
      targetInTile(schema, map, "ThrownEgg");
      schema.registerSimple(map, "LeashKnot");
      schema.registerSimple(map, "Painting");
      schema.register(map, "Arrow", (name) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
      });
      schema.register(map, "TippedArrow", (name) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
      });
      schema.register(map, "SpectralArrow", (name) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema));
      });
      targetInTile(schema, map, "Snowball");
      targetInTile(schema, map, "Fireball");
      targetInTile(schema, map, "SmallFireball");
      targetInTile(schema, map, "ThrownEnderpearl");
      schema.registerSimple(map, "EyeOfEnderSignal");
      schema.register(map, "ThrownPotion", (name) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(schema), "Potion", TypeReferences.ITEM_STACK.in(schema));
      });
      targetInTile(schema, map, "ThrownExpBottle");
      schema.register(map, "ItemFrame", (name) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
      });
      targetInTile(schema, map, "WitherSkull");
      schema.registerSimple(map, "PrimedTnt");
      schema.register(map, "FallingSand", (name) -> {
         return DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(schema), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(schema));
      });
      schema.register(map, "FireworksRocketEntity", (name) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.registerSimple(map, "Boat");
      schema.register(map, "Minecart", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
      targetDisplayTile(schema, map, "MinecartRideable");
      schema.register(map, "MinecartChest", (name) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
      targetDisplayTile(schema, map, "MinecartFurnace");
      targetDisplayTile(schema, map, "MinecartTNT");
      schema.register(map, "MinecartSpawner", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), TypeReferences.UNTAGGED_SPAWNER.in(schema));
      });
      schema.register(map, "MinecartHopper", (name) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
      schema.register(map, "MinecartCommandBlock", () -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(schema), "LastOutput", TypeReferences.TEXT_COMPONENT.in(schema));
      });
      schema.registerSimple(map, "ArmorStand");
      schema.registerSimple(map, "Creeper");
      schema.registerSimple(map, "Skeleton");
      schema.registerSimple(map, "Spider");
      schema.registerSimple(map, "Giant");
      schema.registerSimple(map, "Zombie");
      schema.registerSimple(map, "Slime");
      schema.registerSimple(map, "Ghast");
      schema.registerSimple(map, "PigZombie");
      schema.register(map, "Enderman", (name) -> {
         return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(schema));
      });
      schema.registerSimple(map, "CaveSpider");
      schema.registerSimple(map, "Silverfish");
      schema.registerSimple(map, "Blaze");
      schema.registerSimple(map, "LavaSlime");
      schema.registerSimple(map, "EnderDragon");
      schema.registerSimple(map, "WitherBoss");
      schema.registerSimple(map, "Bat");
      schema.registerSimple(map, "Witch");
      schema.registerSimple(map, "Endermite");
      schema.registerSimple(map, "Guardian");
      schema.registerSimple(map, "Pig");
      schema.registerSimple(map, "Sheep");
      schema.registerSimple(map, "Cow");
      schema.registerSimple(map, "Chicken");
      schema.registerSimple(map, "Squid");
      schema.registerSimple(map, "Wolf");
      schema.registerSimple(map, "MushroomCow");
      schema.registerSimple(map, "SnowMan");
      schema.registerSimple(map, "Ozelot");
      schema.registerSimple(map, "VillagerGolem");
      schema.register(map, "EntityHorse", (name) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.registerSimple(map, "Rabbit");
      schema.register(map, "Villager", (name) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(TypeReferences.VILLAGER_TRADE.in(schema))));
      });
      schema.registerSimple(map, "EnderCrystal");
      schema.register(map, "AreaEffectCloud", (string) -> {
         return DSL.optionalFields("Particle", TypeReferences.PARTICLE.in(schema));
      });
      schema.registerSimple(map, "ShulkerBullet");
      schema.registerSimple(map, "DragonFireball");
      schema.registerSimple(map, "Shulker");
      return map;
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = Maps.newHashMap();
      targetItems(schema, map, "Furnace");
      targetItems(schema, map, "Chest");
      schema.registerSimple(map, "EnderChest");
      schema.register(map, "RecordPlayer", (name) -> {
         return DSL.optionalFields("RecordItem", TypeReferences.ITEM_STACK.in(schema));
      });
      targetItems(schema, map, "Trap");
      targetItems(schema, map, "Dropper");
      schema.register(map, "Sign", () -> {
         return method_66194(schema);
      });
      schema.register(map, "MobSpawner", (name) -> {
         return TypeReferences.UNTAGGED_SPAWNER.in(schema);
      });
      schema.registerSimple(map, "Music");
      schema.registerSimple(map, "Piston");
      targetItems(schema, map, "Cauldron");
      schema.registerSimple(map, "EnchantTable");
      schema.registerSimple(map, "Airportal");
      schema.register(map, "Control", () -> {
         return DSL.optionalFields("LastOutput", TypeReferences.TEXT_COMPONENT.in(schema));
      });
      schema.registerSimple(map, "Beacon");
      schema.register(map, "Skull", () -> {
         return DSL.optionalFields("custom_name", TypeReferences.TEXT_COMPONENT.in(schema));
      });
      schema.registerSimple(map, "DLDetector");
      targetItems(schema, map, "Hopper");
      schema.registerSimple(map, "Comparator");
      schema.register(map, "FlowerPot", (name) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)));
      });
      schema.register(map, "Banner", () -> {
         return DSL.optionalFields("CustomName", TypeReferences.TEXT_COMPONENT.in(schema));
      });
      schema.registerSimple(map, "Structure");
      schema.registerSimple(map, "EndGateway");
      return map;
   }

   public static TypeTemplate method_66194(Schema schema) {
      return DSL.optionalFields(new Pair[]{Pair.of("Text1", TypeReferences.TEXT_COMPONENT.in(schema)), Pair.of("Text2", TypeReferences.TEXT_COMPONENT.in(schema)), Pair.of("Text3", TypeReferences.TEXT_COMPONENT.in(schema)), Pair.of("Text4", TypeReferences.TEXT_COMPONENT.in(schema)), Pair.of("FilteredText1", TypeReferences.TEXT_COMPONENT.in(schema)), Pair.of("FilteredText2", TypeReferences.TEXT_COMPONENT.in(schema)), Pair.of("FilteredText3", TypeReferences.TEXT_COMPONENT.in(schema)), Pair.of("FilteredText4", TypeReferences.TEXT_COMPONENT.in(schema))});
   }

   public void registerTypes(Schema schema, Map entityTypes, Map blockEntityTypes) {
      schema.registerType(false, TypeReferences.LEVEL, () -> {
         return DSL.optionalFields("CustomBossEvents", DSL.compoundList(DSL.optionalFields("Name", TypeReferences.TEXT_COMPONENT.in(schema))), TypeReferences.LIGHTWEIGHT_LEVEL.in(schema));
      });
      schema.registerType(false, TypeReferences.LIGHTWEIGHT_LEVEL, DSL::remainder);
      schema.registerType(false, TypeReferences.PLAYER, () -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
      schema.registerType(false, TypeReferences.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(DSL.or(TypeReferences.BLOCK_ENTITY.in(schema), DSL.remainder())), "TileTicks", DSL.list(DSL.fields("i", TypeReferences.BLOCK_NAME.in(schema)))));
      });
      schema.registerType(true, TypeReferences.BLOCK_ENTITY, () -> {
         return DSL.optionalFields("components", TypeReferences.DATA_COMPONENTS.in(schema), DSL.taggedChoiceLazy("id", DSL.string(), blockEntityTypes));
      });
      schema.registerType(true, TypeReferences.ENTITY_TREE, () -> {
         return DSL.optionalFields("Riding", TypeReferences.ENTITY_TREE.in(schema), TypeReferences.ENTITY.in(schema));
      });
      schema.registerType(false, TypeReferences.ENTITY_NAME, () -> {
         return DSL.constType(IdentifierNormalizingSchema.getIdentifierType());
      });
      schema.registerType(true, TypeReferences.ENTITY, () -> {
         return DSL.and(TypeReferences.ENTITY_EQUIPMENT.in(schema), DSL.optionalFields("CustomName", DSL.constType(DSL.string()), DSL.taggedChoiceLazy("id", DSL.string(), entityTypes)));
      });
      schema.registerType(true, TypeReferences.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), TypeReferences.ITEM_NAME.in(schema)), "tag", method_66195(schema)), field_5747, HookFunction.IDENTITY);
      });
      schema.registerType(false, TypeReferences.OPTIONS, DSL::remainder);
      schema.registerType(false, TypeReferences.BLOCK_NAME, () -> {
         return DSL.or(DSL.constType(DSL.intType()), DSL.constType(IdentifierNormalizingSchema.getIdentifierType()));
      });
      schema.registerType(false, TypeReferences.ITEM_NAME, () -> {
         return DSL.constType(IdentifierNormalizingSchema.getIdentifierType());
      });
      schema.registerType(false, TypeReferences.STATS, DSL::remainder);
      schema.registerType(false, TypeReferences.SAVED_DATA_COMMAND_STORAGE, DSL::remainder);
      schema.registerType(false, TypeReferences.TICKETS_SAVED_DATA, DSL::remainder);
      schema.registerType(false, TypeReferences.SAVED_DATA_MAP_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("banners", DSL.list(DSL.optionalFields("Name", TypeReferences.TEXT_COMPONENT.in(schema)))));
      });
      schema.registerType(false, TypeReferences.SAVED_DATA_IDCOUNTS, DSL::remainder);
      schema.registerType(false, TypeReferences.SAVED_DATA_RAIDS, DSL::remainder);
      schema.registerType(false, TypeReferences.SAVED_DATA_RANDOM_SEQUENCES, DSL::remainder);
      schema.registerType(false, TypeReferences.SAVED_DATA_SCOREBOARD, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Objectives", DSL.list(TypeReferences.OBJECTIVE.in(schema)), "Teams", DSL.list(TypeReferences.TEAM.in(schema)), "PlayerScores", DSL.list(DSL.optionalFields("display", TypeReferences.TEXT_COMPONENT.in(schema)))));
      });
      schema.registerType(false, TypeReferences.SAVED_DATA_STRUCTURE_FEATURE_INDICES, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(TypeReferences.STRUCTURE_FEATURE.in(schema))));
      });
      schema.registerType(false, TypeReferences.STRUCTURE_FEATURE, DSL::remainder);
      schema.registerType(false, TypeReferences.OBJECTIVE, DSL::remainder);
      schema.registerType(false, TypeReferences.TEAM, () -> {
         return DSL.optionalFields("MemberNamePrefix", TypeReferences.TEXT_COMPONENT.in(schema), "MemberNameSuffix", TypeReferences.TEXT_COMPONENT.in(schema), "DisplayName", TypeReferences.TEXT_COMPONENT.in(schema));
      });
      schema.registerType(true, TypeReferences.UNTAGGED_SPAWNER, DSL::remainder);
      schema.registerType(false, TypeReferences.POI_CHUNK, DSL::remainder);
      schema.registerType(false, TypeReferences.WORLD_GEN_SETTINGS, DSL::remainder);
      schema.registerType(false, TypeReferences.ENTITY_CHUNK, () -> {
         return DSL.optionalFields("Entities", DSL.list(TypeReferences.ENTITY_TREE.in(schema)));
      });
      schema.registerType(true, TypeReferences.DATA_COMPONENTS, DSL::remainder);
      schema.registerType(true, TypeReferences.VILLAGER_TRADE, () -> {
         return DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(schema), "buyB", TypeReferences.ITEM_STACK.in(schema), "sell", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.registerType(true, TypeReferences.PARTICLE, () -> {
         return DSL.constType(DSL.string());
      });
      schema.registerType(true, TypeReferences.TEXT_COMPONENT, () -> {
         return DSL.constType(DSL.string());
      });
      schema.registerType(false, TypeReferences.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TREE.in(schema))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(schema))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(schema)));
      });
      schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
      schema.registerType(false, TypeReferences.FLAT_BLOCK_STATE, DSL::remainder);
      schema.registerType(true, TypeReferences.ENTITY_EQUIPMENT, () -> {
         return DSL.optional(DSL.field("Equipment", DSL.list(TypeReferences.ITEM_STACK.in(schema))));
      });
   }

   public static TypeTemplate method_66195(Schema schema) {
      return DSL.optionalFields(new Pair[]{Pair.of("EntityTag", TypeReferences.ENTITY_TREE.in(schema)), Pair.of("BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(schema)), Pair.of("CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(schema))), Pair.of("CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(schema))), Pair.of("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema))), Pair.of("ChargedProjectiles", DSL.list(TypeReferences.ITEM_STACK.in(schema))), Pair.of("pages", DSL.list(TypeReferences.TEXT_COMPONENT.in(schema))), Pair.of("filtered_pages", DSL.compoundList(TypeReferences.TEXT_COMPONENT.in(schema))), Pair.of("display", DSL.optionalFields("Name", TypeReferences.TEXT_COMPONENT.in(schema), "Lore", DSL.list(TypeReferences.TEXT_COMPONENT.in(schema))))});
   }

   protected static Object updateBlockEntityTags(Dynamic stack, Map renames, Map map) {
      return stack.update("tag", (tag) -> {
         return tag.update("BlockEntityTag", (blockEntityTag) -> {
            String string = (String)stack.get("id").asString().result().map(IdentifierNormalizingSchema::normalize).orElse("minecraft:air");
            if (!"minecraft:air".equals(string)) {
               String string2 = (String)renames.get(string);
               if (string2 != null) {
                  return blockEntityTag.set("id", stack.createString(string2));
               }

               LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", string);
            }

            return blockEntityTag;
         }).update("EntityTag", (entityTag) -> {
            if (entityTag.get("id").result().isPresent()) {
               return entityTag;
            } else {
               String string = IdentifierNormalizingSchema.normalize(stack.get("id").asString(""));
               String string2 = (String)map.get(string);
               return string2 != null ? entityTag.set("id", stack.createString(string2)) : entityTag;
            }
         });
      }).getValue();
   }
}
