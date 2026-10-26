package net.noahsarch.deeperdark.block;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
//? if <26.3 {
import net.fabricmc.fabric.api.registry.FuelValueEvents;
//?}
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if >=26.3 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
*///?}

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Per wood type: a plank wall, a bark wall, a stripped bark wall, and end-grain blocks (log top on every face).
 * Asset and data files come from {@code tools/generate_wood_variant_assets.py}.
 */
public class WoodVariantBlocks {
    /** Vanilla item -> the blocks that follow it in the Building Blocks tab. */
    private static final Map<Identifier, List<Block>> TAB_POSITIONS = new LinkedHashMap<>();

    static {
        registerWood("oak", "oak_wood", Blocks.OAK_LOG, Blocks.OAK_FENCE);
        registerWood("spruce", "spruce_wood", Blocks.SPRUCE_LOG, Blocks.SPRUCE_FENCE);
        registerWood("birch", "birch_wood", Blocks.BIRCH_LOG, Blocks.BIRCH_FENCE);
        registerWood("jungle", "jungle_wood", Blocks.JUNGLE_LOG, Blocks.JUNGLE_FENCE);
        registerWood("acacia", "acacia_wood", Blocks.ACACIA_LOG, Blocks.ACACIA_FENCE);
        registerWood("dark_oak", "dark_oak_wood", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_FENCE);
        registerWood("mangrove", "mangrove_wood", Blocks.MANGROVE_LOG, Blocks.MANGROVE_FENCE);
        registerWood("cherry", "cherry_wood", Blocks.CHERRY_LOG, Blocks.CHERRY_FENCE);
        registerWood("pale_oak", "pale_oak_wood", Blocks.PALE_OAK_LOG, Blocks.PALE_OAK_FENCE);
        //? if >=26.3 {
        /*registerWood("poplar", "poplar_wood", Blocks.POPLAR_LOG, Blocks.POPLAR_FENCE);
        *///?}
        registerWood("bamboo", "bamboo_block", Blocks.BAMBOO_BLOCK, Blocks.BAMBOO_FENCE);
        registerWood("crimson", "crimson_hyphae", Blocks.CRIMSON_STEM, Blocks.CRIMSON_FENCE);
        registerWood("warped", "warped_hyphae", Blocks.WARPED_STEM, Blocks.WARPED_FENCE);
    }

    private static void registerWood(String wood, String bark, Block log, Block fence) {
        // A pillar so it can copy the log's properties, whose map color reads the axis.
        TAB_POSITIONS.put(Identifier.withDefaultNamespace("stripped_" + bark), List.of(
                register(wood + "_end_grain", RotatedPillarBlock::new, log, 5),
                register("stripped_" + wood + "_end_grain", RotatedPillarBlock::new, log, 5)));
        TAB_POSITIONS.put(Identifier.withDefaultNamespace(wood + "_fence_gate"), List.of(
                register(wood + "_wall", WallBlock::new, fence, 20),
                register(bark + "_wall", WallBlock::new, fence, 20),
                register("stripped_" + bark + "_wall", WallBlock::new, fence, 20)));
    }

    /** Copies {@code template}'s properties; burns like vanilla fences (walls) or logs (end grain) unless nether wood. */
    private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, Block template, int burnOdds) {
        boolean burnable = template.defaultBlockState().ignitedByLava();
        ResourceKey<Block> blockKey = ModBlocks.keyOfBlock(name);
        Block block = factory.apply(BlockBehaviour.Properties.ofFullCopy(template).setId(blockKey));
        ResourceKey<Item> itemKey = ModBlocks.keyOfItem(name);
        Item.Properties itemProperties = new Item.Properties().setId(itemKey).useBlockDescriptionPrefix();
        //? if >=26.3 {
        /*if (burnable) itemProperties.cookingFuel(ContextIntProviders.COOKING_TIME_WOOD_BLOCKS);
        *///?}
        Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, itemProperties));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        if (burnable) FlammableBlockRegistry.getDefaultInstance().add(block, 5, burnOdds);
        return block;
    }

    public static void initialize() {
        // End grain is already fuel through the log item tags.
        //? if <26.3 {
        FuelValueEvents.BUILD.register((builder, context) -> TAB_POSITIONS.values().stream().flatMap(List::stream)
                .filter(block -> block instanceof WallBlock && block.defaultBlockState().ignitedByLava())
                .forEach(block -> builder.add(block, context.baseSmeltTime() * 3 / 2)));
        //?}
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS)
                .register(content -> TAB_POSITIONS.forEach((after, blocks) ->
                        content.insertAfter(BuiltInRegistries.ITEM.getValue(after), blocks.toArray(ItemLike[]::new))));
    }
}
