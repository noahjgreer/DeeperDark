/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.data.Model
 *  net.minecraft.client.data.Models
 *  net.minecraft.client.data.TextureKey
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.data;

import java.util.Optional;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Models {
    public static final Model CUBE = Models.block((String)"cube", (TextureKey[])new TextureKey[]{TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN});
    public static final Model CUBE_DIRECTIONAL = Models.block((String)"cube_directional", (TextureKey[])new TextureKey[]{TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN});
    public static final Model CUBE_ALL = Models.block((String)"cube_all", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model CUBE_ALL_INNER_FACES = Models.block((String)"cube_all_inner_faces", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model CUBE_MIRRORED_ALL = Models.block((String)"cube_mirrored_all", (String)"_mirrored", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model CUBE_NORTH_WEST_MIRRORED_ALL = Models.block((String)"cube_north_west_mirrored_all", (String)"_north_west_mirrored", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model CUBE_COLUMN_UV_LOCKED_X = Models.block((String)"cube_column_uv_locked_x", (String)"_x", (TextureKey[])new TextureKey[]{TextureKey.END, TextureKey.SIDE});
    public static final Model CUBE_COLUMN_UV_LOCKED_Y = Models.block((String)"cube_column_uv_locked_y", (String)"_y", (TextureKey[])new TextureKey[]{TextureKey.END, TextureKey.SIDE});
    public static final Model CUBE_COLUMN_UV_LOCKED_Z = Models.block((String)"cube_column_uv_locked_z", (String)"_z", (TextureKey[])new TextureKey[]{TextureKey.END, TextureKey.SIDE});
    public static final Model CUBE_COLUMN = Models.block((String)"cube_column", (TextureKey[])new TextureKey[]{TextureKey.END, TextureKey.SIDE});
    public static final Model CUBE_COLUMN_HORIZONTAL = Models.block((String)"cube_column_horizontal", (String)"_horizontal", (TextureKey[])new TextureKey[]{TextureKey.END, TextureKey.SIDE});
    public static final Model CUBE_COLUMN_MIRRORED = Models.block((String)"cube_column_mirrored", (String)"_mirrored", (TextureKey[])new TextureKey[]{TextureKey.END, TextureKey.SIDE});
    public static final Model CUBE_TOP = Models.block((String)"cube_top", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.SIDE});
    public static final Model CUBE_BOTTOM_TOP = Models.block((String)"cube_bottom_top", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE});
    public static final Model CUBE_BOTTOM_TOP_INNER_FACES = Models.block((String)"cube_bottom_top_inner_faces", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE});
    public static final Model ORIENTABLE = Models.block((String)"orientable", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.FRONT, TextureKey.SIDE});
    public static final Model ORIENTABLE_WITH_BOTTOM = Models.block((String)"orientable_with_bottom", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.FRONT});
    public static final Model ORIENTABLE_VERTICAL = Models.block((String)"orientable_vertical", (String)"_vertical", (TextureKey[])new TextureKey[]{TextureKey.FRONT, TextureKey.SIDE});
    public static final Model BUTTON = Models.block((String)"button", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model BUTTON_PRESSED = Models.block((String)"button_pressed", (String)"_pressed", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model BUTTON_INVENTORY = Models.block((String)"button_inventory", (String)"_inventory", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model DOOR_BOTTOM_LEFT = Models.block((String)"door_bottom_left", (String)"_bottom_left", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model DOOR_BOTTOM_LEFT_OPEN = Models.block((String)"door_bottom_left_open", (String)"_bottom_left_open", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model DOOR_BOTTOM_RIGHT = Models.block((String)"door_bottom_right", (String)"_bottom_right", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model DOOR_BOTTOM_RIGHT_OPEN = Models.block((String)"door_bottom_right_open", (String)"_bottom_right_open", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model DOOR_TOP_LEFT = Models.block((String)"door_top_left", (String)"_top_left", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model DOOR_TOP_LEFT_OPEN = Models.block((String)"door_top_left_open", (String)"_top_left_open", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model DOOR_TOP_RIGHT = Models.block((String)"door_top_right", (String)"_top_right", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model DOOR_TOP_RIGHT_OPEN = Models.block((String)"door_top_right_open", (String)"_top_right_open", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM});
    public static final Model CUSTOM_FENCE_POST = Models.block((String)"custom_fence_post", (String)"_post", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE, TextureKey.PARTICLE});
    public static final Model CUSTOM_FENCE_SIDE_NORTH = Models.block((String)"custom_fence_side_north", (String)"_side_north", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model CUSTOM_FENCE_SIDE_EAST = Models.block((String)"custom_fence_side_east", (String)"_side_east", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model CUSTOM_FENCE_SIDE_SOUTH = Models.block((String)"custom_fence_side_south", (String)"_side_south", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model CUSTOM_FENCE_SIDE_WEST = Models.block((String)"custom_fence_side_west", (String)"_side_west", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model CUSTOM_FENCE_INVENTORY = Models.block((String)"custom_fence_inventory", (String)"_inventory", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model FENCE_POST = Models.block((String)"fence_post", (String)"_post", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model FENCE_SIDE = Models.block((String)"fence_side", (String)"_side", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model FENCE_INVENTORY = Models.block((String)"fence_inventory", (String)"_inventory", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_WALL_POST = Models.block((String)"template_wall_post", (String)"_post", (TextureKey[])new TextureKey[]{TextureKey.WALL});
    public static final Model TEMPLATE_WALL_SIDE = Models.block((String)"template_wall_side", (String)"_side", (TextureKey[])new TextureKey[]{TextureKey.WALL});
    public static final Model TEMPLATE_WALL_SIDE_TALL = Models.block((String)"template_wall_side_tall", (String)"_side_tall", (TextureKey[])new TextureKey[]{TextureKey.WALL});
    public static final Model WALL_INVENTORY = Models.block((String)"wall_inventory", (String)"_inventory", (TextureKey[])new TextureKey[]{TextureKey.WALL});
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE = Models.block((String)"template_custom_fence_gate", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE, TextureKey.PARTICLE});
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE_OPEN = Models.block((String)"template_custom_fence_gate_open", (String)"_open", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE, TextureKey.PARTICLE});
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE_WALL = Models.block((String)"template_custom_fence_gate_wall", (String)"_wall", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE, TextureKey.PARTICLE});
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN = Models.block((String)"template_custom_fence_gate_wall_open", (String)"_wall_open", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE, TextureKey.PARTICLE});
    public static final Model TEMPLATE_FENCE_GATE = Models.block((String)"template_fence_gate", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_FENCE_GATE_OPEN = Models.block((String)"template_fence_gate_open", (String)"_open", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_FENCE_GATE_WALL = Models.block((String)"template_fence_gate_wall", (String)"_wall", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_FENCE_GATE_WALL_OPEN = Models.block((String)"template_fence_gate_wall_open", (String)"_wall_open", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model PRESSURE_PLATE_UP = Models.block((String)"pressure_plate_up", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model PRESSURE_PLATE_DOWN = Models.block((String)"pressure_plate_down", (String)"_down", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model PARTICLE = Models.make((TextureKey[])new TextureKey[]{TextureKey.PARTICLE});
    public static final Model SLAB = Models.block((String)"slab", (TextureKey[])new TextureKey[]{TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE});
    public static final Model SLAB_TOP = Models.block((String)"slab_top", (String)"_top", (TextureKey[])new TextureKey[]{TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE});
    public static final Model LEAVES = Models.block((String)"leaves", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model STAIRS = Models.block((String)"stairs", (TextureKey[])new TextureKey[]{TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE});
    public static final Model INNER_STAIRS = Models.block((String)"inner_stairs", (String)"_inner", (TextureKey[])new TextureKey[]{TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE});
    public static final Model OUTER_STAIRS = Models.block((String)"outer_stairs", (String)"_outer", (TextureKey[])new TextureKey[]{TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE});
    public static final Model TEMPLATE_TRAPDOOR_TOP = Models.block((String)"template_trapdoor_top", (String)"_top", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_TRAPDOOR_BOTTOM = Models.block((String)"template_trapdoor_bottom", (String)"_bottom", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_TRAPDOOR_OPEN = Models.block((String)"template_trapdoor_open", (String)"_open", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_TOP = Models.block((String)"template_orientable_trapdoor_top", (String)"_top", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM = Models.block((String)"template_orientable_trapdoor_bottom", (String)"_bottom", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN = Models.block((String)"template_orientable_trapdoor_open", (String)"_open", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model POINTED_DRIPSTONE = Models.block((String)"pointed_dripstone", (TextureKey[])new TextureKey[]{TextureKey.CROSS});
    public static final Model CROSS = Models.block((String)"cross", (TextureKey[])new TextureKey[]{TextureKey.CROSS});
    public static final Model TINTED_CROSS = Models.block((String)"tinted_cross", (TextureKey[])new TextureKey[]{TextureKey.CROSS});
    public static final Model CROSS_EMISSIVE = Models.block((String)"cross_emissive", (TextureKey[])new TextureKey[]{TextureKey.CROSS, TextureKey.CROSS_EMISSIVE});
    public static final Model FLOWER_POT_CROSS = Models.block((String)"flower_pot_cross", (TextureKey[])new TextureKey[]{TextureKey.PLANT});
    public static final Model TINTED_FLOWER_POT_CROSS = Models.block((String)"tinted_flower_pot_cross", (TextureKey[])new TextureKey[]{TextureKey.PLANT});
    public static final Model FLOWER_POT_CROSS_EMISSIVE = Models.block((String)"flower_pot_cross_emissive", (TextureKey[])new TextureKey[]{TextureKey.PLANT, TextureKey.CROSS_EMISSIVE});
    public static final Model RAIL_FLAT = Models.block((String)"rail_flat", (TextureKey[])new TextureKey[]{TextureKey.RAIL});
    public static final Model RAIL_CURVED = Models.block((String)"rail_curved", (String)"_corner", (TextureKey[])new TextureKey[]{TextureKey.RAIL});
    public static final Model TEMPLATE_RAIL_RAISED_NE = Models.block((String)"template_rail_raised_ne", (String)"_raised_ne", (TextureKey[])new TextureKey[]{TextureKey.RAIL});
    public static final Model TEMPLATE_RAIL_RAISED_SW = Models.block((String)"template_rail_raised_sw", (String)"_raised_sw", (TextureKey[])new TextureKey[]{TextureKey.RAIL});
    public static final Model CARPET = Models.block((String)"carpet", (TextureKey[])new TextureKey[]{TextureKey.WOOL});
    public static final Model MOSSY_CARPET_SIDE = Models.block((String)"mossy_carpet_side", (TextureKey[])new TextureKey[]{TextureKey.SIDE});
    public static final Model FLOWERBED_1 = Models.block((String)"flowerbed_1", (String)"_1", (TextureKey[])new TextureKey[]{TextureKey.FLOWERBED, TextureKey.STEM});
    public static final Model FLOWERBED_2 = Models.block((String)"flowerbed_2", (String)"_2", (TextureKey[])new TextureKey[]{TextureKey.FLOWERBED, TextureKey.STEM});
    public static final Model FLOWERBED_3 = Models.block((String)"flowerbed_3", (String)"_3", (TextureKey[])new TextureKey[]{TextureKey.FLOWERBED, TextureKey.STEM});
    public static final Model FLOWERBED_4 = Models.block((String)"flowerbed_4", (String)"_4", (TextureKey[])new TextureKey[]{TextureKey.FLOWERBED, TextureKey.STEM});
    public static final Model TEMPLATE_LEAF_LITTER_1 = Models.block((String)"template_leaf_litter_1", (String)"_1", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_LEAF_LITTER_2 = Models.block((String)"template_leaf_litter_2", (String)"_2", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_LEAF_LITTER_3 = Models.block((String)"template_leaf_litter_3", (String)"_3", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_LEAF_LITTER_4 = Models.block((String)"template_leaf_litter_4", (String)"_4", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model CORAL_FAN = Models.block((String)"coral_fan", (TextureKey[])new TextureKey[]{TextureKey.FAN});
    public static final Model CORAL_WALL_FAN = Models.block((String)"coral_wall_fan", (TextureKey[])new TextureKey[]{TextureKey.FAN});
    public static final Model TEMPLATE_GLAZED_TERRACOTTA = Models.block((String)"template_glazed_terracotta", (TextureKey[])new TextureKey[]{TextureKey.PATTERN});
    public static final Model TEMPLATE_CHORUS_FLOWER = Models.block((String)"template_chorus_flower", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_DAYLIGHT_DETECTOR = Models.block((String)"template_daylight_detector", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.SIDE});
    public static final Model TEMPLATE_GLASS_PANE_NOSIDE = Models.block((String)"template_glass_pane_noside", (String)"_noside", (TextureKey[])new TextureKey[]{TextureKey.PANE});
    public static final Model TEMPLATE_GLASS_PANE_NOSIDE_ALT = Models.block((String)"template_glass_pane_noside_alt", (String)"_noside_alt", (TextureKey[])new TextureKey[]{TextureKey.PANE});
    public static final Model TEMPLATE_GLASS_PANE_POST = Models.block((String)"template_glass_pane_post", (String)"_post", (TextureKey[])new TextureKey[]{TextureKey.PANE, TextureKey.EDGE});
    public static final Model TEMPLATE_GLASS_PANE_SIDE = Models.block((String)"template_glass_pane_side", (String)"_side", (TextureKey[])new TextureKey[]{TextureKey.PANE, TextureKey.EDGE});
    public static final Model TEMPLATE_GLASS_PANE_SIDE_ALT = Models.block((String)"template_glass_pane_side_alt", (String)"_side_alt", (TextureKey[])new TextureKey[]{TextureKey.PANE, TextureKey.EDGE});
    public static final Model TEMPLATE_COMMAND_BLOCK = Models.block((String)"template_command_block", (TextureKey[])new TextureKey[]{TextureKey.FRONT, TextureKey.BACK, TextureKey.SIDE});
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_LEFT = Models.block((String)"template_chiseled_bookshelf_slot_top_left", (String)"_slot_top_left", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_MID = Models.block((String)"template_chiseled_bookshelf_slot_top_mid", (String)"_slot_top_mid", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_RIGHT = Models.block((String)"template_chiseled_bookshelf_slot_top_right", (String)"_slot_top_right", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT = Models.block((String)"template_chiseled_bookshelf_slot_bottom_left", (String)"_slot_bottom_left", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_MID = Models.block((String)"template_chiseled_bookshelf_slot_bottom_mid", (String)"_slot_bottom_mid", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT = Models.block((String)"template_chiseled_bookshelf_slot_bottom_right", (String)"_slot_bottom_right", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_SHELF_BODY = Models.block((String)"template_shelf_body", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_SHELF_INVENTORY = Models.block((String)"template_shelf_inventory", (String)"_inventory", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_SHELF_UNPOWERED = Models.block((String)"template_shelf_unpowered", (String)"_unpowered", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_SHELF_UNCONNECTED = Models.block((String)"template_shelf_unconnected", (String)"_unconnected", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_SHELF_LEFT = Models.block((String)"template_shelf_left", (String)"_left", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_SHELF_CENTER = Models.block((String)"template_shelf_center", (String)"_center", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_SHELF_RIGHT = Models.block((String)"template_shelf_right", (String)"_right", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_ANVIL = Models.block((String)"template_anvil", (TextureKey[])new TextureKey[]{TextureKey.TOP});
    public static final Model[] STEM_GROWTH_STAGES = (Model[])IntStream.range(0, 8).mapToObj(stage -> Models.block((String)("stem_growth" + stage), (String)("_stage" + stage), (TextureKey[])new TextureKey[]{TextureKey.STEM})).toArray(Model[]::new);
    public static final Model STEM_FRUIT = Models.block((String)"stem_fruit", (TextureKey[])new TextureKey[]{TextureKey.STEM, TextureKey.UPPERSTEM});
    public static final Model CROP = Models.block((String)"crop", (TextureKey[])new TextureKey[]{TextureKey.CROP});
    public static final Model TEMPLATE_FARMLAND = Models.block((String)"template_farmland", (TextureKey[])new TextureKey[]{TextureKey.DIRT, TextureKey.TOP});
    public static final Model TEMPLATE_FIRE_FLOOR = Models.block((String)"template_fire_floor", (TextureKey[])new TextureKey[]{TextureKey.FIRE});
    public static final Model TEMPLATE_FIRE_SIDE = Models.block((String)"template_fire_side", (TextureKey[])new TextureKey[]{TextureKey.FIRE});
    public static final Model TEMPLATE_FIRE_SIDE_ALT = Models.block((String)"template_fire_side_alt", (TextureKey[])new TextureKey[]{TextureKey.FIRE});
    public static final Model TEMPLATE_FIRE_UP = Models.block((String)"template_fire_up", (TextureKey[])new TextureKey[]{TextureKey.FIRE});
    public static final Model TEMPLATE_FIRE_UP_ALT = Models.block((String)"template_fire_up_alt", (TextureKey[])new TextureKey[]{TextureKey.FIRE});
    public static final Model TEMPLATE_CAMPFIRE = Models.block((String)"template_campfire", (TextureKey[])new TextureKey[]{TextureKey.FIRE, TextureKey.LIT_LOG});
    public static final Model TEMPLATE_LANTERN = Models.block((String)"template_lantern", (TextureKey[])new TextureKey[]{TextureKey.LANTERN});
    public static final Model TEMPLATE_HANGING_LANTERN = Models.block((String)"template_hanging_lantern", (String)"_hanging", (TextureKey[])new TextureKey[]{TextureKey.LANTERN});
    public static final Model TEMPLATE_CHAIN = Models.block((String)"template_chain", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_BARS_CAP = Models.block((String)"template_bars_cap", (String)"_cap", (TextureKey[])new TextureKey[]{TextureKey.BARS, TextureKey.EDGE});
    public static final Model TEMPLATE_BARS_CAP_ALT = Models.block((String)"template_bars_cap_alt", (String)"_cap_alt", (TextureKey[])new TextureKey[]{TextureKey.BARS, TextureKey.EDGE});
    public static final Model TEMPLATE_BARS_POST = Models.block((String)"template_bars_post", (String)"_post", (TextureKey[])new TextureKey[]{TextureKey.BARS, TextureKey.EDGE});
    public static final Model TEMPLATE_BARS_POST_ENDS = Models.block((String)"template_bars_post_ends", (String)"_post_ends", (TextureKey[])new TextureKey[]{TextureKey.BARS, TextureKey.EDGE});
    public static final Model TEMPLATE_BARS_SIDE = Models.block((String)"template_bars_side", (String)"_side", (TextureKey[])new TextureKey[]{TextureKey.BARS, TextureKey.EDGE});
    public static final Model TEMPLATE_BARS_SIDE_ALT = Models.block((String)"template_bars_side_alt", (String)"_side_alt", (TextureKey[])new TextureKey[]{TextureKey.BARS, TextureKey.EDGE});
    public static final Model TEMPLATE_TORCH = Models.block((String)"template_torch", (TextureKey[])new TextureKey[]{TextureKey.TORCH});
    public static final Model TEMPLATE_TORCH_UNLIT = Models.block((String)"template_torch_unlit", (TextureKey[])new TextureKey[]{TextureKey.TORCH});
    public static final Model TEMPLATE_TORCH_WALL = Models.block((String)"template_torch_wall", (TextureKey[])new TextureKey[]{TextureKey.TORCH});
    public static final Model TEMPLATE_TORCH_WALL_UNLIT = Models.block((String)"template_torch_wall_unlit", (TextureKey[])new TextureKey[]{TextureKey.TORCH});
    public static final Model TEMPLATE_REDSTONE_TORCH = Models.block((String)"template_redstone_torch", (TextureKey[])new TextureKey[]{TextureKey.TORCH});
    public static final Model TEMPLATE_REDSTONE_TORCH_WALL = Models.block((String)"template_redstone_torch_wall", (TextureKey[])new TextureKey[]{TextureKey.TORCH});
    public static final Model TEMPLATE_PISTON = Models.block((String)"template_piston", (TextureKey[])new TextureKey[]{TextureKey.PLATFORM, TextureKey.BOTTOM, TextureKey.SIDE});
    public static final Model TEMPLATE_PISTON_HEAD = Models.block((String)"template_piston_head", (TextureKey[])new TextureKey[]{TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY});
    public static final Model TEMPLATE_PISTON_HEAD_SHORT = Models.block((String)"template_piston_head_short", (TextureKey[])new TextureKey[]{TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY});
    public static final Model TEMPLATE_SEAGRASS = Models.block((String)"template_seagrass", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_TURTLE_EGG = Models.block((String)"template_turtle_egg", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model DRIED_GHAST = Models.block((String)"dried_ghast", (TextureKey[])new TextureKey[]{TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.TENTACLES});
    public static final Model TEMPLATE_TWO_TURTLE_EGGS = Models.block((String)"template_two_turtle_eggs", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model TEMPLATE_THREE_TURTLE_EGGS = Models.block((String)"template_three_turtle_eggs", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model TEMPLATE_FOUR_TURTLE_EGGS = Models.block((String)"template_four_turtle_eggs", (TextureKey[])new TextureKey[]{TextureKey.ALL});
    public static final Model TEMPLATE_SINGLE_FACE = Models.block((String)"template_single_face", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});
    public static final Model TEMPLATE_CAULDRON_LEVEL1 = Models.block((String)"template_cauldron_level1", (TextureKey[])new TextureKey[]{TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE});
    public static final Model TEMPLATE_CAULDRON_LEVEL2 = Models.block((String)"template_cauldron_level2", (TextureKey[])new TextureKey[]{TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE});
    public static final Model TEMPLATE_CAULDRON_FULL = Models.block((String)"template_cauldron_full", (TextureKey[])new TextureKey[]{TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE});
    public static final Model TEMPLATE_AZALEA = Models.block((String)"template_azalea", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.SIDE});
    public static final Model TEMPLATE_POTTED_AZALEA_BUSH = Models.block((String)"template_potted_azalea_bush", (TextureKey[])new TextureKey[]{TextureKey.PLANT, TextureKey.TOP, TextureKey.SIDE});
    public static final Model TEMPLATE_POTTED_FLOWERING_AZALEA_BUSH = Models.block((String)"template_potted_azalea_bush", (TextureKey[])new TextureKey[]{TextureKey.PLANT, TextureKey.TOP, TextureKey.SIDE});
    public static final Model SNIFFER_EGG = Models.block((String)"sniffer_egg", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST});
    public static final Model GENERATED = Models.item((String)"generated", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model TEMPLATE_MUSIC_DISC = Models.item((String)"template_music_disc", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model HANDHELD = Models.item((String)"handheld", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model HANDHELD_ROD = Models.item((String)"handheld_rod", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model GENERATED_TWO_LAYERS = Models.item((String)"generated", (TextureKey[])new TextureKey[]{TextureKey.LAYER0, TextureKey.LAYER1});
    public static final Model GENERATED_THREE_LAYERS = Models.item((String)"generated", (TextureKey[])new TextureKey[]{TextureKey.LAYER0, TextureKey.LAYER1, TextureKey.LAYER2});
    public static final Model TEMPLATE_SHULKER_BOX = Models.item((String)"template_shulker_box", (TextureKey[])new TextureKey[]{TextureKey.PARTICLE});
    public static final Model TEMPLATE_BED = Models.item((String)"template_bed", (TextureKey[])new TextureKey[]{TextureKey.PARTICLE});
    public static final Model TEMPLATE_CHEST = Models.item((String)"template_chest", (TextureKey[])new TextureKey[]{TextureKey.PARTICLE});
    public static final Model TEMPLATE_BUNDLE_OPEN_FRONT = Models.openBundle((String)"template_bundle_open_front", (String)"_open_front", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model TEMPLATE_BUNDLE_OPEN_BACK = Models.openBundle((String)"template_bundle_open_back", (String)"_open_back", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model BOW = Models.item((String)"bow", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model CROSSBOW = Models.item((String)"crossbow", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model SPEAR_IN_HAND = Models.openBundle((String)"spear_in_hand", (String)"_in_hand", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model TEMPLATE_CANDLE = Models.block((String)"template_candle", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_TWO_CANDLES = Models.block((String)"template_two_candles", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_THREE_CANDLES = Models.block((String)"template_three_candles", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_FOUR_CANDLES = Models.block((String)"template_four_candles", (TextureKey[])new TextureKey[]{TextureKey.ALL, TextureKey.PARTICLE});
    public static final Model TEMPLATE_CAKE_WITH_CANDLE = Models.block((String)"template_cake_with_candle", (TextureKey[])new TextureKey[]{TextureKey.CANDLE, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE});
    public static final Model TEMPLATE_SCULK_SHRIEKER = Models.block((String)"template_sculk_shrieker", (TextureKey[])new TextureKey[]{TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE, TextureKey.INNER_TOP});
    public static final Model TEMPLATE_VAULT = Models.block((String)"template_vault", (TextureKey[])new TextureKey[]{TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.FRONT});
    public static final Model HANDHELD_MACE = Models.item((String)"handheld_mace", (TextureKey[])new TextureKey[]{TextureKey.LAYER0});
    public static final Model TEMPLATE_LIGHTNING_ROD = Models.block((String)"template_lightning_rod", (TextureKey[])new TextureKey[]{TextureKey.TEXTURE});

    private static Model make(TextureKey ... requiredTextureKeys) {
        return new Model(Optional.empty(), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla((String)("block/" + parent))), Optional.empty(), requiredTextureKeys);
    }

    private static Model item(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla((String)("item/" + parent))), Optional.empty(), requiredTextureKeys);
    }

    private static Model openBundle(String parent, String variant, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla((String)("item/" + parent))), Optional.of(variant), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(Identifier.ofVanilla((String)("block/" + parent))), Optional.of(variant), requiredTextureKeys);
    }
}

