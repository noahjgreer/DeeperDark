package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.noahsarch.deeperdark.DeeperDarkConfig;

/**
 * Handles natural mossing of cobblestone and stone bricks over time.
 * Since these blocks don't have randomTicks enabled, we use the server tick event
 * and manually select random blocks to check for mossing.
 */
public class MossGrowthHandler {

    private static long tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(MossGrowthHandler::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        // Check if moss growth is enabled
        if (!config.mossGrowthEnabled) {
            return;
        }

        tickCounter++;

        // Only run every N ticks to reduce performance impact
        // mossTickCheckFrequency controls how often we check (higher = less frequent)
        int checkInterval = Math.max(1, config.mossTickCheckFrequency * 20); // Convert to ticks
        if (tickCounter % checkInterval != 0) {
            return;
        }

        Random random = world.getRandom();

        // Process random blocks similar to how random ticks work
        // We'll check a small number of random positions around each player
        for (var player : world.getPlayers()) {
            // Check random positions in a radius around the player (similar to random tick range)
            int checkRadius = 128; // Vanilla random tick range
            int checksPerPlayer = 3; // Number of random checks per player per interval

            for (int i = 0; i < checksPerPlayer; i++) {
                int x = player.getBlockX() + random.nextInt(checkRadius * 2) - checkRadius;
                int y = player.getBlockY() + random.nextInt(checkRadius) - checkRadius / 2;
                int z = player.getBlockZ() + random.nextInt(checkRadius * 2) - checkRadius;

                // Clamp Y to valid range
                y = Math.max(world.getBottomY(), Math.min(world.getTopYInclusive(), y));

                BlockPos pos = new BlockPos(x, y, z);

                // Check if chunk is loaded
                if (!world.isChunkLoaded(pos)) {
                    continue;
                }

                BlockState state = world.getBlockState(pos);
                tryMossBlock(world, pos, state, random, config);
            }
        }
    }

    private static void tryMossBlock(ServerWorld world, BlockPos pos, BlockState state, Random random, DeeperDarkConfig.ConfigInstance config) {
        Block block = state.getBlock();
        Block mossyVariant = null;
        double speedMultiplier = 1.0;

        // Check if this is a mossable block
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
            return; // Not a mossable block
        }

        // Calculate moss chance
        double chance = config.mossBaseChance * speedMultiplier;

        // Check if underwater (faster mossing)
        FluidState fluidState = world.getFluidState(pos);
        if (fluidState.isIn(FluidTags.WATER)) {
            chance *= config.mossUnderwaterMultiplier;
        }

        // Bonus for nearby mossy blocks (moss spreads)
        int nearbyMossy = countNearbyMossyBlocks(world, pos);
        chance += nearbyMossy * config.mossNearbyBonus;

        // Roll for mossing
        if (random.nextDouble() < chance) {
            // Convert to mossy variant, preserving block state properties where possible
            BlockState mossyState = copyBlockStateProperties(state, mossyVariant.getDefaultState());
            world.setBlockState(pos, mossyState, Block.NOTIFY_ALL);
        }
    }

    private static int countNearbyMossyBlocks(ServerWorld world, BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            BlockState adjacent = world.getBlockState(pos.offset(direction));
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
            if (to.contains(property)) {
                to = copyProperty(from, to, (net.minecraft.state.property.Property) property);
            }
        }
        return to;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, net.minecraft.state.property.Property<T> property) {
        return to.with(property, from.get(property));
    }
}
