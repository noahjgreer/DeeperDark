package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.noahsarch.deeperdark.DeeperDarkConfig;

/**
 * Handles natural mossing of cobblestone and stone bricks over time.
 */
public class MossGrowthHandler {

    private static long tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_LEVEL_TICK.register(MossGrowthHandler::onWorldTick);
    }

    private static void onWorldTick(ServerLevel world) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        if (!config.mossGrowthEnabled) {
            return;
        }

        tickCounter++;

        int checkInterval = Math.max(1, config.mossTickCheckFrequency * 20);
        if (tickCounter % checkInterval != 0) {
            return;
        }

        RandomSource random = world.getRandom();

        for (var player : world.players()) {
            int checkRadius = 128;
            int checksPerPlayer = 3;

            for (int i = 0; i < checksPerPlayer; i++) {
                int x = player.getBlockX() + random.nextInt(checkRadius * 2) - checkRadius;
                int y = player.getBlockY() + random.nextInt(checkRadius) - checkRadius / 2;
                int z = player.getBlockZ() + random.nextInt(checkRadius * 2) - checkRadius;

                // Clamp Y to valid range
                y = Math.max(world.getMinY(), Math.min(world.getMaxY(), y));

                BlockPos pos = new BlockPos(x, y, z);

                if (!world.isLoaded(pos)) {
                    continue;
                }

                BlockState state = world.getBlockState(pos);
                tryMossBlock(world, pos, state, random, config);
            }
        }
    }

    private static void tryMossBlock(ServerLevel world, BlockPos pos, BlockState state, RandomSource random, DeeperDarkConfig.ConfigInstance config) {
        Block block = state.getBlock();
        Block mossyVariant = null;
        double speedMultiplier = 1.0;

        if (block == Blocks.COBBLESTONE) {
            mossyVariant = Blocks.MOSSY_COBBLESTONE;
            speedMultiplier = 1.0;
        } else if (block == Blocks.STONE_BRICKS) {
            mossyVariant = Blocks.MOSSY_STONE_BRICKS;
            speedMultiplier = config.stoneBrickMossMultiplier;
        } else if (block == Blocks.COBBLESTONE_WALL) {
            mossyVariant = Blocks.MOSSY_COBBLESTONE_WALL;
            speedMultiplier = 1.0;
        } else if (block == Blocks.STONE_BRICK_WALL) {
            mossyVariant = Blocks.MOSSY_STONE_BRICK_WALL;
            speedMultiplier = config.stoneBrickMossMultiplier;
        } else if (block == Blocks.COBBLESTONE_SLAB) {
            mossyVariant = Blocks.MOSSY_COBBLESTONE_SLAB;
            speedMultiplier = 1.0;
        } else if (block == Blocks.STONE_BRICK_SLAB) {
            mossyVariant = Blocks.MOSSY_STONE_BRICK_SLAB;
            speedMultiplier = config.stoneBrickMossMultiplier;
        } else if (block == Blocks.COBBLESTONE_STAIRS) {
            mossyVariant = Blocks.MOSSY_COBBLESTONE_STAIRS;
            speedMultiplier = 1.0;
        } else if (block == Blocks.STONE_BRICK_STAIRS) {
            mossyVariant = Blocks.MOSSY_STONE_BRICK_STAIRS;
            speedMultiplier = config.stoneBrickMossMultiplier;
        } else {
            return;
        }

        double chance = config.mossBaseChance * speedMultiplier;

        FluidState fluidState = world.getFluidState(pos);
        if (fluidState.getType().is(FluidTags.WATER)) {
            chance *= config.mossUnderwaterMultiplier;
        }

        int nearbyMossy = countNearbyMossyBlocks(world, pos);
        chance += nearbyMossy * config.mossNearbyBonus;

        if (random.nextDouble() < chance) {
            BlockState mossyState = copyBlockStateProperties(state, mossyVariant.defaultBlockState());
            world.setBlock(pos, mossyState, Block.UPDATE_ALL);
        }
    }

    private static int countNearbyMossyBlocks(ServerLevel world, BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            BlockState adjacent = world.getBlockState(pos.relative(direction));
            if (isMossyBlock(adjacent)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isMossyBlock(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.MOSSY_COBBLESTONE ||
               block == Blocks.MOSSY_STONE_BRICKS ||
               block == Blocks.MOSSY_COBBLESTONE_WALL ||
               block == Blocks.MOSSY_STONE_BRICK_WALL ||
               block == Blocks.MOSSY_COBBLESTONE_SLAB ||
               block == Blocks.MOSSY_STONE_BRICK_SLAB ||
               block == Blocks.MOSSY_COBBLESTONE_STAIRS ||
               block == Blocks.MOSSY_STONE_BRICK_STAIRS ||
               block == Blocks.MOSS_BLOCK ||
               block == Blocks.MOSS_CARPET;
    }

    @SuppressWarnings("unchecked")
    private static BlockState copyBlockStateProperties(BlockState from, BlockState to) {
        for (var property : from.getProperties()) {
            if (to.hasProperty(property)) {
                to = copyProperty(from, to, (net.minecraft.world.level.block.state.properties.Property) property);
            }
        }
        return to;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, net.minecraft.world.level.block.state.properties.Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}
