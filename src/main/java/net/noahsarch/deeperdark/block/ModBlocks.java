package net.noahsarch.deeperdark.block;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.sound.ModSoundType;
import net.noahsarch.deeperdark.sound.ModSounds;

import java.util.function.Function;

public class ModBlocks {
    private static Block registerStacksTo1(String name, Function<BlockBehaviour.Properties, Block> blockFactory,
            BlockBehaviour.Properties settings) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockKey));
        ResourceKey<Item> itemKey = keyOfItem(name);
        BlockItem blockItem = new BlockItem(block,
                new Item.Properties().setId(itemKey).useBlockDescriptionPrefix().stacksTo(1));
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory,
            BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.setId(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or
        // `minecraft:end_gateway`
        if (shouldRegisterItem) {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block,
                    new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    public static void initialize() {
        Deeperdark.LOGGER.info("Registering ModBlocks for deeperdark");

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            content.accept(GUNPOWDER_BLOCK);
            content.accept(LEATHER_BLOCK);
            content.accept(FLINT_BLOCK);
            content.accept(ROTTEN_FLESH_BLOCK);
            content.accept(ENDER_PEARL_BLOCK);
            content.accept(SANDSTONE_PILLAR);
            content.accept(STONE_BRICK_PILLAR);
            content.accept(DEEPSLATE_BRICK_PILLAR);
            content.accept(GLASS_DOOR);
            content.accept(WHITE_STAINED_GLASS_DOOR);
            content.accept(ORANGE_STAINED_GLASS_DOOR);
            content.accept(MAGENTA_STAINED_GLASS_DOOR);
            content.accept(LIGHT_BLUE_STAINED_GLASS_DOOR);
            content.accept(YELLOW_STAINED_GLASS_DOOR);
            content.accept(LIME_STAINED_GLASS_DOOR);
            content.accept(PINK_STAINED_GLASS_DOOR);
            content.accept(GRAY_STAINED_GLASS_DOOR);
            content.accept(LIGHT_GRAY_STAINED_GLASS_DOOR);
            content.accept(CYAN_STAINED_GLASS_DOOR);
            content.accept(PURPLE_STAINED_GLASS_DOOR);
            content.accept(BLUE_STAINED_GLASS_DOOR);
            content.accept(BROWN_STAINED_GLASS_DOOR);
            content.accept(GREEN_STAINED_GLASS_DOOR);
            content.accept(RED_STAINED_GLASS_DOOR);
            content.accept(BLACK_STAINED_GLASS_DOOR);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.accept(GOLDEN_CAULDRON);
            content.accept(SIPHON);
            content.accept(FLIMSY_BOX);
            content.accept(STURDY_BOX);
            content.accept(REINFORCED_BOX);
            content.accept(COLLAR_BENCH);
            content.accept(SMALL_ITEM_VAULT);
            content.accept(MEDIUM_ITEM_VAULT);
            content.accept(LARGE_ITEM_VAULT);
        });
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, name));
    }

    public static final Block GUNPOWDER_BLOCK = register(
            "gunpowder_block",
            Block::new,
            BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.TUFF_BRICKS),
            true);
    public static final Block LEATHER_BLOCK = register(
            "leather_block",
            SwordShearableBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.0F)
                    .sound(SoundType.WET_SPONGE),
            true);
    public static final Block FLINT_BLOCK = register(
            "flint_block",
            Block::new,
            BlockBehaviour.Properties.of()
                    .strength(0.6F, 4.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE),
            true);
    public static final Block ROTTEN_FLESH_BLOCK = register(
            "rotten_flesh_block",
            SwordShearableBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0.8F)
                    .sound(SoundType.WEEPING_VINES),
            true);
    // CAULDRON = register("cauldron", CauldronBlock::new,
    // Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
    // WATER_CAULDRON = register("water_cauldron", (p) -> new
    // LayeredCauldronBlock(Precipitation.RAIN, CauldronInteractions.WATER, p),
    // Properties.ofLegacyCopy(CAULDRON));
    // LAVA_CAULDRON = register("lava_cauldron", LavaCauldronBlock::new,
    // Properties.ofLegacyCopy(CAULDRON).lightLevel((statex) -> 15));
    // POWDER_SNOW_CAULDRON = register("powder_snow_cauldron", (p) -> new
    // LayeredCauldronBlock(Precipitation.SNOW, CauldronInteractions.POWDER_SNOW,
    // p), Properties.ofLegacyCopy(CAULDRON));

    public static final Block GOLDEN_CAULDRON = register(
            "golden_cauldron",
            GoldenCauldronBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL),
            true);
    public static final Block WATER_GOLDEN_CAULDRON = register(
            "water_golden_cauldron",
            (p) -> new GoldenLayeredCauldronBlock(
                    Precipitation.RAIN,
                    CauldronInteractions.WATER,
                    Items.WATER_BUCKET,
                    SoundEvents.BUCKET_FILL,
                    p),
            Properties.ofLegacyCopy(GOLDEN_CAULDRON),
            false);
    public static final Block LAVA_GOLDEN_CAULDRON = register(
            "lava_golden_cauldron",
            GoldenLavaCauldronBlock::new,
            Properties.ofLegacyCopy(GOLDEN_CAULDRON)
                    .lightLevel((statex) -> 15),
            false);
    public static final Block POWDER_SNOW_GOLDEN_CAULDRON = register(
            "powder_snow_golden_cauldron",
            (p) -> new GoldenLayeredCauldronBlock(
                    Precipitation.SNOW,
                    CauldronInteractions.POWDER_SNOW,
                    Items.POWDER_SNOW_BUCKET,
                    SoundEvents.BUCKET_FILL_POWDER_SNOW,
                    p),
            Properties.ofLegacyCopy(GOLDEN_CAULDRON),
            false);
    public static final Block SIPHON = register(
            "siphon",
            SiphonBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(5.0F, 1200.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 7),
            true);
    public static final Block REINFORCED_BOX = register(
            "reinforced_box",
            BoxBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.0F)
                    .sound(ModSoundType.BOX),
            true);
    public static final Block FLIMSY_BOX = register(
            "flimsy_box",
            BoxBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.0F)
                    .sound(ModSoundType.BOX),
            true);
    public static final Block STURDY_BOX = register(
            "sturdy_box",
            BoxBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.0F)
                    .sound(ModSoundType.BOX),
            true);
    public static final Block ENDER_PEARL_BLOCK = register(
            "ender_pearl_block",
            Block::new,
            BlockBehaviour.Properties.of()
                    .strength(0.5F)
                    .requiresCorrectToolForDrops()
                    .sound(ModSoundType.ENDER_PEARL_BLOCK)
                    .noOcclusion(),
            true);

    public static final Block SMALL_ITEM_VAULT = registerStacksTo1(
            "small_item_vault",
            VaultBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(150F, 1200F)
                    .sound(VaultBlock.VAULT_SOUND_TYPE)
                    .noOcclusion());
    public static final Block MEDIUM_ITEM_VAULT = registerStacksTo1(
            "medium_item_vault",
            VaultBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(150F, 1200F)
                    .sound(VaultBlock.VAULT_SOUND_TYPE)
                    .noOcclusion());
    public static final Block LARGE_ITEM_VAULT = registerStacksTo1(
            "large_item_vault",
            VaultBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(150F, 1200F)
                    .sound(VaultBlock.VAULT_SOUND_TYPE)
                    .noOcclusion());

    public static final Block COLLAR_BENCH = register(
            "collarbench",
            CollarBenchBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.125F)
                    .sound(SoundType.WOOD),
            true);

    public static final Block SANDSTONE_PILLAR = register(
            "sandstone_pillar",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0.8F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE),
            true);

    public static final Block STONE_BRICK_PILLAR = register(
            "stone_brick_pillar",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE),
            true);

    public static final Block DEEPSLATE_BRICK_PILLAR = register(
            "deepslate_brick_pillar",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.DEEPSLATE_BRICKS),
            true);

    // Glass doors — uses a custom BlockSetType backed by the glass door sounds.
    // canOpenByWindCharge=false: glass doors shatter rather than swing in wind.
    private static final BlockSetType GLASS_BLOCK_SET_TYPE = new BlockSetType(
            "deeperdark:glass",
            true, // canOpenByHand
            false, // canOpenByWindCharge
            false, // canButtonBeActivatedByArrows
            BlockSetType.PressurePlateSensitivity.EVERYTHING,
            SoundType.GLASS,
            ModSounds.GLASS_DOOR_CLOSE,
            ModSounds.GLASS_DOOR_OPEN,
            SoundEvents.WOODEN_TRAPDOOR_CLOSE,
            SoundEvents.WOODEN_TRAPDOOR_OPEN,
            SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.WOODEN_BUTTON_CLICK_OFF,
            SoundEvents.WOODEN_BUTTON_CLICK_ON);

    private static BlockBehaviour.Properties glassDoorProps() {
        return BlockBehaviour.Properties.of()
                .strength(0.3F)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }

    private static Block registerGlassDoor(String name, DyeColor color) {
        return register(
                name,
                p -> new DoorBlock(GLASS_BLOCK_SET_TYPE, p),
                glassDoorProps().mapColor(color),
                true);
    }

    public static final Block GLASS_DOOR = register(
            "glass_door",
            p -> new DoorBlock(GLASS_BLOCK_SET_TYPE, p),
            glassDoorProps(),
            true);

    public static final Block WHITE_STAINED_GLASS_DOOR = registerGlassDoor("white_stained_glass_door", DyeColor.WHITE);
    public static final Block ORANGE_STAINED_GLASS_DOOR = registerGlassDoor("orange_stained_glass_door",
            DyeColor.ORANGE);
    public static final Block MAGENTA_STAINED_GLASS_DOOR = registerGlassDoor("magenta_stained_glass_door",
            DyeColor.MAGENTA);
    public static final Block LIGHT_BLUE_STAINED_GLASS_DOOR = registerGlassDoor("light_blue_stained_glass_door",
            DyeColor.LIGHT_BLUE);
    public static final Block YELLOW_STAINED_GLASS_DOOR = registerGlassDoor("yellow_stained_glass_door",
            DyeColor.YELLOW);
    public static final Block LIME_STAINED_GLASS_DOOR = registerGlassDoor("lime_stained_glass_door", DyeColor.LIME);
    public static final Block PINK_STAINED_GLASS_DOOR = registerGlassDoor("pink_stained_glass_door", DyeColor.PINK);
    public static final Block GRAY_STAINED_GLASS_DOOR = registerGlassDoor("gray_stained_glass_door", DyeColor.GRAY);
    public static final Block LIGHT_GRAY_STAINED_GLASS_DOOR = registerGlassDoor("light_gray_stained_glass_door",
            DyeColor.LIGHT_GRAY);
    public static final Block CYAN_STAINED_GLASS_DOOR = registerGlassDoor("cyan_stained_glass_door", DyeColor.CYAN);
    public static final Block PURPLE_STAINED_GLASS_DOOR = registerGlassDoor("purple_stained_glass_door",
            DyeColor.PURPLE);
    public static final Block BLUE_STAINED_GLASS_DOOR = registerGlassDoor("blue_stained_glass_door", DyeColor.BLUE);
    public static final Block BROWN_STAINED_GLASS_DOOR = registerGlassDoor("brown_stained_glass_door", DyeColor.BROWN);
    public static final Block GREEN_STAINED_GLASS_DOOR = registerGlassDoor("green_stained_glass_door", DyeColor.GREEN);
    public static final Block RED_STAINED_GLASS_DOOR = registerGlassDoor("red_stained_glass_door", DyeColor.RED);
    public static final Block BLACK_STAINED_GLASS_DOOR = registerGlassDoor("black_stained_glass_door", DyeColor.BLACK);

}
