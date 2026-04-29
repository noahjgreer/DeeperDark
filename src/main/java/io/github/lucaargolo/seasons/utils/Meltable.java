package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public interface Meltable {

    TagKey<Block> REPLACEABLE_BY_SNOW = TagKey.create(Registries.BLOCK, FabricSeasons.identifier("replaceable_by_snow"));

    default void onMeltableReplaced(ServerLevel world, BlockPos pos) {
        FabricSeasons.getPlacedMeltablesState(world).setManuallyPlaced(pos, false);
        FabricSeasons.getReplacedMeltablesState(world).setReplaced(pos, null);
    }

    default void onMeltableManuallyPlaced(ServerLevel world, BlockPos pos) {
        FabricSeasons.getPlacedMeltablesState(world).setManuallyPlaced(pos, true);
    }

    static void replaceBlockOnSnow(ServerLevel world, BlockPos blockPos, Biome biome) {
        BlockState plantState = world.getBlockState(blockPos);
        if (plantState.is(REPLACEABLE_BY_SNOW)) {
            if (biome.coldEnoughToSnow(blockPos, 0)
                    && blockPos.getY() >= world.getMinY()
                    && blockPos.getY() < world.getMaxY()
                    && world.getBrightness(LightLayer.BLOCK, blockPos) < 10) {
                BlockState upperState = world.getBlockState(blockPos.above());
                if (plantState.hasProperty(DoublePlantBlock.HALF) && upperState.hasProperty(DoublePlantBlock.HALF)) {
                    if (upperState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                        FabricSeasons.setMeltable(blockPos);
                        FabricSeasons.getReplacedMeltablesState(world).setReplaced(blockPos, plantState);
                        world.setBlock(blockPos, Blocks.SNOW.defaultBlockState(), Block.UPDATE_ALL);
                        world.setBlock(blockPos.above(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        world.sendBlockUpdated(blockPos, plantState, Blocks.SNOW.defaultBlockState(), Block.UPDATE_ALL);
                    }
                } else if (upperState.isAir()) {
                    FabricSeasons.setMeltable(blockPos);
                    FabricSeasons.getReplacedMeltablesState(world).setReplaced(blockPos, plantState);
                    world.setBlock(blockPos, Blocks.SNOW.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }
}
