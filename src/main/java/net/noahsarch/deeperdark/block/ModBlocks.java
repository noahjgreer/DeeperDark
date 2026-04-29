package net.noahsarch.deeperdark.block;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
        Block::new,
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
        Block::new,
        BlockBehaviour.Properties.of()
            .strength(0.8F)
            .sound(SoundType.WEEPING_VINES),
        true
    );
    public static final Block GOLDEN_CAULDRON = register(
        "golden_cauldron",
        GoldenCauldronBlock::new,
        BlockBehaviour.Properties.of()
            .strength(2.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL),
        true
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
