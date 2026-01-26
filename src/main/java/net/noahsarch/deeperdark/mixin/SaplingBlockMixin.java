package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.noahsarch.deeperdark.Deeperdark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Mixin to add 2x2, 3x3, 4x4, and 5x5 giant oak tree generation for oak saplings.
 * Larger patterns create progressively taller and more impressive trees with curved trunks.
 * Also teleports players standing in sapling positions to the top of grown trees.
 */
@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin {

    /**
     * Inject into the generate method to handle multi-size oak sapling patterns.
     * Also stores player positions for teleportation after tree growth.
     *
     * IMPORTANT: This only triggers actual generation if the sapling is at stage 1.
     * This ensures growth rate matches vanilla (requires stage advancement first).
     */
    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    private void deeperdark$handleGiantOakAndTeleport(ServerWorld world, BlockPos pos, BlockState state, Random random, CallbackInfo ci) {
        // Check if this is an oak sapling
        Block block = state.getBlock();
        if (!isOakSapling(block)) {
            // For non-oak saplings, just handle the player teleportation after growth
            deeperdark$teleportPlayersAfterGrowth(world, pos);
            return;
        }

        // Check if the sapling is ready to grow (stage 1)
        // This matches vanilla behavior where saplings need to advance through stages
        int stage = state.get(Properties.STAGE);
        if (stage == 0) {
            // Not ready yet - check if this is part of a pattern
            int patternSize = deeperdark$getPatternSize(world, pos, block);
            if (patternSize >= 2) {
                // Advance all saplings in the pattern to stage 1, just like vanilla does for single saplings
                BlockPos corner = deeperdark$findPatternCorner(world, pos, state, patternSize);
                if (corner != null) {
                    deeperdark$advanceAllSaplingsInPattern(world, corner, patternSize, state);
                    ci.cancel(); // Don't let vanilla also advance this sapling
                    return;
                }
            }
            // Single sapling or pattern not found - let vanilla handle stage advancement
            return;
        }

        // Stage 1 - ready to attempt growth
        // Check patterns from largest to smallest

        // Check for 10x10 oak sapling pattern (the ultimate tree!)
        BlockPos corner10x10 = deeperdark$findPatternCorner(world, pos, state, 10);
        if (corner10x10 != null) {
            if (deeperdark$generateUltimate10x10Oak(world, corner10x10, state, random)) {
                ci.cancel();
                return;
            }
        }

        // Check for 5x5 oak sapling pattern
        BlockPos corner5x5 = deeperdark$findPatternCorner(world, pos, state, 5);
        if (corner5x5 != null) {
            if (deeperdark$generateColossal5x5Oak(world, corner5x5, state, random)) {
                ci.cancel();
                return;
            }
        }

        // Check for 4x4 oak sapling pattern
        BlockPos corner4x4 = deeperdark$findPatternCorner(world, pos, state, 4);
        if (corner4x4 != null) {
            if (deeperdark$generateGrand4x4Oak(world, corner4x4, state, random)) {
                ci.cancel();
                return;
            }
        }

        // Check for 3x3 oak sapling pattern
        BlockPos corner3x3 = deeperdark$findPatternCorner(world, pos, state, 3);
        if (corner3x3 != null) {
            if (deeperdark$generateMassive3x3Oak(world, corner3x3, state, random)) {
                ci.cancel();
                return;
            }
        }

        // Check for 2x2 oak sapling pattern
        BlockPos cornerPos = deeperdark$findGiant2x2Corner(world, pos, state);
        if (cornerPos != null) {
            if (deeperdark$generateGiantFancyOak(world, cornerPos, state, random)) {
                ci.cancel();
                return;
            }
        }

        // Handle normal generation with teleportation
        deeperdark$teleportPlayersAfterGrowth(world, pos);
    }

    /**
     * Gets the largest pattern size that includes this position.
     */
    @Unique
    private int deeperdark$getPatternSize(ServerWorld world, BlockPos pos, Block block) {
        // Check 10, then 5, 4, 3, 2 (skipping 6-9)
        int[] sizesToCheck = {10, 5, 4, 3, 2};
        for (int size : sizesToCheck) {
            for (int dx = 0; dx >= -(size - 1); dx--) {
                for (int dz = 0; dz >= -(size - 1); dz--) {
                    BlockPos corner = pos.add(dx, 0, dz);
                    if (deeperdark$isNxNPattern(world, corner, block, size)) {
                        return size;
                    }
                }
            }
        }
        return 1;
    }

    /**
     * Advances all saplings in a pattern to stage 1 (like vanilla stage cycling).
     */
    @Unique
    private void deeperdark$advanceAllSaplingsInPattern(ServerWorld world, BlockPos corner, int size, BlockState state) {
        BlockState advancedState = state.with(Properties.STAGE, 1);
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos saplingPos = corner.add(x, 0, z);
                BlockState currentState = world.getBlockState(saplingPos);
                if (currentState.isOf(state.getBlock()) && currentState.get(Properties.STAGE) == 0) {
                    world.setBlockState(saplingPos, advancedState, Block.NOTIFY_ALL);
                }
            }
        }
    }

    @Unique
    private boolean isOakSapling(Block block) {
        return block == Blocks.OAK_SAPLING;
    }

    /**
     * Generic method to find the southwest corner of an NxN sapling pattern.
     */
    @Unique
    private BlockPos deeperdark$findPatternCorner(ServerWorld world, BlockPos pos, BlockState state, int size) {
        Block block = state.getBlock();

        // Check all possible NxN patterns that include this position
        for (int dx = 0; dx >= -(size - 1); dx--) {
            for (int dz = 0; dz >= -(size - 1); dz--) {
                BlockPos corner = pos.add(dx, 0, dz);
                if (deeperdark$isNxNPattern(world, corner, block, size)) {
                    return corner;
                }
            }
        }
        return null;
    }

    @Unique
    private boolean deeperdark$isNxNPattern(ServerWorld world, BlockPos corner, Block block, int size) {
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                if (!world.getBlockState(corner.add(x, 0, z)).isOf(block)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds the southwest corner of a 2x2 sapling pattern if one exists.
     */
    @Unique
    private BlockPos deeperdark$findGiant2x2Corner(ServerWorld world, BlockPos pos, BlockState state) {
        return deeperdark$findPatternCorner(world, pos, state, 2);
    }

    // ==================== TREE GENERATION METHODS ====================

    /**
     * Generates a 3x3 massive oak tree - taller and spindlier than 2x2.
     * Height: 25-35 blocks
     */
    @Unique
    private boolean deeperdark$generateMassive3x3Oak(ServerWorld world, BlockPos corner, BlockState state, Random random) {
        int baseHeight = 25 + random.nextInt(11); // 25-35 blocks tall
        return deeperdark$generateGiantOakTree(world, corner, state, random, 3, baseHeight);
    }

    /**
     * Generates a 4x4 grand oak tree - even taller with more dramatic curves.
     * Height: 35-50 blocks
     */
    @Unique
    private boolean deeperdark$generateGrand4x4Oak(ServerWorld world, BlockPos corner, BlockState state, Random random) {
        int baseHeight = 35 + random.nextInt(16); // 35-50 blocks tall
        return deeperdark$generateGiantOakTree(world, corner, state, random, 4, baseHeight);
    }

    /**
     * Generates a 5x5 colossal oak tree - as tall as mega spruce/jungle trees.
     * Height: 50-70 blocks
     */
    @Unique
    private boolean deeperdark$generateColossal5x5Oak(ServerWorld world, BlockPos corner, BlockState state, Random random) {
        int baseHeight = 50 + random.nextInt(21); // 50-70 blocks tall
        return deeperdark$generateGiantOakTree(world, corner, state, random, 5, baseHeight);
    }

    /**
     * Generates a 10x10 ULTIMATE oak tree - the biggest tree possible!
     * Height: 80-120 blocks with massive branching canopy
     */
    @Unique
    private boolean deeperdark$generateUltimate10x10Oak(ServerWorld world, BlockPos corner, BlockState state, Random random) {
        int baseHeight = 80 + random.nextInt(41); // 80-120 blocks tall
        return deeperdark$generateGiantOakTree(world, corner, state, random, 10, baseHeight);
    }

    /**
     * Core method for generating giant oak trees with curved trunks and spindly branches.
     *
     * @param world The world
     * @param corner Southwest corner of the sapling pattern
     * @param state The sapling block state
     * @param random Random for generation
     * @param trunkSize The size of the trunk base (3, 4, 5, or 10)
     * @param targetHeight Target height for the tree
     */
    @Unique
    private boolean deeperdark$generateGiantOakTree(ServerWorld world, BlockPos corner, BlockState state, Random random, int trunkSize, int targetHeight) {
        // Calculate trunk curve parameters FIRST so we know where the top will be
        Direction.Axis curveAxis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        float curveStrength = (random.nextFloat() - 0.5f) * 2.0f * (trunkSize * 0.6f); // Curve scales with size
        boolean doubleCurve = random.nextFloat() < 0.3f; // 30% chance of S-curve

        // Find the top position BEFORE generating anything
        BlockPos topPos = deeperdark$getTrunkTopPosition(corner, trunkSize, targetHeight, curveAxis, curveStrength, doubleCurve);

        // Collect players standing in the sapling area and TELEPORT THEM FIRST
        Box saplingArea = new Box(
            corner.getX(), corner.getY(), corner.getZ(),
            corner.getX() + trunkSize, corner.getY() + 2, corner.getZ() + trunkSize
        );
        List<PlayerEntity> playersInArea = world.getEntitiesByClass(PlayerEntity.class, saplingArea, p -> true);

        // Teleport players to the top BEFORE generating the tree (so they don't suffocate)
        for (PlayerEntity player : playersInArea) {
            player.teleport(topPos.getX() + 0.5, topPos.getY() + 2, topPos.getZ() + 0.5, false);
            Deeperdark.LOGGER.info("[Deeper Dark] Teleported player {} to top of {}x{} giant oak tree",
                player.getName().getString(), trunkSize, trunkSize);
        }

        // Clear all saplings
        BlockState air = Blocks.AIR.getDefaultState();
        for (int x = 0; x < trunkSize; x++) {
            for (int z = 0; z < trunkSize; z++) {
                world.setBlockState(corner.add(x, 0, z), air, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
            }
        }

        // Generate the curved trunk with buttress roots
        int actualHeight = deeperdark$generateCurvedTrunkWithButtress(world, corner, trunkSize, targetHeight, curveAxis, curveStrength, doubleCurve, random);

        if (actualHeight < 10) {
            // Failed to generate - restore saplings and teleport players back
            for (int x = 0; x < trunkSize; x++) {
                for (int z = 0; z < trunkSize; z++) {
                    world.setBlockState(corner.add(x, 0, z), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
                }
            }
            // Teleport players back down
            for (PlayerEntity player : playersInArea) {
                player.teleport(corner.getX() + trunkSize / 2.0, corner.getY(), corner.getZ() + trunkSize / 2.0, false);
            }
            return false;
        }

        // Generate major branches with sub-branches for large canopy
        List<BlockPos> branchEnds = deeperdark$generateBranchingCanopy(world, corner, trunkSize, actualHeight, curveAxis, curveStrength, doubleCurve, random);

        // Generate leaves at branch ends with support logs to prevent decay
        deeperdark$generateLeafClustersWithSupport(world, corner, trunkSize, actualHeight, curveAxis, curveStrength, doubleCurve, branchEnds, random);

        // Set dirt under the trunk
        BlockState dirt = Blocks.DIRT.getDefaultState();
        for (int x = 0; x < trunkSize; x++) {
            for (int z = 0; z < trunkSize; z++) {
                world.setBlockState(corner.add(x, -1, z), dirt, Block.NOTIFY_ALL);
            }
        }

        return true;
    }

    /**
     * Generates a curved trunk with buttress roots at the base for a wider, more natural look.
     * Returns the actual height achieved.
     */
    @Unique
    private int deeperdark$generateCurvedTrunkWithButtress(ServerWorld world, BlockPos corner, int trunkSize, int targetHeight,
                                               Direction.Axis curveAxis, float curveStrength, boolean doubleCurve, Random random) {
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();

        float centerX = corner.getX() + trunkSize / 2.0f;
        float centerZ = corner.getZ() + trunkSize / 2.0f;

        int actualHeight = 0;

        // Calculate buttress height (bottom 15-20% of the tree has expanded base)
        int buttressHeight = Math.max(3, (int)(targetHeight * 0.15f));

        for (int y = 0; y < targetHeight; y++) {
            // Calculate curve offset at this height
            float progress = (float) y / targetHeight;
            float curveOffset = deeperdark$calculateCurveOffset(progress, curveStrength, doubleCurve);

            // Calculate current trunk center position
            float currentCenterX = centerX;
            float currentCenterZ = centerZ;

            if (curveAxis == Direction.Axis.X) {
                currentCenterX += curveOffset;
            } else {
                currentCenterZ += curveOffset;
            }

            // Calculate trunk radius at this height
            // Base is wider (buttress), then tapers, then narrows towards top
            float baseRadius = trunkSize / 2.0f;
            float currentRadius;

            if (y < buttressHeight) {
                // Buttress zone - wider at the very bottom, smoothly transitioning
                float buttressProgress = (float) y / buttressHeight;
                float buttressExpansion = 1.0f + (1.0f - buttressProgress) * 0.5f; // 50% wider at base
                currentRadius = baseRadius * buttressExpansion;
            } else {
                // Normal tapering above the buttress
                float taperProgress = (float)(y - buttressHeight) / (targetHeight - buttressHeight);
                float radiusMultiplier = 1.0f - (taperProgress * taperProgress * 0.7f); // Quadratic taper
                currentRadius = baseRadius * radiusMultiplier;
            }

            // Minimum radius of 0.5 blocks at the very top
            currentRadius = Math.max(currentRadius, 0.5f);

            boolean placedAny = false;

            // Place logs in a circle around the current center
            int searchRadius = (int) Math.ceil(currentRadius) + 1;
            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                    int blockX = (int) Math.floor(currentCenterX) + dx;
                    int blockZ = (int) Math.floor(currentCenterZ) + dz;

                    // Check if this position is within the trunk radius
                    float distX = blockX + 0.5f - currentCenterX;
                    float distZ = blockZ + 0.5f - currentCenterZ;
                    float dist = MathHelper.sqrt(distX * distX + distZ * distZ);

                    if (dist <= currentRadius) {
                        BlockPos logPos = new BlockPos(blockX, corner.getY() + y, blockZ);

                        // Check if we can place here
                        BlockState existingState = world.getBlockState(logPos);
                        if (existingState.isAir() || existingState.isOf(Blocks.OAK_LEAVES) ||
                            existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {
                            world.setBlockState(logPos, oakLog, Block.NOTIFY_ALL);
                            placedAny = true;
                        }
                    }
                }
            }

            if (placedAny) {
                actualHeight = y + 1;
            }
        }

        return actualHeight;
    }

    /**
     * Calculates the curve offset for a given height progress.
     * Uses a sine-based curve for smooth bending.
     */
    @Unique
    private float deeperdark$calculateCurveOffset(float progress, float curveStrength, boolean doubleCurve) {
        if (doubleCurve) {
            // S-curve: goes one way then the other
            return curveStrength * (float) Math.sin(progress * Math.PI * 2);
        } else {
            // Single curve: bends in one direction
            return curveStrength * (float) Math.sin(progress * Math.PI);
        }
    }

    /**
     * Gets the position of the trunk top accounting for curve.
     */
    @Unique
    private BlockPos deeperdark$getTrunkTopPosition(BlockPos corner, int trunkSize, int height,
                                                    Direction.Axis curveAxis, float curveStrength, boolean doubleCurve) {
        float progress = 1.0f;
        float curveOffset = deeperdark$calculateCurveOffset(progress, curveStrength, doubleCurve);

        float centerX = corner.getX() + trunkSize / 2.0f;
        float centerZ = corner.getZ() + trunkSize / 2.0f;

        if (curveAxis == Direction.Axis.X) {
            centerX += curveOffset;
        } else {
            centerZ += curveOffset;
        }

        return new BlockPos((int) centerX, corner.getY() + height, (int) centerZ);
    }

    /**
     * Generates spindly branches - fewer but longer for larger trees.
     * Branches extend from the upper 60% of the trunk.
     * Returns a list of branch end positions for leaf cluster placement.
     */
    @Unique
    private List<BlockPos> deeperdark$generateBranchingCanopy(ServerWorld world, BlockPos corner, int trunkSize, int height,
                                                    Direction.Axis curveAxis, float curveStrength, boolean doubleCurve, Random random) {
        List<BlockPos> branchEnds = new ArrayList<>();

        // Determine number of branch levels based on tree size
        int branchLevels = trunkSize + 2; // 5, 6, 7, 12 levels for 3x3, 4x4, 5x5, 10x10

        // Branches start at 35% height and go to 85% height
        int startHeight = (int) (height * 0.35f);
        int endHeight = (int) (height * 0.85f);
        int levelSpacing = Math.max(1, (endHeight - startHeight) / branchLevels);

        for (int level = 0; level < branchLevels; level++) {
            int branchY = startHeight + (level * levelSpacing) + random.nextInt(Math.max(1, levelSpacing / 2));
            if (branchY >= height) continue;

            // Calculate trunk center at this height
            float progress = (float) branchY / height;
            float curveOffset = deeperdark$calculateCurveOffset(progress, curveStrength, doubleCurve);

            float trunkCenterX = corner.getX() + trunkSize / 2.0f;
            float trunkCenterZ = corner.getZ() + trunkSize / 2.0f;

            if (curveAxis == Direction.Axis.X) {
                trunkCenterX += curveOffset;
            } else {
                trunkCenterZ += curveOffset;
            }

            // Generate more branches for larger trees, spread around the trunk
            int branchCount = 3 + (trunkSize / 2) + random.nextInt(3); // More branches for bigger trees
            float startAngle = random.nextFloat() * (float) Math.PI * 2;

            for (int b = 0; b < branchCount; b++) {
                float angle = startAngle + (b * (float) Math.PI * 2 / branchCount) + (random.nextFloat() - 0.5f) * 0.4f;

                // Branch length scales with tree size and creates wider canopy
                // Lower branches are longer, upper branches are shorter
                float heightFactor = 1.0f - (progress * 0.4f);
                int baseBranchLength = (int) ((trunkSize * 3 + 5 + random.nextInt(6)) * heightFactor);

                // Generate the main branch and get its end position
                BlockPos mainBranchEnd = deeperdark$generateMainBranch(world,
                    (int) trunkCenterX, corner.getY() + branchY, (int) trunkCenterZ,
                    angle, baseBranchLength, random);

                if (mainBranchEnd != null) {
                    branchEnds.add(mainBranchEnd);

                    // Generate sub-branches from the main branch for larger trees
                    if (trunkSize >= 4) {
                        List<BlockPos> subBranchEnds = deeperdark$generateSubBranches(world, mainBranchEnd,
                            angle, baseBranchLength, trunkSize, random);
                        branchEnds.addAll(subBranchEnds);
                    }
                }
            }
        }

        // Add branch ends at the top of the tree
        BlockPos topPos = deeperdark$getTrunkTopPosition(corner, trunkSize, height, curveAxis, curveStrength, doubleCurve);
        branchEnds.add(topPos);

        return branchEnds;
    }

    /**
     * Generates a main branch extending outward and slightly upward.
     * Returns the end position of the branch.
     */
    @Unique
    private BlockPos deeperdark$generateMainBranch(ServerWorld world, int startX, int startY, int startZ,
                                           float angle, int length, Random random) {
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();

        float x = startX + 0.5f;
        float y = startY + 0.5f;
        float z = startZ + 0.5f;

        float dx = MathHelper.cos(angle);
        float dz = MathHelper.sin(angle);
        float dy = 0.15f + random.nextFloat() * 0.25f; // Slight upward angle

        // Normalize direction
        float mag = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= mag;
        dy /= mag;
        dz /= mag;

        BlockPos lastPos = null;

        for (int i = 0; i < length; i++) {
            x += dx;
            y += dy;
            z += dz;

            // Add some gentle waviness
            if (i > 3) {
                x += (random.nextFloat() - 0.5f) * 0.2f;
                z += (random.nextFloat() - 0.5f) * 0.2f;
            }

            BlockPos branchPos = new BlockPos((int) x, (int) y, (int) z);
            BlockState existingState = world.getBlockState(branchPos);

            if (existingState.isAir() || existingState.isOf(Blocks.OAK_LEAVES) ||
                existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {

                // Determine axis for the log based on direction
                Direction.Axis axis = Direction.Axis.Y;
                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
                    axis = Direction.Axis.X;
                } else if (Math.abs(dz) > Math.abs(dy)) {
                    axis = Direction.Axis.Z;
                }

                world.setBlockState(branchPos, oakLog.with(PillarBlock.AXIS, axis), Block.NOTIFY_ALL);
                lastPos = branchPos;
            }
        }

        return lastPos;
    }

    /**
     * Generates sub-branches splitting off from a main branch end.
     * Creates a more natural, spreading canopy.
     */
    @Unique
    private List<BlockPos> deeperdark$generateSubBranches(ServerWorld world, BlockPos branchEnd,
                                                          float parentAngle, int parentLength, int trunkSize, Random random) {
        List<BlockPos> subBranchEnds = new ArrayList<>();
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();

        // Number of sub-branches scales with tree size
        int subBranchCount = 2 + random.nextInt(trunkSize >= 10 ? 4 : 2);

        for (int i = 0; i < subBranchCount; i++) {
            // Sub-branches spread out from the parent direction
            float angleOffset = (random.nextFloat() - 0.5f) * (float) Math.PI * 0.8f; // +/- 72 degrees
            float subAngle = parentAngle + angleOffset;

            // Sub-branch length is shorter than parent
            int subLength = parentLength / 3 + random.nextInt(parentLength / 3 + 1);

            float x = branchEnd.getX() + 0.5f;
            float y = branchEnd.getY() + 0.5f;
            float z = branchEnd.getZ() + 0.5f;

            float dx = MathHelper.cos(subAngle);
            float dz = MathHelper.sin(subAngle);
            float dy = 0.1f + random.nextFloat() * 0.3f; // Mostly horizontal to slightly upward

            // Normalize
            float mag = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= mag;
            dy /= mag;
            dz /= mag;

            BlockPos lastPos = branchEnd;

            for (int j = 0; j < subLength; j++) {
                x += dx;
                y += dy;
                z += dz;

                // Add waviness
                if (j > 1) {
                    x += (random.nextFloat() - 0.5f) * 0.25f;
                    z += (random.nextFloat() - 0.5f) * 0.25f;
                }

                BlockPos subBranchPos = new BlockPos((int) x, (int) y, (int) z);
                BlockState existingState = world.getBlockState(subBranchPos);

                if (existingState.isAir() || existingState.isOf(Blocks.OAK_LEAVES) ||
                    existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {

                    Direction.Axis axis = Direction.Axis.Y;
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
                        axis = Direction.Axis.X;
                    } else if (Math.abs(dz) > Math.abs(dy)) {
                        axis = Direction.Axis.Z;
                    }

                    world.setBlockState(subBranchPos, oakLog.with(PillarBlock.AXIS, axis), Block.NOTIFY_ALL);
                    lastPos = subBranchPos;
                }
            }

            subBranchEnds.add(lastPos);
        }

        return subBranchEnds;
    }

    /**
     * Generates leaf clusters at branch ends with support logs inside to prevent decay.
     * This ensures leaves don't decay due to being too far from logs.
     */
    @Unique
    private void deeperdark$generateLeafClustersWithSupport(ServerWorld world, BlockPos corner, int trunkSize, int height,
                                                  Direction.Axis curveAxis, float curveStrength, boolean doubleCurve,
                                                  List<BlockPos> branchEnds, Random random) {
        BlockState oakLeaves = Blocks.OAK_LEAVES.getDefaultState().with(Properties.DISTANCE_1_7, 1);

        // Leaf cluster size scales with tree size
        int baseClusterRadius = 3 + (trunkSize / 2);

        // Generate leaf clusters at each branch end
        for (BlockPos branchEnd : branchEnds) {
            int clusterRadius = baseClusterRadius + random.nextInt(2);

            // Generate support log network FIRST, then fill with leaves
            // This ensures proper decay prevention
            deeperdark$generateLeafSupportNetwork(world, branchEnd, clusterRadius, random);

            // Now place the leaf sphere around the support network
            for (int dx = -clusterRadius; dx <= clusterRadius; dx++) {
                for (int dy = -clusterRadius / 2; dy <= clusterRadius; dy++) {
                    for (int dz = -clusterRadius; dz <= clusterRadius; dz++) {
                        // Ellipsoid shape - wider than tall
                        float distSq = (dx * dx) + (dy * dy * 2.5f) + (dz * dz);
                        float radiusSq = clusterRadius * clusterRadius;

                        if (distSq <= radiusSq) {
                            // Add some randomness to the edges
                            if (distSq > radiusSq * 0.7f && random.nextFloat() > 0.6f) {
                                continue;
                            }

                            BlockPos leafPos = branchEnd.add(dx, dy, dz);
                            BlockState existingState = world.getBlockState(leafPos);

                            // Only place leaves where there isn't already a log
                            if (existingState.isAir() || existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {
                                world.setBlockState(leafPos, oakLeaves, Block.NOTIFY_ALL);
                            }
                        }
                    }
                }
            }
        }

        // Generate diagonal crown branches to fill in the sparse top
        BlockPos topPos = deeperdark$getTrunkTopPosition(corner, trunkSize, height, curveAxis, curveStrength, doubleCurve);
        List<BlockPos> crownBranchEnds = deeperdark$generateDiagonalCrownBranches(world, topPos, trunkSize, random);

        // Add crown branch ends to get leaf clusters
        int crownRadius = trunkSize + 2;

        // Generate support network for crown (at the trunk top)
        deeperdark$generateLeafSupportNetwork(world, topPos, crownRadius, random);

        // Generate leaf clusters at diagonal branch ends (now extending upward)
        for (BlockPos crownBranchEnd : crownBranchEnds) {
            int crownClusterRadius = 3 + random.nextInt(2);
            deeperdark$generateLeafSupportNetwork(world, crownBranchEnd, crownClusterRadius, random);

            for (int dx = -crownClusterRadius; dx <= crownClusterRadius; dx++) {
                // Extend leaves more in both directions vertically since branches now go up
                for (int dy = -crownClusterRadius; dy <= crownClusterRadius; dy++) {
                    for (int dz = -crownClusterRadius; dz <= crownClusterRadius; dz++) {
                        // More spherical shape for the upward-reaching cluster ends
                        float distSq = (dx * dx) + (dy * dy * 1.5f) + (dz * dz);
                        float radiusSq = crownClusterRadius * crownClusterRadius;

                        if (distSq <= radiusSq && (distSq < radiusSq * 0.6f || random.nextFloat() > 0.4f)) {
                            BlockPos leafPos = crownBranchEnd.add(dx, dy, dz);
                            BlockState existingState = world.getBlockState(leafPos);
                            if (existingState.isAir() || existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {
                                world.setBlockState(leafPos, oakLeaves, Block.NOTIFY_ALL);
                            }
                        }
                    }
                }
            }
        }

        // Fill in the main crown with leaves - now extends upward more
        int crownHeightUp = crownRadius + trunkSize / 2; // Extend higher for fuller top
        for (int dx = -crownRadius; dx <= crownRadius; dx++) {
            for (int dy = -2; dy <= crownHeightUp; dy++) {
                for (int dz = -crownRadius; dz <= crownRadius; dz++) {
                    // Taper the radius as we go higher to create a more natural dome/cone shape
                    float heightFactor = dy > 0 ? 1.0f - ((float)dy / crownHeightUp) * 0.6f : 1.0f;
                    float effectiveRadius = crownRadius * heightFactor;

                    float dist = MathHelper.sqrt(dx * dx + dz * dz);
                    if (dist <= effectiveRadius && (dist < effectiveRadius - 0.5f || random.nextFloat() < 0.5f)) {
                        BlockPos leafPos = topPos.add(dx, dy, dz);
                        BlockState existingState = world.getBlockState(leafPos);
                        if (existingState.isAir() || existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {
                            world.setBlockState(leafPos, oakLeaves, Block.NOTIFY_ALL);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates a network of support logs within a leaf cluster to prevent decay.
     * Logs are placed in a compact interior pattern that stays hidden inside the leaves.
     * Leaves can be up to 7 blocks from a log before they decay.
     */
    @Unique
    private void deeperdark$generateLeafSupportNetwork(ServerWorld world, BlockPos center, int radius, Random random) {
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();

        // Calculate the interior zone where logs should be placed (stay 2 blocks inside the leaf edge)
        int interiorRadius = Math.max(1, radius - 2);

        // Place a central vertical column of logs
        for (int dy = -1; dy <= 1; dy++) {
            BlockPos logPos = center.add(0, dy, 0);
            BlockState existingState = world.getBlockState(logPos);
            if (existingState.isAir() || existingState.isOf(Blocks.OAK_LEAVES) ||
                existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {
                world.setBlockState(logPos, oakLog, Block.NOTIFY_ALL);
            }
        }

        // Place logs in 4 cardinal directions, but only extending into the interior
        // Space them every 3 blocks to ensure all leaves are within 7 blocks of a log
        float[] angles = {0, (float)(Math.PI * 0.5), (float)Math.PI, (float)(Math.PI * 1.5)};

        for (float angle : angles) {
            float dx = MathHelper.cos(angle);
            float dz = MathHelper.sin(angle);

            // Place logs along this direction, but stay inside the interior
            for (int dist = 3; dist <= interiorRadius; dist += 3) {
                int x = (int)(dx * dist);
                int z = (int)(dz * dist);

                BlockPos logPos = center.add(x, 0, z);
                BlockState existingState = world.getBlockState(logPos);

                // Place log if position is air or replaceable (will be filled with leaves later)
                if (existingState.isAir() || existingState.isOf(Blocks.OAK_LEAVES) ||
                    existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {

                    // Determine axis based on direction
                    Direction.Axis axis = Direction.Axis.Y;
                    if (Math.abs(dx) > Math.abs(dz)) {
                        axis = Direction.Axis.X;
                    } else if (Math.abs(dz) > 0.1f) {
                        axis = Direction.Axis.Z;
                    }

                    world.setBlockState(logPos, oakLog.with(PillarBlock.AXIS, axis), Block.NOTIFY_ALL);
                }
            }
        }

        // Add a few random interior supports for larger clusters, well inside the leaf mass
        if (radius > 4) {
            int extraSupports = radius / 2;
            for (int i = 0; i < extraSupports; i++) {
                int x = random.nextInt(interiorRadius * 2 + 1) - interiorRadius;
                int z = random.nextInt(interiorRadius * 2 + 1) - interiorRadius;
                int y = random.nextInt(3) - 1; // Spread vertically too
                if (x * x + z * z <= interiorRadius * interiorRadius * 0.6f) {
                    BlockPos supportPos = center.add(x, y, z);
                    if (world.getBlockState(supportPos).isAir() || world.getBlockState(supportPos).isOf(Blocks.OAK_LEAVES)) {
                        world.setBlockState(supportPos, oakLog, Block.NOTIFY_ALL);
                    }
                }
            }
        }
    }

    /**
     * Generates diagonal branches radiating UPWARD from the upper trunk to fill in the crown.
     * These branches extend outward and upward to create a full, natural crown shape.
     * Returns the end positions of these branches for leaf cluster placement.
     */
    @Unique
    private List<BlockPos> deeperdark$generateDiagonalCrownBranches(ServerWorld world, BlockPos topPos, int trunkSize, Random random) {
        List<BlockPos> branchEnds = new ArrayList<>();
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();

        // Generate multiple tiers of upward-reaching branches from the upper trunk
        int tierCount = 2 + (trunkSize / 3); // More tiers for larger trees
        int baseHeight = topPos.getY();

        for (int tier = 0; tier < tierCount; tier++) {
            // Start each tier at different heights below the top (30% to 80% of the way up from trunk branches)
            int tierHeight = baseHeight - (int)((tier + 1) * trunkSize * 0.8f);

            // Generate 4-6 branches per tier, spread around the trunk
            int branchCount = 4 + random.nextInt(3);
            float startAngle = random.nextFloat() * (float)Math.PI * 2;

            // Branch length scales with tree size and tier (lower tiers have longer branches)
            int branchLength = trunkSize + 4 + random.nextInt(4) + (tierCount - tier);

            for (int b = 0; b < branchCount; b++) {
                float angle = startAngle + (b * (float)Math.PI * 2 / branchCount) + (random.nextFloat() - 0.5f) * 0.4f;

                float x = topPos.getX() + 0.5f;
                float y = tierHeight + 0.5f;
                float z = topPos.getZ() + 0.5f;

                float dx = MathHelper.cos(angle);
                float dz = MathHelper.sin(angle);
                // Key fix: branches go UPWARD and outward, with steeper angles for inner tiers
                float dy = 0.4f + random.nextFloat() * 0.3f + (tier * 0.1f);

                // Normalize
                float mag = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
                dx /= mag;
                dy /= mag;
                dz /= mag;

                BlockPos lastPos = new BlockPos((int)x, tierHeight, (int)z);

                for (int i = 0; i < branchLength; i++) {
                    x += dx;
                    y += dy;
                    z += dz;

                    // Add slight waviness
                    if (i > 2) {
                        x += (random.nextFloat() - 0.5f) * 0.15f;
                        z += (random.nextFloat() - 0.5f) * 0.15f;
                    }

                    BlockPos branchPos = new BlockPos((int)x, (int)y, (int)z);
                    BlockState existingState = world.getBlockState(branchPos);

                    if (existingState.isAir() || existingState.isOf(Blocks.OAK_LEAVES) ||
                        existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {

                        Direction.Axis axis = Direction.Axis.Y;
                        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
                            axis = Direction.Axis.X;
                        } else if (Math.abs(dz) > Math.abs(dy)) {
                            axis = Direction.Axis.Z;
                        }

                        world.setBlockState(branchPos, oakLog.with(PillarBlock.AXIS, axis), Block.NOTIFY_ALL);
                        lastPos = branchPos;
                    }
                }

                branchEnds.add(lastPos);
            }
        }

        // Also add some short upward branches right at the top for the peak
        int peakBranches = 3 + random.nextInt(2);
        float peakStartAngle = random.nextFloat() * (float)Math.PI * 2;

        for (int b = 0; b < peakBranches; b++) {
            float angle = peakStartAngle + (b * (float)Math.PI * 2 / peakBranches);
            float x = topPos.getX() + 0.5f;
            float y = topPos.getY() + 0.5f;
            float z = topPos.getZ() + 0.5f;

            float dx = MathHelper.cos(angle) * 0.3f;
            float dz = MathHelper.sin(angle) * 0.3f;
            float dy = 0.9f; // Almost straight up

            int peakLength = 3 + random.nextInt(3);
            BlockPos lastPos = topPos;

            for (int i = 0; i < peakLength; i++) {
                x += dx;
                y += dy;
                z += dz;

                BlockPos branchPos = new BlockPos((int)x, (int)y, (int)z);
                BlockState existingState = world.getBlockState(branchPos);

                if (existingState.isAir() || existingState.isOf(Blocks.OAK_LEAVES) ||
                    existingState.isIn(net.minecraft.registry.tag.BlockTags.REPLACEABLE_BY_TREES)) {
                    world.setBlockState(branchPos, oakLog, Block.NOTIFY_ALL);
                    lastPos = branchPos;
                }
            }

            branchEnds.add(lastPos);
        }

        return branchEnds;
    }

    /**
     * Generates a giant fancy oak tree with a 2x2 trunk base.
     */
    @Unique
    private boolean deeperdark$generateGiantFancyOak(ServerWorld world, BlockPos corner, BlockState state, Random random) {
        // Collect players standing in the sapling area before clearing
        List<PlayerEntity> playersToTeleport = new ArrayList<>();
        Box saplingArea = new Box(
            corner.getX(), corner.getY(), corner.getZ(),
            corner.getX() + 2, corner.getY() + 2, corner.getZ() + 2
        );
        playersToTeleport.addAll(world.getEntitiesByClass(PlayerEntity.class, saplingArea, p -> true));

        // Get the FANCY_OAK feature
        RegistryEntry<ConfiguredFeature<?, ?>> fancyOakFeature = world.getRegistryManager()
            .getOrThrow(RegistryKeys.CONFIGURED_FEATURE)
            .getOptional(TreeConfiguredFeatures.FANCY_OAK)
            .orElse(null);

        if (fancyOakFeature == null) {
            return false;
        }

        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();

        // Clear the 2x2 saplings
        BlockState air = Blocks.AIR.getDefaultState();
        world.setBlockState(corner, air, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        world.setBlockState(corner.east(), air, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        world.setBlockState(corner.south(), air, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        world.setBlockState(corner.south().east(), air, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);

        // Generate the fancy oak at the corner position
        // The LargeOakTrunkPlacer will create a single-trunk fancy tree
        // We'll generate it and then add extra trunk blocks for the 2x2 base
        BlockPos treePos = corner;

        if (fancyOakFeature.value().generate(world, chunkGenerator, random, treePos)) {
            // Find the height of the tree by scanning for logs
            int treeHeight = deeperdark$findTreeHeight(world, corner);

            // Add 2x2 trunk blocks at the base (the tree already placed some)
            deeperdark$fill2x2TrunkBase(world, corner, treeHeight);

            // Teleport players to the top of the tree
            if (!playersToTeleport.isEmpty() && treeHeight > 0) {
                BlockPos topPos = corner.up(treeHeight + 1);
                for (PlayerEntity player : playersToTeleport) {
                    player.teleport(topPos.getX() + 0.5, topPos.getY(), topPos.getZ() + 0.5, false);
                    Deeperdark.LOGGER.info("[Deeper Dark] Teleported player {} to top of growing tree", player.getName().getString());
                }
            }

            return true;
        }

        // Failed to generate - restore saplings
        world.setBlockState(corner, state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        world.setBlockState(corner.east(), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        world.setBlockState(corner.south(), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        world.setBlockState(corner.south().east(), state, Block.SKIP_REDRAW_AND_BLOCK_ENTITY_REPLACED_CALLBACK);
        return false;
    }

    @Unique
    private int deeperdark$findTreeHeight(ServerWorld world, BlockPos base) {
        int height = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int y = 0; y < 40; y++) {
            mutable.set(base.getX(), base.getY() + y, base.getZ());
            if (world.getBlockState(mutable).isOf(Blocks.OAK_LOG)) {
                height = y;
            }
        }
        return height;
    }

    @Unique
    private void deeperdark$fill2x2TrunkBase(ServerWorld world, BlockPos corner, int height) {
        // Fill in a 2x2 trunk for the lower portion of the tree
        int trunkHeight = Math.min(height / 2 + 2, height); // Make 2x2 trunk for about half the height
        BlockState oakLog = Blocks.OAK_LOG.getDefaultState();

        for (int y = 0; y < trunkHeight; y++) {
            BlockPos pos = corner.up(y);
            // Only place if not already a log (to preserve branches)
            if (!world.getBlockState(pos).isOf(Blocks.OAK_LOG)) {
                world.setBlockState(pos, oakLog, Block.NOTIFY_ALL);
            }
            if (!world.getBlockState(pos.east()).isOf(Blocks.OAK_LOG)) {
                world.setBlockState(pos.east(), oakLog, Block.NOTIFY_ALL);
            }
            if (!world.getBlockState(pos.south()).isOf(Blocks.OAK_LOG)) {
                world.setBlockState(pos.south(), oakLog, Block.NOTIFY_ALL);
            }
            if (!world.getBlockState(pos.south().east()).isOf(Blocks.OAK_LOG)) {
                world.setBlockState(pos.south().east(), oakLog, Block.NOTIFY_ALL);
            }
        }

        // Set dirt under the trunk
        BlockState dirt = Blocks.DIRT.getDefaultState();
        world.setBlockState(corner.down(), dirt, Block.NOTIFY_ALL);
        world.setBlockState(corner.east().down(), dirt, Block.NOTIFY_ALL);
        world.setBlockState(corner.south().down(), dirt, Block.NOTIFY_ALL);
        world.setBlockState(corner.south().east().down(), dirt, Block.NOTIFY_ALL);
    }

    /**
     * Handles teleporting players to the top of any tree that grows while they're standing in it.
     */
    @Unique
    private void deeperdark$teleportPlayersAfterGrowth(ServerWorld world, BlockPos pos) {
        // Get players standing in the sapling position
        Box saplingArea = new Box(pos).expand(0.5, 1, 0.5);
        List<PlayerEntity> playersToTeleport = new ArrayList<>(
            world.getEntitiesByClass(PlayerEntity.class, saplingArea, p -> true)
        );

        if (playersToTeleport.isEmpty()) {
            return; // No players to teleport
        }

        // Schedule a delayed check to see if tree grew
        var server = world.getServer();
        if (server != null) {
            server.execute(() -> {
                // Check if a tree grew (there's now a log where the sapling was)
                BlockState newState = world.getBlockState(pos);
                if (newState.isOf(Blocks.OAK_LOG) || newState.isOf(Blocks.BIRCH_LOG) ||
                    newState.isOf(Blocks.SPRUCE_LOG) || newState.isOf(Blocks.JUNGLE_LOG) ||
                    newState.isOf(Blocks.ACACIA_LOG) || newState.isOf(Blocks.DARK_OAK_LOG) ||
                    newState.isOf(Blocks.CHERRY_LOG) || newState.isOf(Blocks.MANGROVE_LOG) ||
                    newState.isOf(Blocks.PALE_OAK_LOG)) {

                    // Find tree height
                    int treeHeight = 0;
                    for (int y = 0; y < 40; y++) {
                        if (world.getBlockState(pos.up(y)).isIn(net.minecraft.registry.tag.BlockTags.LOGS)) {
                            treeHeight = y;
                        }
                    }

                    if (treeHeight > 0) {
                        BlockPos topPos = pos.up(treeHeight + 1);
                        for (PlayerEntity player : playersToTeleport) {
                            if (player.isAlive()) {
                                player.teleport(topPos.getX() + 0.5, topPos.getY(), topPos.getZ() + 0.5, false);
                                Deeperdark.LOGGER.info("[Deeper Dark] Teleported player {} to top of growing tree", player.getName().getString());
                            }
                        }
                    }
                }
            });
        }
    }
}
