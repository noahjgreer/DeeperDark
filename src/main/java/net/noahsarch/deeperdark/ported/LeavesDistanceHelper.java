package net.noahsarch.deeperdark.ported;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class LeavesDistanceHelper {

    private LeavesDistanceHelper() {
    }

    public static int updateDistance(BlockState blockState, BlockState neighborState, int distanceAt) {
        return mustResetDistance(blockState, neighborState, distanceAt) ? 7 : distanceAt;
    }

    private static boolean mustResetDistance(BlockState blockState, BlockState neighborState, int distanceAt) {
        if (distanceAt == 7) return false;
        if (!neighborState.is(BlockTags.LEAVES)) return false;
        if (neighborState.is(blockState.getBlock())) return false;
        if (blockState.is(createBlockTag(neighborState.getBlock()))) return false;
        if (neighborState.is(createBlockTag(blockState.getBlock()))) return false;
        return true;
    }

    public static TagKey<Block> createBlockTag(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.wrapAsHolder(block)
                .unwrapKey()
                .map(ResourceKey::identifier)
                .orElseThrow();
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("deeperdark", id.getNamespace() + "/" + id.getPath()));
    }
}
