/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.WoodType
 *  net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer$AttachmentType
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.util.Identifier;

/*
 * Duplicate member names - consider using --renamedupmembers true
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class EntityModelLayers {
    private static final String MAIN = "main";
    private static final Set<EntityModelLayer> LAYERS = Sets.newHashSet();
    public static final EntityModelLayer ACACIA_BOAT = EntityModelLayers.registerMain((String)"boat/acacia");
    public static final EntityModelLayer ACACIA_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/acacia");
    public static final EntityModelLayer ALLAY = EntityModelLayers.registerMain((String)"allay");
    public static final EntityModelLayer ARMADILLO = EntityModelLayers.registerMain((String)"armadillo");
    public static final EntityModelLayer ARMADILLO_BABY = EntityModelLayers.registerMain((String)"armadillo_baby");
    public static final EntityModelLayer ARMOR_STAND = EntityModelLayers.registerMain((String)"armor_stand");
    public static final EquipmentModelData<EntityModelLayer> ARMOR_STAND_EQUIPMENT = EntityModelLayers.registerEquipment((String)"armor_stand");
    public static final EntityModelLayer ARMOR_STAND_SMALL = EntityModelLayers.registerMain((String)"armor_stand_small");
    public static final EquipmentModelData<EntityModelLayer> SMALL_ARMOR_STAND_EQUIPMENT = EntityModelLayers.registerEquipment((String)"armor_stand_small");
    public static final EntityModelLayer ARROW = EntityModelLayers.registerMain((String)"arrow");
    public static final EntityModelLayer AXOLOTL = EntityModelLayers.registerMain((String)"axolotl");
    public static final EntityModelLayer AXOLOTL_BABY = EntityModelLayers.registerMain((String)"axolotl_baby");
    public static final EntityModelLayer BAMBOO_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/bamboo");
    public static final EntityModelLayer BAMBOO_BOAT = EntityModelLayers.registerMain((String)"boat/bamboo");
    public static final EntityModelLayer STANDING_BANNER = EntityModelLayers.registerMain((String)"standing_banner");
    public static final EntityModelLayer STANDING_BANNER_FLAG = EntityModelLayers.register((String)"standing_banner", (String)"flag");
    public static final EntityModelLayer WALL_BANNER = EntityModelLayers.registerMain((String)"wall_banner");
    public static final EntityModelLayer WALL_BANNER_FLAG = EntityModelLayers.register((String)"wall_banner", (String)"flag");
    public static final EntityModelLayer BAT = EntityModelLayers.registerMain((String)"bat");
    public static final EntityModelLayer BED_FOOT = EntityModelLayers.registerMain((String)"bed_foot");
    public static final EntityModelLayer BED_HEAD = EntityModelLayers.registerMain((String)"bed_head");
    public static final EntityModelLayer BEE = EntityModelLayers.registerMain((String)"bee");
    public static final EntityModelLayer BEE_BABY = EntityModelLayers.registerMain((String)"bee_baby");
    public static final EntityModelLayer BEE_STINGER = EntityModelLayers.registerMain((String)"bee_stinger");
    public static final EntityModelLayer BELL = EntityModelLayers.registerMain((String)"bell");
    public static final EntityModelLayer BIRCH_BOAT = EntityModelLayers.registerMain((String)"boat/birch");
    public static final EntityModelLayer BIRCH_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/birch");
    public static final EntityModelLayer BLAZE = EntityModelLayers.registerMain((String)"blaze");
    public static final EntityModelLayer BOAT = EntityModelLayers.register((String)"boat", (String)"water_patch");
    public static final EntityModelLayer BOGGED = EntityModelLayers.registerMain((String)"bogged");
    public static final EquipmentModelData<EntityModelLayer> BOGGED_EQUIPMENT = EntityModelLayers.registerEquipment((String)"bogged");
    public static final EntityModelLayer BOGGED_OUTER = EntityModelLayers.register((String)"bogged", (String)"outer");
    public static final EntityModelLayer BOOK = EntityModelLayers.registerMain((String)"book");
    public static final EntityModelLayer BREEZE = EntityModelLayers.registerMain((String)"breeze");
    public static final EntityModelLayer BREEZE_WIND = EntityModelLayers.register((String)"breeze", (String)"wind");
    public static final EntityModelLayer BREEZE_EYES = EntityModelLayers.register((String)"breeze", (String)"eyes");
    public static final EntityModelLayer CAMEL = EntityModelLayers.registerMain((String)"camel");
    public static final EntityModelLayer CAMEL_BABY = EntityModelLayers.registerMain((String)"camel_baby");
    public static final EntityModelLayer CAMEL_SADDLE = EntityModelLayers.register((String)"camel", (String)"saddle");
    public static final EntityModelLayer CAMEL_BABY_SADDLE = EntityModelLayers.register((String)"camel_baby", (String)"saddle");
    public static final EntityModelLayer CAMEL_HUSK = EntityModelLayers.register((String)"camel_husk", (String)"saddle");
    public static final EntityModelLayer CAMEL_HUSK_BABY = EntityModelLayers.register((String)"camel_husk_baby", (String)"saddle");
    public static final EntityModelLayer CAT = EntityModelLayers.registerMain((String)"cat");
    public static final EntityModelLayer CAT_BABY = EntityModelLayers.registerMain((String)"cat_baby");
    public static final EntityModelLayer CAT_BABY_COLLAR = EntityModelLayers.register((String)"cat_baby", (String)"collar");
    public static final EntityModelLayer CAT_COLLAR = EntityModelLayers.register((String)"cat", (String)"collar");
    public static final EntityModelLayer CAVE_SPIDER = EntityModelLayers.registerMain((String)"cave_spider");
    public static final EntityModelLayer CHERRY_BOAT = EntityModelLayers.registerMain((String)"boat/cherry");
    public static final EntityModelLayer CHERRY_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/cherry");
    public static final EntityModelLayer CHEST = EntityModelLayers.registerMain((String)"chest");
    public static final EntityModelLayer CHEST_MINECART = EntityModelLayers.registerMain((String)"chest_minecart");
    public static final EntityModelLayer CHICKEN = EntityModelLayers.registerMain((String)"chicken");
    public static final EntityModelLayer CHICKEN_BABY = EntityModelLayers.registerMain((String)"chicken_baby");
    public static final EntityModelLayer COD = EntityModelLayers.registerMain((String)"cod");
    public static final EntityModelLayer COLD_CHICKEN = EntityModelLayers.registerMain((String)"cold_chicken");
    public static final EntityModelLayer COLD_CHICKEN_BABY = EntityModelLayers.registerMain((String)"cold_chicken_baby");
    public static final EntityModelLayer COLD_COW = EntityModelLayers.registerMain((String)"cold_cow");
    public static final EntityModelLayer COLD_COW_BABY = EntityModelLayers.registerMain((String)"cold_cow_baby");
    public static final EntityModelLayer COLD_PIG = EntityModelLayers.registerMain((String)"cold_pig");
    public static final EntityModelLayer COLD_PIG_BABY = EntityModelLayers.registerMain((String)"cold_pig_baby");
    public static final EntityModelLayer COMMAND_BLOCK_MINECART = EntityModelLayers.registerMain((String)"command_block_minecart");
    public static final EntityModelLayer CONDUIT = EntityModelLayers.register((String)"conduit", (String)"cage");
    public static final EntityModelLayer CONDUIT_EYE = EntityModelLayers.register((String)"conduit", (String)"eye");
    public static final EntityModelLayer CONDUIT_SHELL = EntityModelLayers.register((String)"conduit", (String)"shell");
    public static final EntityModelLayer CONDUIT_WIND = EntityModelLayers.register((String)"conduit", (String)"wind");
    public static final EntityModelLayer COPPER_GOLEM = EntityModelLayers.registerMain((String)"copper_golem");
    public static final EntityModelLayer COPPER_GOLEM_EYES = EntityModelLayers.register((String)"copper_golem", (String)"eyes");
    public static final EntityModelLayer COPPER_GOLEM_RUNNING = EntityModelLayers.registerMain((String)"copper_golem_running");
    public static final EntityModelLayer COPPER_GOLEM_SITTING = EntityModelLayers.registerMain((String)"copper_golem_sitting");
    public static final EntityModelLayer COPPER_GOLEM_STAR = EntityModelLayers.registerMain((String)"copper_golem_star");
    public static final EntityModelLayer ZOMBIE_NAUTILUS_CORAL = EntityModelLayers.registerMain((String)"zombie_nautilus_coral");
    public static final EntityModelLayer COW = EntityModelLayers.registerMain((String)"cow");
    public static final EntityModelLayer COW_BABY = EntityModelLayers.registerMain((String)"cow_baby");
    public static final EntityModelLayer CREAKING = EntityModelLayers.registerMain((String)"creaking");
    public static final EntityModelLayer CREAKING_EYES = EntityModelLayers.register((String)"creaking", (String)"eyes");
    public static final EntityModelLayer CREEPER = EntityModelLayers.registerMain((String)"creeper");
    public static final EntityModelLayer CREEPER_ARMOR = EntityModelLayers.register((String)"creeper", (String)"armor");
    public static final EntityModelLayer CREEPER_HEAD = EntityModelLayers.registerMain((String)"creeper_head");
    public static final EntityModelLayer DARK_OAK_BOAT = EntityModelLayers.registerMain((String)"boat/dark_oak");
    public static final EntityModelLayer DARK_OAK_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/dark_oak");
    public static final EntityModelLayer DECORATED_POT_BASE = EntityModelLayers.registerMain((String)"decorated_pot_base");
    public static final EntityModelLayer DECORATED_POT_SIDES = EntityModelLayers.registerMain((String)"decorated_pot_sides");
    public static final EntityModelLayer DOLPHIN = EntityModelLayers.registerMain((String)"dolphin");
    public static final EntityModelLayer DOLPHIN_BABY = EntityModelLayers.registerMain((String)"dolphin_baby");
    public static final EntityModelLayer DONKEY = EntityModelLayers.registerMain((String)"donkey");
    public static final EntityModelLayer DONKEY_BABY = EntityModelLayers.registerMain((String)"donkey_baby");
    public static final EntityModelLayer DONKEY_SADDLE = EntityModelLayers.register((String)"donkey", (String)"saddle");
    public static final EntityModelLayer DONKEY_BABY_SADDLE = EntityModelLayers.register((String)"donkey_baby", (String)"saddle");
    public static final EntityModelLayer DOUBLE_CHEST_LEFT = EntityModelLayers.registerMain((String)"double_chest_left");
    public static final EntityModelLayer DOUBLE_CHEST_RIGHT = EntityModelLayers.registerMain((String)"double_chest_right");
    public static final EntityModelLayer DRAGON_SKULL = EntityModelLayers.registerMain((String)"dragon_skull");
    public static final EntityModelLayer DROWNED = EntityModelLayers.registerMain((String)"drowned");
    public static final EntityModelLayer DROWNED_BABY = EntityModelLayers.registerMain((String)"drowned_baby");
    public static final EquipmentModelData<EntityModelLayer> DROWNED_BABY_EQUIPMENT = EntityModelLayers.registerEquipment((String)"drowned_baby");
    public static final EntityModelLayer DROWNED_BABY_OUTER = EntityModelLayers.register((String)"drowned_baby", (String)"outer");
    public static final EquipmentModelData<EntityModelLayer> DROWNED_EQUIPMENT = EntityModelLayers.registerEquipment((String)"drowned");
    public static final EntityModelLayer DROWNED_OUTER = EntityModelLayers.register((String)"drowned", (String)"outer");
    public static final EntityModelLayer ELDER_GUARDIAN = EntityModelLayers.registerMain((String)"elder_guardian");
    public static final EntityModelLayer ELYTRA = EntityModelLayers.registerMain((String)"elytra");
    public static final EntityModelLayer ELYTRA_BABY = EntityModelLayers.registerMain((String)"elytra_baby");
    public static final EntityModelLayer ENDERMAN = EntityModelLayers.registerMain((String)"enderman");
    public static final EntityModelLayer ENDERMITE = EntityModelLayers.registerMain((String)"endermite");
    public static final EntityModelLayer ENDER_DRAGON = EntityModelLayers.registerMain((String)"ender_dragon");
    public static final EntityModelLayer END_CRYSTAL = EntityModelLayers.registerMain((String)"end_crystal");
    public static final EntityModelLayer EVOKER = EntityModelLayers.registerMain((String)"evoker");
    public static final EntityModelLayer EVOKER_FANGS = EntityModelLayers.registerMain((String)"evoker_fangs");
    public static final EntityModelLayer FOX = EntityModelLayers.registerMain((String)"fox");
    public static final EntityModelLayer FOX_BABY = EntityModelLayers.registerMain((String)"fox_baby");
    public static final EntityModelLayer FROG = EntityModelLayers.registerMain((String)"frog");
    public static final EntityModelLayer FURNACE_MINECART = EntityModelLayers.registerMain((String)"furnace_minecart");
    public static final EntityModelLayer GHAST = EntityModelLayers.registerMain((String)"ghast");
    public static final EntityModelLayer GIANT = EntityModelLayers.registerMain((String)"giant");
    public static final EquipmentModelData<EntityModelLayer> GIANT_EQUIPMENT = EntityModelLayers.registerEquipment((String)"giant");
    public static final EntityModelLayer GLOW_SQUID = EntityModelLayers.registerMain((String)"glow_squid");
    public static final EntityModelLayer GLOW_SQUID_BABY = EntityModelLayers.registerMain((String)"glow_squid_baby");
    public static final EntityModelLayer GOAT = EntityModelLayers.registerMain((String)"goat");
    public static final EntityModelLayer GOAT_BABY = EntityModelLayers.registerMain((String)"goat_baby");
    public static final EntityModelLayer GUARDIAN = EntityModelLayers.registerMain((String)"guardian");
    public static final EntityModelLayer HAPPY_GHAST = EntityModelLayers.registerMain((String)"happy_ghast");
    public static final EntityModelLayer HAPPY_GHAST_BABY = EntityModelLayers.registerMain((String)"happy_ghast_baby");
    public static final EntityModelLayer HAPPY_GHAST_HARNESS = EntityModelLayers.registerMain((String)"happy_ghast_harness");
    public static final EntityModelLayer HAPPY_GHAST_BABY_HARNESS = EntityModelLayers.registerMain((String)"happy_ghast_baby_harness");
    public static final EntityModelLayer HAPPY_GHAST_ROPES = EntityModelLayers.registerMain((String)"happy_ghast_ropes");
    public static final EntityModelLayer HAPPY_GHAST_BABY_ROPES = EntityModelLayers.registerMain((String)"happy_ghast_baby_ropes");
    public static final EntityModelLayer HOGLIN = EntityModelLayers.registerMain((String)"hoglin");
    public static final EntityModelLayer HOGLIN_BABY = EntityModelLayers.registerMain((String)"hoglin_baby");
    public static final EntityModelLayer HOPPER_MINECART = EntityModelLayers.registerMain((String)"hopper_minecart");
    public static final EntityModelLayer HORSE = EntityModelLayers.registerMain((String)"horse");
    public static final EntityModelLayer HORSE_ARMOR = EntityModelLayers.registerMain((String)"horse_armor");
    public static final EntityModelLayer HORSE_SADDLE = EntityModelLayers.register((String)"horse", (String)"saddle");
    public static final EntityModelLayer HORSE_BABY = EntityModelLayers.registerMain((String)"horse_baby");
    public static final EntityModelLayer HORSE_ARMOR_BABY = EntityModelLayers.registerMain((String)"horse_armor_baby");
    public static final EntityModelLayer HORSE_BABY_SADDLE = EntityModelLayers.register((String)"horse_baby", (String)"saddle");
    public static final EntityModelLayer HUSK = EntityModelLayers.registerMain((String)"husk");
    public static final EntityModelLayer HUSK_BABY = EntityModelLayers.registerMain((String)"husk_baby");
    public static final EquipmentModelData<EntityModelLayer> HUSK_BABY_EQUIPMENT = EntityModelLayers.registerEquipment((String)"husk_baby");
    public static final EquipmentModelData<EntityModelLayer> HUSK_EQUIPMENT = EntityModelLayers.registerEquipment((String)"husk");
    public static final EntityModelLayer ILLUSIONER = EntityModelLayers.registerMain((String)"illusioner");
    public static final EntityModelLayer IRON_GOLEM = EntityModelLayers.registerMain((String)"iron_golem");
    public static final EntityModelLayer JUNGLE_BOAT = EntityModelLayers.registerMain((String)"boat/jungle");
    public static final EntityModelLayer JUNGLE_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/jungle");
    public static final EntityModelLayer LEASH_KNOT = EntityModelLayers.registerMain((String)"leash_knot");
    public static final EntityModelLayer LLAMA = EntityModelLayers.registerMain((String)"llama");
    public static final EntityModelLayer LLAMA_BABY = EntityModelLayers.registerMain((String)"llama_baby");
    public static final EntityModelLayer LLAMA_BABY_DECOR = EntityModelLayers.register((String)"llama_baby", (String)"decor");
    public static final EntityModelLayer LLAMA_DECOR = EntityModelLayers.register((String)"llama", (String)"decor");
    public static final EntityModelLayer LLAMA_SPIT = EntityModelLayers.registerMain((String)"llama_spit");
    public static final EntityModelLayer MAGMA_CUBE = EntityModelLayers.registerMain((String)"magma_cube");
    public static final EntityModelLayer MANGROVE_BOAT = EntityModelLayers.registerMain((String)"boat/mangrove");
    public static final EntityModelLayer MANGROVE_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/mangrove");
    public static final EntityModelLayer MINECART = EntityModelLayers.registerMain((String)"minecart");
    public static final EntityModelLayer MOOSHROOM = EntityModelLayers.registerMain((String)"mooshroom");
    public static final EntityModelLayer MOOSHROOM_BABY = EntityModelLayers.registerMain((String)"mooshroom_baby");
    public static final EntityModelLayer MULE = EntityModelLayers.registerMain((String)"mule");
    public static final EntityModelLayer MULE_BABY = EntityModelLayers.registerMain((String)"mule_baby");
    public static final EntityModelLayer MULE_SADDLE = EntityModelLayers.register((String)"mule", (String)"saddle");
    public static final EntityModelLayer MULE_BABY_SADDLE = EntityModelLayers.register((String)"mule_baby", (String)"saddle");
    public static final EntityModelLayer NAUTILUS = EntityModelLayers.registerMain((String)"nautilus");
    public static final EntityModelLayer NAUTILUS_BABY = EntityModelLayers.registerMain((String)"nautilus_baby");
    public static final EntityModelLayer NAUTILUS_SADDLE = EntityModelLayers.register((String)"nautilus", (String)"saddle");
    public static final EntityModelLayer NAUTILUS_ARMOR = EntityModelLayers.registerMain((String)"nautilus_armor");
    public static final EntityModelLayer OAK_BOAT = EntityModelLayers.registerMain((String)"boat/oak");
    public static final EntityModelLayer OAK_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/oak");
    public static final EntityModelLayer OCELOT = EntityModelLayers.registerMain((String)"ocelot");
    public static final EntityModelLayer OCELOT_BABY = EntityModelLayers.registerMain((String)"ocelot_baby");
    public static final EntityModelLayer PALE_OAK_BOAT = EntityModelLayers.registerMain((String)"boat/pale_oak");
    public static final EntityModelLayer PALE_OAK_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/pale_oak");
    public static final EntityModelLayer PANDA = EntityModelLayers.registerMain((String)"panda");
    public static final EntityModelLayer PANDA_BABY = EntityModelLayers.registerMain((String)"panda_baby");
    public static final EntityModelLayer PARCHED = EntityModelLayers.registerMain((String)"parched");
    public static final EquipmentModelData<EntityModelLayer> PARCHED_EQUIPMENT = EntityModelLayers.registerEquipment((String)"parched");
    public static final EntityModelLayer PARCHED_OUTER = EntityModelLayers.register((String)"parched", (String)"outer");
    public static final EntityModelLayer PARROT = EntityModelLayers.registerMain((String)"parrot");
    public static final EntityModelLayer PHANTOM = EntityModelLayers.registerMain((String)"phantom");
    public static final EntityModelLayer PIG = EntityModelLayers.registerMain((String)"pig");
    public static final EntityModelLayer PIGLIN = EntityModelLayers.registerMain((String)"piglin");
    public static final EntityModelLayer PIGLIN_BABY = EntityModelLayers.registerMain((String)"piglin_baby");
    public static final EquipmentModelData<EntityModelLayer> PIGLIN_BABY_EQUIPMENT = EntityModelLayers.registerEquipment((String)"piglin_baby");
    public static final EntityModelLayer PIGLIN_BRUTE = EntityModelLayers.registerMain((String)"piglin_brute");
    public static final EquipmentModelData<EntityModelLayer> PIGLIN_BRUTE_EQUIPMENT = EntityModelLayers.registerEquipment((String)"piglin_brute");
    public static final EntityModelLayer PIGLIN_HEAD = EntityModelLayers.registerMain((String)"piglin_head");
    public static final EquipmentModelData<EntityModelLayer> PIGLIN_EQUIPMENT = EntityModelLayers.registerEquipment((String)"piglin");
    public static final EntityModelLayer PIG_BABY = EntityModelLayers.registerMain((String)"pig_baby");
    public static final EntityModelLayer PIG_BABY_SADDLE = EntityModelLayers.register((String)"pig_baby", (String)"saddle");
    public static final EntityModelLayer PIG_SADDLE = EntityModelLayers.register((String)"pig", (String)"saddle");
    public static final EntityModelLayer PILLAGER = EntityModelLayers.registerMain((String)"pillager");
    public static final EntityModelLayer PLAYER = EntityModelLayers.registerMain((String)"player");
    public static final EntityModelLayer PLAYER_CAPE = EntityModelLayers.register((String)"player", (String)"cape");
    public static final EntityModelLayer PLAYER_EARS = EntityModelLayers.register((String)"player", (String)"ears");
    public static final EntityModelLayer PLAYER_HEAD = EntityModelLayers.registerMain((String)"player_head");
    public static final EquipmentModelData<EntityModelLayer> PLAYER_EQUIPMENT = EntityModelLayers.registerEquipment((String)"player");
    public static final EntityModelLayer PLAYER_SLIM = EntityModelLayers.registerMain((String)"player_slim");
    public static final EquipmentModelData<EntityModelLayer> PLAYER_SLIM;
    public static final EntityModelLayer SPIN_ATTACK;
    public static final EntityModelLayer POLAR_BEAR;
    public static final EntityModelLayer POLAR_BEAR_BABY;
    public static final EntityModelLayer PUFFERFISH_BIG;
    public static final EntityModelLayer PUFFERFISH_MEDIUM;
    public static final EntityModelLayer PUFFERFISH_SMALL;
    public static final EntityModelLayer RABBIT;
    public static final EntityModelLayer RABBIT_BABY;
    public static final EntityModelLayer RAVAGER;
    public static final EntityModelLayer SALMON;
    public static final EntityModelLayer SALMON_LARGE;
    public static final EntityModelLayer SALMON_SMALL;
    public static final EntityModelLayer SHEEP;
    public static final EntityModelLayer SHEEP_BABY;
    public static final EntityModelLayer SHEEP_BABY_WOOL;
    public static final EntityModelLayer SHEEP_WOOL;
    public static final EntityModelLayer SHEEP_WOOL_UNDERCOAT;
    public static final EntityModelLayer SHEEP_BABY_WOOL_UNDERCOAT;
    public static final EntityModelLayer SHIELD;
    public static final EntityModelLayer SHULKER;
    public static final EntityModelLayer SHULKER_BOX;
    public static final EntityModelLayer SHULKER_BULLET;
    public static final EntityModelLayer SILVERFISH;
    public static final EntityModelLayer SKELETON;
    public static final EntityModelLayer SKELETON_HORSE;
    public static final EntityModelLayer SKELETON_HORSE_BABY;
    public static final EntityModelLayer SKELETON_HORSE_SADDLE;
    public static final EntityModelLayer SKELETON_HORSE_BABY_SADDLE;
    public static final EquipmentModelData<EntityModelLayer> SKELETON_EQUIPMENT;
    public static final EntityModelLayer SKELETON_SKULL;
    public static final EntityModelLayer SLIME;
    public static final EntityModelLayer SLIME_OUTER;
    public static final EntityModelLayer SNIFFER;
    public static final EntityModelLayer SNIFFER_BABY;
    public static final EntityModelLayer SNOW_GOLEM;
    public static final EntityModelLayer SPAWNER_MINECART;
    public static final EntityModelLayer SPIDER;
    public static final EntityModelLayer SPRUCE_BOAT;
    public static final EntityModelLayer SPRUCE_CHEST_BOAT;
    public static final EntityModelLayer SQUID;
    public static final EntityModelLayer SQUID_BABY;
    public static final EntityModelLayer STRAY;
    public static final EquipmentModelData<EntityModelLayer> STRAY_EQUIPMENT;
    public static final EntityModelLayer STRAY_OUTER;
    public static final EntityModelLayer STRIDER;
    public static final EntityModelLayer STRIDER_SADDLE;
    public static final EntityModelLayer STRIDER_BABY;
    public static final EntityModelLayer STRIDER_BABY_SADDLE;
    public static final EntityModelLayer TADPOLE;
    public static final EntityModelLayer TNT_MINECART;
    public static final EntityModelLayer TRADER_LLAMA;
    public static final EntityModelLayer TRADER_LLAMA_BABY;
    public static final EntityModelLayer TRIDENT;
    public static final EntityModelLayer TROPICAL_FISH_LARGE;
    public static final EntityModelLayer TROPICAL_FISH_LARGE_PATTERN;
    public static final EntityModelLayer TROPICAL_FISH_SMALL;
    public static final EntityModelLayer TROPICAL_FISH_SMALL_PATTERN;
    public static final EntityModelLayer TURTLE;
    public static final EntityModelLayer TURTLE_BABY;
    public static final EntityModelLayer UNDEAD_HORSE_ARMOR;
    public static final EntityModelLayer UNDEAD_HORSE_BABY_ARMOR;
    public static final EntityModelLayer VEX;
    public static final EntityModelLayer VILLAGER;
    public static final EntityModelLayer VILLAGER_NO_HAT;
    public static final EntityModelLayer VILLAGER_BABY;
    public static final EntityModelLayer VILLAGER_BABY_NO_HAT;
    public static final EntityModelLayer VINDICATOR;
    public static final EntityModelLayer WANDERING_TRADER;
    public static final EntityModelLayer WARDEN;
    public static final EntityModelLayer WARDEN_BIOLUMINESCENT;
    public static final EntityModelLayer WARDEN_PULSATING_SPOTS;
    public static final EntityModelLayer WARDEN_TENDRILS;
    public static final EntityModelLayer WARDEN_HEART;
    public static final EntityModelLayer WARM_COW;
    public static final EntityModelLayer WARM_COW_BABY;
    public static final EntityModelLayer WIND_CHARGE;
    public static final EntityModelLayer WITCH;
    public static final EntityModelLayer WITHER;
    public static final EntityModelLayer WITHER_ARMOR;
    public static final EntityModelLayer WITHER_SKELETON;
    public static final EquipmentModelData<EntityModelLayer> WITHER_SKELETON_EQUIPMENT;
    public static final EntityModelLayer WITHER_SKELETON_SKULL;
    public static final EntityModelLayer WITHER_SKULL;
    public static final EntityModelLayer WOLF;
    public static final EntityModelLayer WOLF_ARMOR;
    public static final EntityModelLayer WOLF_BABY;
    public static final EntityModelLayer WOLF_BABY_ARMOR;
    public static final EntityModelLayer ZOGLIN;
    public static final EntityModelLayer ZOGLIN_BABY;
    public static final EntityModelLayer ZOMBIE;
    public static final EntityModelLayer ZOMBIE_BABY;
    public static final EquipmentModelData<EntityModelLayer> ZOMBIE_BABY_EQUIPMENT;
    public static final EntityModelLayer ZOMBIE_HEAD;
    public static final EntityModelLayer ZOMBIE_HORSE;
    public static final EntityModelLayer ZOMBIE_HORSE_BABY;
    public static final EntityModelLayer ZOMBIE_HORSE_SADDLE;
    public static final EntityModelLayer ZOMBIE_HORSE_BABY_SADDLE;
    public static final EquipmentModelData<EntityModelLayer> ZOMBIE_EQUIPMENT;
    public static final EntityModelLayer ZOMBIE_VILLAGER;
    public static final EntityModelLayer ZOMBIE_VILLAGER_NO_HAT;
    public static final EntityModelLayer ZOMBIE_VILLAGER_BABY;
    public static final EntityModelLayer ZOMBIE_VILLAGER_BABY_NO_HAT;
    public static final EquipmentModelData<EntityModelLayer> ZOMBIE_VILLAGER_BABY_EQUIPMENT;
    public static final EquipmentModelData<EntityModelLayer> ZOMBIE_VILLAGER_EQUIPMENT;
    public static final EntityModelLayer ZOMBIFIED_PIGLIN;
    public static final EntityModelLayer ZOMBIFIED_PIGLIN_BABY;
    public static final EquipmentModelData<EntityModelLayer> ZOMBIFIED_PIGLIN_BABY_EQUIPMENT;
    public static final EquipmentModelData<EntityModelLayer> ZOMBIFIED_PIGLIN_EQUIPMENT;
    public static final EntityModelLayer ZOMBIE_NAUTILUS;

    private static EntityModelLayer registerMain(String id) {
        return EntityModelLayers.register((String)id, (String)"main");
    }

    private static EntityModelLayer register(String id, String layer) {
        EntityModelLayer entityModelLayer = EntityModelLayers.create((String)id, (String)layer);
        if (!LAYERS.add(entityModelLayer)) {
            throw new IllegalStateException("Duplicate registration for " + String.valueOf(entityModelLayer));
        }
        return entityModelLayer;
    }

    private static EntityModelLayer create(String id, String layer) {
        return new EntityModelLayer(Identifier.ofVanilla((String)id), layer);
    }

    private static EquipmentModelData<EntityModelLayer> registerEquipment(String id) {
        return new EquipmentModelData((Object)EntityModelLayers.register((String)id, (String)"helmet"), (Object)EntityModelLayers.register((String)id, (String)"chestplate"), (Object)EntityModelLayers.register((String)id, (String)"leggings"), (Object)EntityModelLayers.register((String)id, (String)"boots"));
    }

    public static EntityModelLayer createStandingSign(WoodType type) {
        return EntityModelLayers.create((String)("sign/standing/" + type.name()), (String)"main");
    }

    public static EntityModelLayer createWallSign(WoodType type) {
        return EntityModelLayers.create((String)("sign/wall/" + type.name()), (String)"main");
    }

    public static EntityModelLayer createHangingSign(WoodType type, HangingSignBlockEntityRenderer.AttachmentType attachmentType) {
        return EntityModelLayers.create((String)("hanging_sign/" + type.name() + "/" + attachmentType.asString()), (String)"main");
    }

    public static Stream<EntityModelLayer> getLayers() {
        return LAYERS.stream();
    }

    static {
        PLAYER_SLIM = EntityModelLayers.registerEquipment((String)"player_slim");
        SPIN_ATTACK = EntityModelLayers.registerMain((String)"spin_attack");
        POLAR_BEAR = EntityModelLayers.registerMain((String)"polar_bear");
        POLAR_BEAR_BABY = EntityModelLayers.registerMain((String)"polar_bear_baby");
        PUFFERFISH_BIG = EntityModelLayers.registerMain((String)"pufferfish_big");
        PUFFERFISH_MEDIUM = EntityModelLayers.registerMain((String)"pufferfish_medium");
        PUFFERFISH_SMALL = EntityModelLayers.registerMain((String)"pufferfish_small");
        RABBIT = EntityModelLayers.registerMain((String)"rabbit");
        RABBIT_BABY = EntityModelLayers.registerMain((String)"rabbit_baby");
        RAVAGER = EntityModelLayers.registerMain((String)"ravager");
        SALMON = EntityModelLayers.registerMain((String)"salmon");
        SALMON_LARGE = EntityModelLayers.registerMain((String)"salmon_large");
        SALMON_SMALL = EntityModelLayers.registerMain((String)"salmon_small");
        SHEEP = EntityModelLayers.registerMain((String)"sheep");
        SHEEP_BABY = EntityModelLayers.registerMain((String)"sheep_baby");
        SHEEP_BABY_WOOL = EntityModelLayers.register((String)"sheep_baby", (String)"wool");
        SHEEP_WOOL = EntityModelLayers.register((String)"sheep", (String)"wool");
        SHEEP_WOOL_UNDERCOAT = EntityModelLayers.register((String)"sheep", (String)"wool_undercoat");
        SHEEP_BABY_WOOL_UNDERCOAT = EntityModelLayers.register((String)"sheep_baby", (String)"wool_undercoat");
        SHIELD = EntityModelLayers.registerMain((String)"shield");
        SHULKER = EntityModelLayers.registerMain((String)"shulker");
        SHULKER_BOX = EntityModelLayers.registerMain((String)"shulker_box");
        SHULKER_BULLET = EntityModelLayers.registerMain((String)"shulker_bullet");
        SILVERFISH = EntityModelLayers.registerMain((String)"silverfish");
        SKELETON = EntityModelLayers.registerMain((String)"skeleton");
        SKELETON_HORSE = EntityModelLayers.registerMain((String)"skeleton_horse");
        SKELETON_HORSE_BABY = EntityModelLayers.registerMain((String)"skeleton_horse_baby");
        SKELETON_HORSE_SADDLE = EntityModelLayers.register((String)"skeleton_horse", (String)"saddle");
        SKELETON_HORSE_BABY_SADDLE = EntityModelLayers.register((String)"skeleton_horse_baby", (String)"saddle");
        SKELETON_EQUIPMENT = EntityModelLayers.registerEquipment((String)"skeleton");
        SKELETON_SKULL = EntityModelLayers.registerMain((String)"skeleton_skull");
        SLIME = EntityModelLayers.registerMain((String)"slime");
        SLIME_OUTER = EntityModelLayers.register((String)"slime", (String)"outer");
        SNIFFER = EntityModelLayers.registerMain((String)"sniffer");
        SNIFFER_BABY = EntityModelLayers.registerMain((String)"sniffer_baby");
        SNOW_GOLEM = EntityModelLayers.registerMain((String)"snow_golem");
        SPAWNER_MINECART = EntityModelLayers.registerMain((String)"spawner_minecart");
        SPIDER = EntityModelLayers.registerMain((String)"spider");
        SPRUCE_BOAT = EntityModelLayers.registerMain((String)"boat/spruce");
        SPRUCE_CHEST_BOAT = EntityModelLayers.registerMain((String)"chest_boat/spruce");
        SQUID = EntityModelLayers.registerMain((String)"squid");
        SQUID_BABY = EntityModelLayers.registerMain((String)"squid_baby");
        STRAY = EntityModelLayers.registerMain((String)"stray");
        STRAY_EQUIPMENT = EntityModelLayers.registerEquipment((String)"stray");
        STRAY_OUTER = EntityModelLayers.register((String)"stray", (String)"outer");
        STRIDER = EntityModelLayers.registerMain((String)"strider");
        STRIDER_SADDLE = EntityModelLayers.register((String)"strider", (String)"saddle");
        STRIDER_BABY = EntityModelLayers.registerMain((String)"strider_baby");
        STRIDER_BABY_SADDLE = EntityModelLayers.register((String)"strider_baby", (String)"saddle");
        TADPOLE = EntityModelLayers.registerMain((String)"tadpole");
        TNT_MINECART = EntityModelLayers.registerMain((String)"tnt_minecart");
        TRADER_LLAMA = EntityModelLayers.registerMain((String)"trader_llama");
        TRADER_LLAMA_BABY = EntityModelLayers.registerMain((String)"trader_llama_baby");
        TRIDENT = EntityModelLayers.registerMain((String)"trident");
        TROPICAL_FISH_LARGE = EntityModelLayers.registerMain((String)"tropical_fish_large");
        TROPICAL_FISH_LARGE_PATTERN = EntityModelLayers.register((String)"tropical_fish_large", (String)"pattern");
        TROPICAL_FISH_SMALL = EntityModelLayers.registerMain((String)"tropical_fish_small");
        TROPICAL_FISH_SMALL_PATTERN = EntityModelLayers.register((String)"tropical_fish_small", (String)"pattern");
        TURTLE = EntityModelLayers.registerMain((String)"turtle");
        TURTLE_BABY = EntityModelLayers.registerMain((String)"turtle_baby");
        UNDEAD_HORSE_ARMOR = EntityModelLayers.registerMain((String)"undead_horse_armor");
        UNDEAD_HORSE_BABY_ARMOR = EntityModelLayers.registerMain((String)"undead_horse_baby_armor");
        VEX = EntityModelLayers.registerMain((String)"vex");
        VILLAGER = EntityModelLayers.registerMain((String)"villager");
        VILLAGER_NO_HAT = EntityModelLayers.registerMain((String)"villager_no_hat");
        VILLAGER_BABY = EntityModelLayers.registerMain((String)"villager_baby");
        VILLAGER_BABY_NO_HAT = EntityModelLayers.registerMain((String)"villager_baby_no_hat");
        VINDICATOR = EntityModelLayers.registerMain((String)"vindicator");
        WANDERING_TRADER = EntityModelLayers.registerMain((String)"wandering_trader");
        WARDEN = EntityModelLayers.registerMain((String)"warden");
        WARDEN_BIOLUMINESCENT = EntityModelLayers.register((String)"warden", (String)"bioluminescent");
        WARDEN_PULSATING_SPOTS = EntityModelLayers.register((String)"warden", (String)"pulsating_spots");
        WARDEN_TENDRILS = EntityModelLayers.register((String)"warden", (String)"tendrils");
        WARDEN_HEART = EntityModelLayers.register((String)"warden", (String)"heart");
        WARM_COW = EntityModelLayers.registerMain((String)"warm_cow");
        WARM_COW_BABY = EntityModelLayers.registerMain((String)"warm_cow_baby");
        WIND_CHARGE = EntityModelLayers.registerMain((String)"wind_charge");
        WITCH = EntityModelLayers.registerMain((String)"witch");
        WITHER = EntityModelLayers.registerMain((String)"wither");
        WITHER_ARMOR = EntityModelLayers.register((String)"wither", (String)"armor");
        WITHER_SKELETON = EntityModelLayers.registerMain((String)"wither_skeleton");
        WITHER_SKELETON_EQUIPMENT = EntityModelLayers.registerEquipment((String)"wither_skeleton");
        WITHER_SKELETON_SKULL = EntityModelLayers.registerMain((String)"wither_skeleton_skull");
        WITHER_SKULL = EntityModelLayers.registerMain((String)"wither_skull");
        WOLF = EntityModelLayers.registerMain((String)"wolf");
        WOLF_ARMOR = EntityModelLayers.registerMain((String)"wolf_armor");
        WOLF_BABY = EntityModelLayers.registerMain((String)"wolf_baby");
        WOLF_BABY_ARMOR = EntityModelLayers.registerMain((String)"wolf_baby_armor");
        ZOGLIN = EntityModelLayers.registerMain((String)"zoglin");
        ZOGLIN_BABY = EntityModelLayers.registerMain((String)"zoglin_baby");
        ZOMBIE = EntityModelLayers.registerMain((String)"zombie");
        ZOMBIE_BABY = EntityModelLayers.registerMain((String)"zombie_baby");
        ZOMBIE_BABY_EQUIPMENT = EntityModelLayers.registerEquipment((String)"zombie_baby");
        ZOMBIE_HEAD = EntityModelLayers.registerMain((String)"zombie_head");
        ZOMBIE_HORSE = EntityModelLayers.registerMain((String)"zombie_horse");
        ZOMBIE_HORSE_BABY = EntityModelLayers.registerMain((String)"zombie_horse_baby");
        ZOMBIE_HORSE_SADDLE = EntityModelLayers.register((String)"zombie_horse", (String)"saddle");
        ZOMBIE_HORSE_BABY_SADDLE = EntityModelLayers.register((String)"zombie_horse_baby", (String)"saddle");
        ZOMBIE_EQUIPMENT = EntityModelLayers.registerEquipment((String)"zombie");
        ZOMBIE_VILLAGER = EntityModelLayers.registerMain((String)"zombie_villager");
        ZOMBIE_VILLAGER_NO_HAT = EntityModelLayers.registerMain((String)"zombie_villager_no_hat");
        ZOMBIE_VILLAGER_BABY = EntityModelLayers.registerMain((String)"zombie_villager_baby");
        ZOMBIE_VILLAGER_BABY_NO_HAT = EntityModelLayers.registerMain((String)"zombie_villager_baby_no_hat");
        ZOMBIE_VILLAGER_BABY_EQUIPMENT = EntityModelLayers.registerEquipment((String)"zombie_villager_baby");
        ZOMBIE_VILLAGER_EQUIPMENT = EntityModelLayers.registerEquipment((String)"zombie_villager");
        ZOMBIFIED_PIGLIN = EntityModelLayers.registerMain((String)"zombified_piglin");
        ZOMBIFIED_PIGLIN_BABY = EntityModelLayers.registerMain((String)"zombified_piglin_baby");
        ZOMBIFIED_PIGLIN_BABY_EQUIPMENT = EntityModelLayers.registerEquipment((String)"zombified_piglin_baby");
        ZOMBIFIED_PIGLIN_EQUIPMENT = EntityModelLayers.registerEquipment((String)"zombified_piglin");
        ZOMBIE_NAUTILUS = EntityModelLayers.registerMain((String)"zombie_nautilus");
    }
}

