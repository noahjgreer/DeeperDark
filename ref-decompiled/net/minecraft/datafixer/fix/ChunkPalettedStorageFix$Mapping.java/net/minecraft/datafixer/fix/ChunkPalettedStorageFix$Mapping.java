/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import net.minecraft.datafixer.FixUtil;

static class ChunkPalettedStorageFix.Mapping {
    static final BitSet field_52401 = new BitSet(256);
    static final BitSet field_52402 = new BitSet(256);
    static final Dynamic<?> PUMPKIN_STATE = FixUtil.createBlockState("minecraft:pumpkin");
    static final Dynamic<?> SNOWY_PODZOL_STATE = FixUtil.createBlockState("minecraft:podzol", Map.of("snowy", "true"));
    static final Dynamic<?> SNOWY_GRASS_BLOCK_STATE = FixUtil.createBlockState("minecraft:grass_block", Map.of("snowy", "true"));
    static final Dynamic<?> SNOWY_MYCELIUM_STATE = FixUtil.createBlockState("minecraft:mycelium", Map.of("snowy", "true"));
    static final Dynamic<?> UPPER_HALF_SUNFLOWER_STATE = FixUtil.createBlockState("minecraft:sunflower", Map.of("half", "upper"));
    static final Dynamic<?> UPPER_HALF_LILAC_STATE = FixUtil.createBlockState("minecraft:lilac", Map.of("half", "upper"));
    static final Dynamic<?> UPPER_HALF_TALL_GRASS_STATE = FixUtil.createBlockState("minecraft:tall_grass", Map.of("half", "upper"));
    static final Dynamic<?> UPPER_HALF_LARGE_FERN_STATE = FixUtil.createBlockState("minecraft:large_fern", Map.of("half", "upper"));
    static final Dynamic<?> UPPER_HALF_ROSE_BUSH_STATE = FixUtil.createBlockState("minecraft:rose_bush", Map.of("half", "upper"));
    static final Dynamic<?> UPPER_HALF_PEONY_STATE = FixUtil.createBlockState("minecraft:peony", Map.of("half", "upper"));
    static final Map<String, Dynamic<?>> PLANT_TO_FLOWER_POT_STATES = (Map)DataFixUtils.make((Object)Maps.newHashMap(), map -> {
        map.put("minecraft:air0", FixUtil.createBlockState("minecraft:flower_pot"));
        map.put("minecraft:red_flower0", FixUtil.createBlockState("minecraft:potted_poppy"));
        map.put("minecraft:red_flower1", FixUtil.createBlockState("minecraft:potted_blue_orchid"));
        map.put("minecraft:red_flower2", FixUtil.createBlockState("minecraft:potted_allium"));
        map.put("minecraft:red_flower3", FixUtil.createBlockState("minecraft:potted_azure_bluet"));
        map.put("minecraft:red_flower4", FixUtil.createBlockState("minecraft:potted_red_tulip"));
        map.put("minecraft:red_flower5", FixUtil.createBlockState("minecraft:potted_orange_tulip"));
        map.put("minecraft:red_flower6", FixUtil.createBlockState("minecraft:potted_white_tulip"));
        map.put("minecraft:red_flower7", FixUtil.createBlockState("minecraft:potted_pink_tulip"));
        map.put("minecraft:red_flower8", FixUtil.createBlockState("minecraft:potted_oxeye_daisy"));
        map.put("minecraft:yellow_flower0", FixUtil.createBlockState("minecraft:potted_dandelion"));
        map.put("minecraft:sapling0", FixUtil.createBlockState("minecraft:potted_oak_sapling"));
        map.put("minecraft:sapling1", FixUtil.createBlockState("minecraft:potted_spruce_sapling"));
        map.put("minecraft:sapling2", FixUtil.createBlockState("minecraft:potted_birch_sapling"));
        map.put("minecraft:sapling3", FixUtil.createBlockState("minecraft:potted_jungle_sapling"));
        map.put("minecraft:sapling4", FixUtil.createBlockState("minecraft:potted_acacia_sapling"));
        map.put("minecraft:sapling5", FixUtil.createBlockState("minecraft:potted_dark_oak_sapling"));
        map.put("minecraft:red_mushroom0", FixUtil.createBlockState("minecraft:potted_red_mushroom"));
        map.put("minecraft:brown_mushroom0", FixUtil.createBlockState("minecraft:potted_brown_mushroom"));
        map.put("minecraft:deadbush0", FixUtil.createBlockState("minecraft:potted_dead_bush"));
        map.put("minecraft:tallgrass2", FixUtil.createBlockState("minecraft:potted_fern"));
        map.put("minecraft:cactus0", FixUtil.createBlockState("minecraft:potted_cactus"));
    });
    static final Map<String, Dynamic<?>> SKULL_IDS_TO_STATES = (Map)DataFixUtils.make((Object)Maps.newHashMap(), map -> {
        ChunkPalettedStorageFix.Mapping.skull(map, 0, "skeleton", "skull");
        ChunkPalettedStorageFix.Mapping.skull(map, 1, "wither_skeleton", "skull");
        ChunkPalettedStorageFix.Mapping.skull(map, 2, "zombie", "head");
        ChunkPalettedStorageFix.Mapping.skull(map, 3, "player", "head");
        ChunkPalettedStorageFix.Mapping.skull(map, 4, "creeper", "head");
        ChunkPalettedStorageFix.Mapping.skull(map, 5, "dragon", "head");
    });
    static final Map<String, Dynamic<?>> DOOR_IDS_TO_STATES = (Map)DataFixUtils.make((Object)Maps.newHashMap(), map -> {
        ChunkPalettedStorageFix.Mapping.door(map, "oak_door");
        ChunkPalettedStorageFix.Mapping.door(map, "iron_door");
        ChunkPalettedStorageFix.Mapping.door(map, "spruce_door");
        ChunkPalettedStorageFix.Mapping.door(map, "birch_door");
        ChunkPalettedStorageFix.Mapping.door(map, "jungle_door");
        ChunkPalettedStorageFix.Mapping.door(map, "acacia_door");
        ChunkPalettedStorageFix.Mapping.door(map, "dark_oak_door");
    });
    static final Map<String, Dynamic<?>> NOTE_BLOCK_IDS_TO_STATES = (Map)DataFixUtils.make((Object)Maps.newHashMap(), map -> {
        for (int i = 0; i < 26; ++i) {
            map.put("true" + i, FixUtil.createBlockState("minecraft:note_block", Map.of("powered", "true", "note", String.valueOf(i))));
            map.put("false" + i, FixUtil.createBlockState("minecraft:note_block", Map.of("powered", "false", "note", String.valueOf(i))));
        }
    });
    private static final Int2ObjectMap<String> COLORS_BY_IDS = (Int2ObjectMap)DataFixUtils.make((Object)new Int2ObjectOpenHashMap(), map -> {
        map.put(0, (Object)"white");
        map.put(1, (Object)"orange");
        map.put(2, (Object)"magenta");
        map.put(3, (Object)"light_blue");
        map.put(4, (Object)"yellow");
        map.put(5, (Object)"lime");
        map.put(6, (Object)"pink");
        map.put(7, (Object)"gray");
        map.put(8, (Object)"light_gray");
        map.put(9, (Object)"cyan");
        map.put(10, (Object)"purple");
        map.put(11, (Object)"blue");
        map.put(12, (Object)"brown");
        map.put(13, (Object)"green");
        map.put(14, (Object)"red");
        map.put(15, (Object)"black");
    });
    static final Map<String, Dynamic<?>> BED_IDS_TO_STATES = (Map)DataFixUtils.make((Object)Maps.newHashMap(), map -> {
        for (Int2ObjectMap.Entry entry : COLORS_BY_IDS.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "red")) continue;
            ChunkPalettedStorageFix.Mapping.bed(map, entry.getIntKey(), (String)entry.getValue());
        }
    });
    static final Map<String, Dynamic<?>> BANNER_IDS_TO_STATES = (Map)DataFixUtils.make((Object)Maps.newHashMap(), map -> {
        for (Int2ObjectMap.Entry entry : COLORS_BY_IDS.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "white")) continue;
            ChunkPalettedStorageFix.Mapping.banner(map, 15 - entry.getIntKey(), (String)entry.getValue());
        }
    });
    static final Dynamic<?> AIR_STATE;

    private ChunkPalettedStorageFix.Mapping() {
    }

    private static void skull(Map<String, Dynamic<?>> map, int id, String entity, String type) {
        map.put(id + "north", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "north")));
        map.put(id + "east", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "east")));
        map.put(id + "south", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "south")));
        map.put(id + "west", FixUtil.createBlockState("minecraft:" + entity + "_wall_" + type, Map.of("facing", "west")));
        for (int i = 0; i < 16; ++i) {
            map.put("" + id + i, FixUtil.createBlockState("minecraft:" + entity + "_" + type, Map.of("rotation", String.valueOf(i))));
        }
    }

    private static void door(Map<String, Dynamic<?>> map, String id) {
        String string = "minecraft:" + id;
        map.put("minecraft:" + id + "eastlowerleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "eastlowerleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "eastlowerlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "eastlowerlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "eastlowerrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "eastlowerrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "eastlowerrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "eastlowerrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "eastupperleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "eastupperleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "eastupperlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "eastupperlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "eastupperrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "eastupperrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "eastupperrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "eastupperrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "east", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "northlowerleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "northlowerleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "northlowerlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "northlowerlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "northlowerrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "northlowerrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "northlowerrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "northlowerrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "northupperleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "northupperleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "northupperlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "northupperlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "northupperrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "northupperrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "northupperrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "northupperrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "north", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "southlowerleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "southlowerleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "southlowerlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "southlowerlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "southlowerrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "southlowerrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "southlowerrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "southlowerrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "southupperleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "southupperleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "southupperlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "southupperlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "southupperrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "southupperrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "southupperrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "southupperrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "south", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "westlowerleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "westlowerleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "westlowerlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "westlowerlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "westlowerrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "westlowerrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "westlowerrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "westlowerrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "lower", "hinge", "right", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "westupperleftfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "westupperleftfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "westupperlefttruefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "westupperlefttruetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "left", "open", "true", "powered", "true")));
        map.put("minecraft:" + id + "westupperrightfalsefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "false")));
        map.put("minecraft:" + id + "westupperrightfalsetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "false", "powered", "true")));
        map.put("minecraft:" + id + "westupperrighttruefalse", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "false")));
        map.put("minecraft:" + id + "westupperrighttruetrue", FixUtil.createBlockState(string, Map.of("facing", "west", "half", "upper", "hinge", "right", "open", "true", "powered", "true")));
    }

    private static void bed(Map<String, Dynamic<?>> map, int id, String color) {
        map.put("southfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "false", "part", "foot")));
        map.put("westfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "false", "part", "foot")));
        map.put("northfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "false", "part", "foot")));
        map.put("eastfalsefoot" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "false", "part", "foot")));
        map.put("southfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "false", "part", "head")));
        map.put("westfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "false", "part", "head")));
        map.put("northfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "false", "part", "head")));
        map.put("eastfalsehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "false", "part", "head")));
        map.put("southtruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "south", "occupied", "true", "part", "head")));
        map.put("westtruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "west", "occupied", "true", "part", "head")));
        map.put("northtruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "north", "occupied", "true", "part", "head")));
        map.put("easttruehead" + id, FixUtil.createBlockState("minecraft:" + color + "_bed", Map.of("facing", "east", "occupied", "true", "part", "head")));
    }

    private static void banner(Map<String, Dynamic<?>> map, int id, String color) {
        for (int i = 0; i < 16; ++i) {
            map.put(i + "_" + id, FixUtil.createBlockState("minecraft:" + color + "_banner", Map.of("rotation", String.valueOf(i))));
        }
        map.put("north_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "north")));
        map.put("south_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "south")));
        map.put("west_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "west")));
        map.put("east_" + id, FixUtil.createBlockState("minecraft:" + color + "_wall_banner", Map.of("facing", "east")));
    }

    static {
        field_52402.set(2);
        field_52402.set(3);
        field_52402.set(110);
        field_52402.set(140);
        field_52402.set(144);
        field_52402.set(25);
        field_52402.set(86);
        field_52402.set(26);
        field_52402.set(176);
        field_52402.set(177);
        field_52402.set(175);
        field_52402.set(64);
        field_52402.set(71);
        field_52402.set(193);
        field_52402.set(194);
        field_52402.set(195);
        field_52402.set(196);
        field_52402.set(197);
        field_52401.set(54);
        field_52401.set(146);
        field_52401.set(25);
        field_52401.set(26);
        field_52401.set(51);
        field_52401.set(53);
        field_52401.set(67);
        field_52401.set(108);
        field_52401.set(109);
        field_52401.set(114);
        field_52401.set(128);
        field_52401.set(134);
        field_52401.set(135);
        field_52401.set(136);
        field_52401.set(156);
        field_52401.set(163);
        field_52401.set(164);
        field_52401.set(180);
        field_52401.set(203);
        field_52401.set(55);
        field_52401.set(85);
        field_52401.set(113);
        field_52401.set(188);
        field_52401.set(189);
        field_52401.set(190);
        field_52401.set(191);
        field_52401.set(192);
        field_52401.set(93);
        field_52401.set(94);
        field_52401.set(101);
        field_52401.set(102);
        field_52401.set(160);
        field_52401.set(106);
        field_52401.set(107);
        field_52401.set(183);
        field_52401.set(184);
        field_52401.set(185);
        field_52401.set(186);
        field_52401.set(187);
        field_52401.set(132);
        field_52401.set(139);
        field_52401.set(199);
        AIR_STATE = FixUtil.createBlockState("minecraft:air");
    }
}
