package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.HashMap;
import java.util.Map;

public class MossGrowthHandler {

    // Maps unmossed block → mossy variant
    private static final Map<Block, Block> MOSSY = new HashMap<>();
    // Maps mossy variant → unmossed block (used for axe stripping)
    public static final Map<Block, Block> STRIPPED = new HashMap<>();

    static {
        register(Blocks.COBBLESTONE,        Blocks.MOSSY_COBBLESTONE);
        register(Blocks.COBBLESTONE_WALL,   Blocks.MOSSY_COBBLESTONE_WALL);
        register(Blocks.COBBLESTONE_SLAB,   Blocks.MOSSY_COBBLESTONE_SLAB);
        register(Blocks.COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_STAIRS);
        register(Blocks.STONE_BRICKS,       Blocks.MOSSY_STONE_BRICKS);
        register(Blocks.STONE_BRICK_WALL,   Blocks.MOSSY_STONE_BRICK_WALL);
        register(Blocks.STONE_BRICK_SLAB,   Blocks.MOSSY_STONE_BRICK_SLAB);
        register(Blocks.STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_STAIRS);
    }

    private static void register(Block plain, Block mossy) {
        MOSSY.put(plain, mossy);
        STRIPPED.put(mossy, plain);
    }

    public static void register() {
        ServerTickEvents.END_LEVEL_TICK.register(MossGrowthHandler::onWorldTick);
    }

    private static void onWorldTick(ServerLevel world) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        if (!config.mossGrowthEnabled) return;

        // Only run every second to keep overhead low.
        if (world.getLevelData().getGameTime() % 20 != 0) return;

        RandomSource random = world.getRandom();

        // Iterate over chunks near each player — same approach as vanilla random ticks.
        for (var player : world.players()) {
            int centerCX = player.getBlockX() >> 4;
            int centerCZ = player.getBlockZ() >> 4;
            int radius = 6; // ~13x13 chunk grid around player

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int cx = centerCX + dx;
                    int cz = centerCZ + dz;
                    if (!world.hasChunk(cx, cz)) continue;

                    // 5 random positions per chunk per second — like vanilla randomTickSpeed=3
                    for (int i = 0; i < 5; i++) {
                        int x = (cx << 4) + random.nextInt(16);
                        int y = world.getMinY() + random.nextInt(world.getHeight());
                        int z = (cz << 4) + random.nextInt(16);
                        BlockPos pos = new BlockPos(x, y, z);
                        tryMoss(world, pos, world.getBlockState(pos), random, config);
                    }
                }
            }
        }
    }

    private static void tryMoss(ServerLevel world, BlockPos pos, BlockState state,
                                 RandomSource random, DeeperDarkConfig.ConfigInstance config) {
        Block plain = state.getBlock();
        Block mossyVariant = MOSSY.get(plain);
        if (mossyVariant == null) return;

        boolean isStoneBrick = (plain == Blocks.STONE_BRICKS || plain == Blocks.STONE_BRICK_WALL
                || plain == Blocks.STONE_BRICK_SLAB || plain == Blocks.STONE_BRICK_STAIRS);

        boolean adjacentToWater = isAdjacentToWater(world, pos);

        double chance;
        if (adjacentToWater) {
            // Primary mossing — water contact
            chance = config.mossBaseChance * (isStoneBrick ? config.stoneBrickMossMultiplier : 1.0);
        } else if (hasAdjacentMoss(world, pos)) {
            // Slow spreading from existing moss
            chance = config.mossBaseChance * 0.15 * (isStoneBrick ? config.stoneBrickMossMultiplier : 1.0);
        } else {
            return;
        }

        if (random.nextDouble() < chance) {
            world.setBlock(pos, copyProperties(state, mossyVariant.defaultBlockState()), Block.UPDATE_ALL);
        }
    }

    private static boolean isAdjacentToWater(ServerLevel world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockPos adj = pos.relative(dir);
            FluidState fluid = world.getFluidState(adj);
            if (fluid.is(FluidTags.WATER)) return true;
            // Waterlogged blocks count too
            BlockState adjState = world.getBlockState(adj);
            if (adjState.hasProperty(BlockStateProperties.WATERLOGGED)
                    && adjState.getValue(BlockStateProperties.WATERLOGGED)) return true;
        }
        return false;
    }

    private static boolean hasAdjacentMoss(ServerLevel world, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            BlockState adj = world.getBlockState(pos.relative(dir));
            if (STRIPPED.containsKey(adj.getBlock())) return true;
            if (adj.getBlock() == Blocks.MOSS_BLOCK || adj.getBlock() == Blocks.MOSS_CARPET) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static BlockState copyProperties(BlockState from, BlockState to) {
        for (var prop : from.getProperties()) {
            if (to.hasProperty(prop)) {
                to = copyProp(from, to, (net.minecraft.world.level.block.state.properties.Property) prop);
            }
        }
        return to;
    }

    private static <T extends Comparable<T>> BlockState copyProp(
            BlockState from, BlockState to,
            net.minecraft.world.level.block.state.properties.Property<T> prop) {
        return to.setValue(prop, from.getValue(prop));
    }
}
