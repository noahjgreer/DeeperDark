/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.block.dispenser.BlockPlacementDispenserBehavior
 *  net.minecraft.block.dispenser.BoatDispenserBehavior
 *  net.minecraft.block.dispenser.DispenserBehavior
 *  net.minecraft.block.dispenser.DispenserBehavior$1
 *  net.minecraft.block.dispenser.DispenserBehavior$10
 *  net.minecraft.block.dispenser.MinecartDispenserBehavior
 *  net.minecraft.block.dispenser.ShearsDispenserBehavior
 *  net.minecraft.entity.EntityType
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.SpawnEggItem
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.math.BlockPointer
 *  org.slf4j.Logger
 */
package net.minecraft.block.dispenser;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.MinecartDispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPointer;
import org.slf4j.Logger;

public interface DispenserBehavior {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DispenserBehavior NOOP = (pointer, stack) -> stack;

    public ItemStack dispense(BlockPointer var1, ItemStack var2);

    public static void registerDefaults() {
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.ARROW);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.TIPPED_ARROW);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.SPECTRAL_ARROW);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.EGG);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.BLUE_EGG);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.BROWN_EGG);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.SNOWBALL);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.EXPERIENCE_BOTTLE);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.SPLASH_POTION);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.LINGERING_POTION);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.FIREWORK_ROCKET);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.FIRE_CHARGE);
        DispenserBlock.registerProjectileBehavior((ItemConvertible)Items.WIND_CHARGE);
        1 itemDispenserBehavior = new /* Unavailable Anonymous Inner Class!! */;
        for (SpawnEggItem spawnEggItem : SpawnEggItem.getAll()) {
            DispenserBlock.registerBehavior((ItemConvertible)spawnEggItem, (DispenserBehavior)itemDispenserBehavior);
        }
        DispenserBlock.registerBehavior((ItemConvertible)Items.ARMOR_STAND, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.CHEST, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.OAK_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.OAK_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.SPRUCE_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.SPRUCE_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.BIRCH_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.BIRCH_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.JUNGLE_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.JUNGLE_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.DARK_OAK_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.DARK_OAK_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.ACACIA_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.ACACIA_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.CHERRY_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.CHERRY_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.MANGROVE_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.MANGROVE_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.PALE_OAK_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.PALE_OAK_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.BAMBOO_RAFT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.BAMBOO_RAFT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.OAK_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.OAK_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.SPRUCE_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.SPRUCE_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.BIRCH_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.BIRCH_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.JUNGLE_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.JUNGLE_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.DARK_OAK_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.DARK_OAK_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.ACACIA_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.ACACIA_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.CHERRY_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.CHERRY_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.MANGROVE_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.MANGROVE_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.PALE_OAK_CHEST_BOAT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.PALE_OAK_CHEST_BOAT));
        DispenserBlock.registerBehavior((ItemConvertible)Items.BAMBOO_CHEST_RAFT, (DispenserBehavior)new BoatDispenserBehavior(EntityType.BAMBOO_CHEST_RAFT));
        10 dispenserBehavior = new /* Unavailable Anonymous Inner Class!! */;
        DispenserBlock.registerBehavior((ItemConvertible)Items.LAVA_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.WATER_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.POWDER_SNOW_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.SALMON_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.COD_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.PUFFERFISH_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.TROPICAL_FISH_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.AXOLOTL_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.TADPOLE_BUCKET, (DispenserBehavior)dispenserBehavior);
        DispenserBlock.registerBehavior((ItemConvertible)Items.BUCKET, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.FLINT_AND_STEEL, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.BONE_MEAL, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Blocks.TNT, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.WITHER_SKELETON_SKULL, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Blocks.CARVED_PUMPKIN, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Blocks.SHULKER_BOX.asItem(), (DispenserBehavior)new BlockPlacementDispenserBehavior());
        for (DyeColor dyeColor : DyeColor.values()) {
            DispenserBlock.registerBehavior((ItemConvertible)ShulkerBoxBlock.get((DyeColor)dyeColor).asItem(), (DispenserBehavior)new BlockPlacementDispenserBehavior());
        }
        DispenserBlock.registerBehavior((ItemConvertible)Items.GLASS_BOTTLE.asItem(), (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.GLOWSTONE, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.SHEARS.asItem(), (DispenserBehavior)new ShearsDispenserBehavior());
        DispenserBlock.registerBehavior((ItemConvertible)Items.BRUSH.asItem(), (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.HONEYCOMB, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.POTION, (DispenserBehavior)new /* Unavailable Anonymous Inner Class!! */);
        DispenserBlock.registerBehavior((ItemConvertible)Items.MINECART, (DispenserBehavior)new MinecartDispenserBehavior(EntityType.MINECART));
        DispenserBlock.registerBehavior((ItemConvertible)Items.CHEST_MINECART, (DispenserBehavior)new MinecartDispenserBehavior(EntityType.CHEST_MINECART));
        DispenserBlock.registerBehavior((ItemConvertible)Items.FURNACE_MINECART, (DispenserBehavior)new MinecartDispenserBehavior(EntityType.FURNACE_MINECART));
        DispenserBlock.registerBehavior((ItemConvertible)Items.TNT_MINECART, (DispenserBehavior)new MinecartDispenserBehavior(EntityType.TNT_MINECART));
        DispenserBlock.registerBehavior((ItemConvertible)Items.HOPPER_MINECART, (DispenserBehavior)new MinecartDispenserBehavior(EntityType.HOPPER_MINECART));
        DispenserBlock.registerBehavior((ItemConvertible)Items.COMMAND_BLOCK_MINECART, (DispenserBehavior)new MinecartDispenserBehavior(EntityType.COMMAND_BLOCK_MINECART));
    }
}

