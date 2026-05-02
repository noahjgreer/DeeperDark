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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.noahsarch.deeperdark.Deeperdark;

import java.util.function.Function;

public class ModBlocks {
    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		// Create a registry key for the block
		ResourceKey<Block> blockKey = keyOfBlock(name);
		// Create the block instance
		Block block = blockFactory.apply(settings.setId(blockKey));

		// Sometimes, you may not want to register an item for the block.
		// Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
		if (shouldRegisterItem) {
			// Items need to be registered with a different type of registry key, but the ID
			// can be the same.
			ResourceKey<Item> itemKey = keyOfItem(name);

			BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
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
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(content -> {
            content.accept(GOLDEN_CAULDRON);
            content.accept(SIPHON);
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
        true
    );
    public static final Block LEATHER_BLOCK = register(
        "leather_block",
        SwordShearableBlock::new,
        BlockBehaviour.Properties.of()
            .strength(1.0F)
            .sound(SoundType.WET_SPONGE),
        true
    );
    public static final Block FLINT_BLOCK = register(
        "flint_block",
        Block::new,
        BlockBehaviour.Properties.of()
            .strength(0.6F, 4.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE),
        true
    );
    public static final Block ROTTEN_FLESH_BLOCK = register(
        "rotten_flesh_block",
        SwordShearableBlock::new,
        BlockBehaviour.Properties.of()
            .strength(0.8F)
            .sound(SoundType.WEEPING_VINES),
        true
    );
    //  CAULDRON = register("cauldron", CauldronBlock::new, Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F).noOcclusion());
    //  WATER_CAULDRON = register("water_cauldron", (p) -> new LayeredCauldronBlock(Precipitation.RAIN, CauldronInteractions.WATER, p), Properties.ofLegacyCopy(CAULDRON));
    //  LAVA_CAULDRON = register("lava_cauldron", LavaCauldronBlock::new, Properties.ofLegacyCopy(CAULDRON).lightLevel((statex) -> 15));
    //  POWDER_SNOW_CAULDRON = register("powder_snow_cauldron", (p) -> new LayeredCauldronBlock(Precipitation.SNOW, CauldronInteractions.POWDER_SNOW, p), Properties.ofLegacyCopy(CAULDRON));

    public static final Block GOLDEN_CAULDRON = register(
        "golden_cauldron",
        GoldenCauldronBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL),
        true
    );
    public static final Block WATER_GOLDEN_CAULDRON = register(
        "water_golden_cauldron",
        (p) -> new GoldenLayeredCauldronBlock(
            Precipitation.RAIN,
            CauldronInteractions.WATER,
            Items.WATER_BUCKET,
            SoundEvents.BUCKET_FILL,
            p),
        Properties.ofLegacyCopy(GOLDEN_CAULDRON),
        false
    );
    public static final Block LAVA_GOLDEN_CAULDRON = register(
        "lava_golden_cauldron",
        GoldenLavaCauldronBlock::new,
        Properties.ofLegacyCopy(GOLDEN_CAULDRON)
            .lightLevel((statex) -> 15),
        false
    );
    public static final Block POWDER_SNOW_GOLDEN_CAULDRON = register(
        "powder_snow_golden_cauldron",
        (p) -> new GoldenLayeredCauldronBlock(
            Precipitation.SNOW,
            CauldronInteractions.POWDER_SNOW,
            Items.POWDER_SNOW_BUCKET,
            SoundEvents.BUCKET_FILL_POWDER_SNOW,
            p),
        Properties.ofLegacyCopy(GOLDEN_CAULDRON),
        false
    );
    public static final Block SIPHON = register(
        "siphon",
        SiphonBlock::new,
        BlockBehaviour.Properties.of()
            .strength(5.0F, 1200.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
            .lightLevel(state -> 7),
        true
    );


}
