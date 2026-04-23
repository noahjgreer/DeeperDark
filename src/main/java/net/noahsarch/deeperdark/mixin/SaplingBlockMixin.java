package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.noahsarch.deeperdark.Deeperdark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin {

    @Inject(method = "advanceTree", at = @At("HEAD"), cancellable = true)
    private void deeperdark$handleGiantOakAndTeleport(ServerLevel world, BlockPos pos, BlockState state, RandomSource random, CallbackInfo ci) {
        Block block = state.getBlock();
        if (!isOakSapling(block)) {
            deeperdark$teleportPlayersAfterGrowth(world, pos);
            return;
        }

        int stage = state.getValue(BlockStateProperties.STAGE);
        if (stage == 0) {
            int patternSize = deeperdark$getPatternSize(world, pos, block);
            if (patternSize >= 2) {
                BlockPos corner = deeperdark$findPatternCorner(world, pos, state, patternSize);
                if (corner != null) {
                    deeperdark$advanceAllSaplingsInPattern(world, corner, patternSize, state);
                    ci.cancel();
                    return;
                }
            }
            return;
        }

        BlockPos corner10x10 = deeperdark$findPatternCorner(world, pos, state, 10);
        if (corner10x10 != null) {
            if (deeperdark$generateUltimate10x10Oak(world, corner10x10, state, random)) {
                ci.cancel();
                return;
            }
        }

        BlockPos corner5x5 = deeperdark$findPatternCorner(world, pos, state, 5);
        if (corner5x5 != null) {
            if (deeperdark$generateColossal5x5Oak(world, corner5x5, state, random)) {
                ci.cancel();
                return;
            }
        }

        BlockPos corner4x4 = deeperdark$findPatternCorner(world, pos, state, 4);
        if (corner4x4 != null) {
            if (deeperdark$generateGrand4x4Oak(world, corner4x4, state, random)) {
                ci.cancel();
                return;
            }
        }

        BlockPos corner3x3 = deeperdark$findPatternCorner(world, pos, state, 3);
        if (corner3x3 != null) {
            if (deeperdark$generateMassive3x3Oak(world, corner3x3, state, random)) {
                ci.cancel();
                return;
            }
        }

        BlockPos cornerPos = deeperdark$findGiant2x2Corner(world, pos, state);
        if (cornerPos != null) {
            if (deeperdark$generateGiantFancyOak(world, cornerPos, state, random)) {
                ci.cancel();
                return;
            }
        }

        deeperdark$teleportPlayersAfterGrowth(world, pos);
    }

    @Unique
    private int deeperdark$getPatternSize(ServerLevel world, BlockPos pos, Block block) {
        int[] sizesToCheck = {10, 5, 4, 3, 2};
        for (int size : sizesToCheck) {
            for (int dx = 0; dx >= -(size - 1); dx--) {
                for (int dz = 0; dz >= -(size - 1); dz--) {
                    BlockPos corner = pos.offset(dx, 0, dz);
                    if (deeperdark$isNxNPattern(world, corner, block, size)) {
                        return size;
                    }
                }
            }
        }
        return 1;
    }

    @Unique
    private void deeperdark$advanceAllSaplingsInPattern(ServerLevel world, BlockPos corner, int size, BlockState state) {
        BlockState advancedState = state.setValue(BlockStateProperties.STAGE, 1);
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                BlockPos saplingPos = corner.offset(x, 0, z);
                BlockState currentState = world.getBlockState(saplingPos);
                if (currentState.is(state.getBlock()) && currentState.getValue(BlockStateProperties.STAGE) == 0) {
                    world.setBlock(saplingPos, advancedState, Block.UPDATE_ALL);
                }
            }
        }
    }

    @Unique
    private boolean isOakSapling(Block block) {
        return block == Blocks.OAK_SAPLING;
    }

    @Unique
    private BlockPos deeperdark$findPatternCorner(ServerLevel world, BlockPos pos, BlockState state, int size) {
        Block block = state.getBlock();
        for (int dx = 0; dx >= -(size - 1); dx--) {
            for (int dz = 0; dz >= -(size - 1); dz--) {
                BlockPos corner = pos.offset(dx, 0, dz);
                if (deeperdark$isNxNPattern(world, corner, block, size)) {
                    return corner;
                }
            }
        }
        return null;
    }

    @Unique
    private boolean deeperdark$isNxNPattern(ServerLevel world, BlockPos corner, Block block, int size) {
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                if (!world.getBlockState(corner.offset(x, 0, z)).is(block)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Unique
    private BlockPos deeperdark$findGiant2x2Corner(ServerLevel world, BlockPos pos, BlockState state) {
        return deeperdark$findPatternCorner(world, pos, state, 2);
    }

    @Unique
    private boolean deeperdark$generateMassive3x3Oak(ServerLevel world, BlockPos corner, BlockState state, RandomSource random) {
        int baseHeight = 25 + random.nextInt(11);
        return deeperdark$generateGiantOakTree(world, corner, state, random, 3, baseHeight);
    }

    @Unique
    private boolean deeperdark$generateGrand4x4Oak(ServerLevel world, BlockPos corner, BlockState state, RandomSource random) {
        int baseHeight = 35 + random.nextInt(16);
        return deeperdark$generateGiantOakTree(world, corner, state, random, 4, baseHeight);
    }

    @Unique
    private boolean deeperdark$generateColossal5x5Oak(ServerLevel world, BlockPos corner, BlockState state, RandomSource random) {
        int baseHeight = 50 + random.nextInt(21);
        return deeperdark$generateGiantOakTree(world, corner, state, random, 5, baseHeight);
    }

    @Unique
    private boolean deeperdark$generateUltimate10x10Oak(ServerLevel world, BlockPos corner, BlockState state, RandomSource random) {
        int baseHeight = 80 + random.nextInt(41);
        return deeperdark$generateGiantOakTree(world, corner, state, random, 10, baseHeight);
    }

    @Unique
    private boolean deeperdark$generateGiantOakTree(ServerLevel world, BlockPos corner, BlockState state, RandomSource random, int trunkSize, int targetHeight) {
        Direction.Axis curveAxis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;
        float curveStrength = (random.nextFloat() - 0.5f) * 2.0f * (trunkSize * 0.6f);
        boolean doubleCurve = random.nextFloat() < 0.3f;

        BlockPos topPos = deeperdark$getTrunkTopPosition(corner, trunkSize, targetHeight, curveAxis, curveStrength, doubleCurve);

        AABB saplingArea = new AABB(
            corner.getX(), corner.getY(), corner.getZ(),
            corner.getX() + trunkSize, corner.getY() + 2, corner.getZ() + trunkSize
        );
        List<Player> playersInArea = world.getEntitiesOfClass(Player.class, saplingArea, p -> true);

        for (Player player : playersInArea) {
            player.teleportTo(topPos.getX() + 0.5, topPos.getY() + 2, topPos.getZ() + 0.5);
            Deeperdark.LOGGER.info("[Deeper Dark] Teleported player {} to top of {}x{} giant oak tree",
                player.getName().getString(), trunkSize, trunkSize);
        }

        BlockState air = Blocks.AIR.defaultBlockState();
        for (int x = 0; x < trunkSize; x++) {
            for (int z = 0; z < trunkSize; z++) {
                world.setBlock(corner.offset(x, 0, z), air, Block.UPDATE_NONE);
            }
        }

        int actualHeight = deeperdark$generateCurvedTrunkWithButtress(world, corner, trunkSize, targetHeight, curveAxis, curveStrength, doubleCurve, random);

        if (actualHeight < 10) {
            for (int x = 0; x < trunkSize; x++) {
                for (int z = 0; z < trunkSize; z++) {
                    world.setBlock(corner.offset(x, 0, z), state, Block.UPDATE_NONE);
                }
            }
            for (Player player : playersInArea) {
                player.teleportTo(corner.getX() + trunkSize / 2.0, corner.getY(), corner.getZ() + trunkSize / 2.0);
            }
            return false;
        }

        List<BlockPos> branchEnds = deeperdark$generateBranchingCanopy(world, corner, trunkSize, actualHeight, curveAxis, curveStrength, doubleCurve, random);

        deeperdark$generateLeafClustersWithSupport(world, corner, trunkSize, actualHeight, curveAxis, curveStrength, doubleCurve, branchEnds, random);

        BlockState dirt = Blocks.DIRT.defaultBlockState();
        for (int x = 0; x < trunkSize; x++) {
            for (int z = 0; z < trunkSize; z++) {
                world.setBlock(corner.offset(x, -1, z), dirt, Block.UPDATE_ALL);
            }
        }

        return true;
    }

    @Unique
    private int deeperdark$generateCurvedTrunkWithButtress(ServerLevel world, BlockPos corner, int trunkSize, int targetHeight,
                                               Direction.Axis curveAxis, float curveStrength, boolean doubleCurve, RandomSource random) {
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();

        float centerX = corner.getX() + trunkSize / 2.0f;
        float centerZ = corner.getZ() + trunkSize / 2.0f;

        int actualHeight = 0;

        int buttressHeight = Math.max(3, (int)(targetHeight * 0.15f));

        for (int y = 0; y < targetHeight; y++) {
            float progress = (float) y / targetHeight;
            float curveOffset = deeperdark$calculateCurveOffset(progress, curveStrength, doubleCurve);

            float currentCenterX = centerX;
            float currentCenterZ = centerZ;

            if (curveAxis == Direction.Axis.X) {
                currentCenterX += curveOffset;
            } else {
                currentCenterZ += curveOffset;
            }

            float baseRadius = trunkSize / 2.0f;
            float currentRadius;

            if (y < buttressHeight) {
                float buttressProgress = (float) y / buttressHeight;
                float buttressExpansion = 1.0f + (1.0f - buttressProgress) * 0.5f;
                currentRadius = baseRadius * buttressExpansion;
            } else {
                float taperProgress = (float)(y - buttressHeight) / (targetHeight - buttressHeight);
                float radiusMultiplier = 1.0f - (taperProgress * taperProgress * 0.7f);
                currentRadius = baseRadius * radiusMultiplier;
            }

            currentRadius = Math.max(currentRadius, 0.5f);

            boolean placedAny = false;

            int searchRadius = (int) Math.ceil(currentRadius) + 1;
            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                    int blockX = (int) Math.floor(currentCenterX) + dx;
                    int blockZ = (int) Math.floor(currentCenterZ) + dz;

                    float distX = blockX + 0.5f - currentCenterX;
                    float distZ = blockZ + 0.5f - currentCenterZ;
                    float dist = Mth.sqrt(distX * distX + distZ * distZ);

                    if (dist <= currentRadius) {
                        BlockPos logPos = new BlockPos(blockX, corner.getY() + y, blockZ);

                        BlockState existingState = world.getBlockState(logPos);
                        if (existingState.isAir() || existingState.is(Blocks.OAK_LEAVES) ||
                            existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {
                            world.setBlock(logPos, oakLog, Block.UPDATE_ALL);
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

    @Unique
    private float deeperdark$calculateCurveOffset(float progress, float curveStrength, boolean doubleCurve) {
        if (doubleCurve) {
            return curveStrength * (float) Math.sin(progress * Math.PI * 2);
        } else {
            return curveStrength * (float) Math.sin(progress * Math.PI);
        }
    }

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

    @Unique
    private List<BlockPos> deeperdark$generateBranchingCanopy(ServerLevel world, BlockPos corner, int trunkSize, int height,
                                                    Direction.Axis curveAxis, float curveStrength, boolean doubleCurve, RandomSource random) {
        List<BlockPos> branchEnds = new ArrayList<>();

        int branchLevels = trunkSize + 2;

        int startHeight = (int) (height * 0.35f);
        int endHeight = (int) (height * 0.85f);
        int levelSpacing = Math.max(1, (endHeight - startHeight) / branchLevels);

        for (int level = 0; level < branchLevels; level++) {
            int branchY = startHeight + (level * levelSpacing) + random.nextInt(Math.max(1, levelSpacing / 2));
            if (branchY >= height) continue;

            float progress = (float) branchY / height;
            float curveOffset = deeperdark$calculateCurveOffset(progress, curveStrength, doubleCurve);

            float trunkCenterX = corner.getX() + trunkSize / 2.0f;
            float trunkCenterZ = corner.getZ() + trunkSize / 2.0f;

            if (curveAxis == Direction.Axis.X) {
                trunkCenterX += curveOffset;
            } else {
                trunkCenterZ += curveOffset;
            }

            int branchCount = 3 + (trunkSize / 2) + random.nextInt(3);
            float startAngle = random.nextFloat() * (float) Math.PI * 2;

            for (int b = 0; b < branchCount; b++) {
                float angle = startAngle + (b * (float) Math.PI * 2 / branchCount) + (random.nextFloat() - 0.5f) * 0.4f;

                float heightFactor = 1.0f - (progress * 0.4f);
                int baseBranchLength = (int) ((trunkSize * 3 + 5 + random.nextInt(6)) * heightFactor);

                BlockPos mainBranchEnd = deeperdark$generateMainBranch(world,
                    (int) trunkCenterX, corner.getY() + branchY, (int) trunkCenterZ,
                    angle, baseBranchLength, random);

                if (mainBranchEnd != null) {
                    branchEnds.add(mainBranchEnd);

                    if (trunkSize >= 4) {
                        List<BlockPos> subBranchEnds = deeperdark$generateSubBranches(world, mainBranchEnd,
                            angle, baseBranchLength, trunkSize, random);
                        branchEnds.addAll(subBranchEnds);
                    }
                }
            }
        }

        BlockPos topPos = deeperdark$getTrunkTopPosition(corner, trunkSize, height, curveAxis, curveStrength, doubleCurve);
        branchEnds.add(topPos);

        return branchEnds;
    }

    @Unique
    private BlockPos deeperdark$generateMainBranch(ServerLevel world, int startX, int startY, int startZ,
                                           float angle, int length, RandomSource random) {
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();

        float x = startX + 0.5f;
        float y = startY + 0.5f;
        float z = startZ + 0.5f;

        float dx = Mth.cos(angle);
        float dz = Mth.sin(angle);
        float dy = 0.15f + random.nextFloat() * 0.25f;

        float mag = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= mag;
        dy /= mag;
        dz /= mag;

        BlockPos lastPos = null;

        for (int i = 0; i < length; i++) {
            x += dx;
            y += dy;
            z += dz;

            if (i > 3) {
                x += (random.nextFloat() - 0.5f) * 0.2f;
                z += (random.nextFloat() - 0.5f) * 0.2f;
            }

            BlockPos branchPos = new BlockPos((int) x, (int) y, (int) z);
            BlockState existingState = world.getBlockState(branchPos);

            if (existingState.isAir() || existingState.is(Blocks.OAK_LEAVES) ||
                existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {

                Direction.Axis axis = Direction.Axis.Y;
                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
                    axis = Direction.Axis.X;
                } else if (Math.abs(dz) > Math.abs(dy)) {
                    axis = Direction.Axis.Z;
                }

                world.setBlock(branchPos, oakLog.setValue(RotatedPillarBlock.AXIS, axis), Block.UPDATE_ALL);
                lastPos = branchPos;
            }
        }

        return lastPos;
    }

    @Unique
    private List<BlockPos> deeperdark$generateSubBranches(ServerLevel world, BlockPos branchEnd,
                                                          float parentAngle, int parentLength, int trunkSize, RandomSource random) {
        List<BlockPos> subBranchEnds = new ArrayList<>();
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();

        int subBranchCount = 2 + random.nextInt(trunkSize >= 10 ? 4 : 2);

        for (int i = 0; i < subBranchCount; i++) {
            float angleOffset = (random.nextFloat() - 0.5f) * (float) Math.PI * 0.8f;
            float subAngle = parentAngle + angleOffset;

            int subLength = parentLength / 3 + random.nextInt(parentLength / 3 + 1);

            float x = branchEnd.getX() + 0.5f;
            float y = branchEnd.getY() + 0.5f;
            float z = branchEnd.getZ() + 0.5f;

            float dx = Mth.cos(subAngle);
            float dz = Mth.sin(subAngle);
            float dy = 0.1f + random.nextFloat() * 0.3f;

            float mag = Mth.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= mag;
            dy /= mag;
            dz /= mag;

            BlockPos lastPos = branchEnd;

            for (int j = 0; j < subLength; j++) {
                x += dx;
                y += dy;
                z += dz;

                if (j > 1) {
                    x += (random.nextFloat() - 0.5f) * 0.25f;
                    z += (random.nextFloat() - 0.5f) * 0.25f;
                }

                BlockPos subBranchPos = new BlockPos((int) x, (int) y, (int) z);
                BlockState existingState = world.getBlockState(subBranchPos);

                if (existingState.isAir() || existingState.is(Blocks.OAK_LEAVES) ||
                    existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {

                    Direction.Axis axis = Direction.Axis.Y;
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
                        axis = Direction.Axis.X;
                    } else if (Math.abs(dz) > Math.abs(dy)) {
                        axis = Direction.Axis.Z;
                    }

                    world.setBlock(subBranchPos, oakLog.setValue(RotatedPillarBlock.AXIS, axis), Block.UPDATE_ALL);
                    lastPos = subBranchPos;
                }
            }

            subBranchEnds.add(lastPos);
        }

        return subBranchEnds;
    }

    @Unique
    private void deeperdark$generateLeafClustersWithSupport(ServerLevel world, BlockPos corner, int trunkSize, int height,
                                                  Direction.Axis curveAxis, float curveStrength, boolean doubleCurve,
                                                  List<BlockPos> branchEnds, RandomSource random) {
        BlockState oakLeaves = Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.DISTANCE, 1);

        int baseClusterRadius = 3 + (trunkSize / 2);

        for (BlockPos branchEnd : branchEnds) {
            int clusterRadius = baseClusterRadius + random.nextInt(2);

            deeperdark$generateLeafSupportNetwork(world, branchEnd, clusterRadius, random);

            for (int dx = -clusterRadius; dx <= clusterRadius; dx++) {
                for (int dy = -clusterRadius / 2; dy <= clusterRadius; dy++) {
                    for (int dz = -clusterRadius; dz <= clusterRadius; dz++) {
                        float distSq = (dx * dx) + (dy * dy * 2.5f) + (dz * dz);
                        float radiusSq = clusterRadius * clusterRadius;

                        if (distSq <= radiusSq) {
                            if (distSq > radiusSq * 0.7f && random.nextFloat() > 0.6f) {
                                continue;
                            }

                            BlockPos leafPos = branchEnd.offset(dx, dy, dz);
                            BlockState existingState = world.getBlockState(leafPos);

                            if (existingState.isAir() || existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {
                                world.setBlock(leafPos, oakLeaves, Block.UPDATE_ALL);
                            }
                        }
                    }
                }
            }
        }

        BlockPos topPos = deeperdark$getTrunkTopPosition(corner, trunkSize, height, curveAxis, curveStrength, doubleCurve);
        List<BlockPos> crownBranchEnds = deeperdark$generateDiagonalCrownBranches(world, topPos, trunkSize, random);

        int crownRadius = trunkSize + 2;

        deeperdark$generateLeafSupportNetwork(world, topPos, crownRadius, random);

        for (BlockPos crownBranchEnd : crownBranchEnds) {
            int crownClusterRadius = 3 + random.nextInt(2);
            deeperdark$generateLeafSupportNetwork(world, crownBranchEnd, crownClusterRadius, random);

            for (int dx = -crownClusterRadius; dx <= crownClusterRadius; dx++) {
                for (int dy = -crownClusterRadius; dy <= crownClusterRadius; dy++) {
                    for (int dz = -crownClusterRadius; dz <= crownClusterRadius; dz++) {
                        float distSq = (dx * dx) + (dy * dy * 1.5f) + (dz * dz);
                        float radiusSq = crownClusterRadius * crownClusterRadius;

                        if (distSq <= radiusSq && (distSq < radiusSq * 0.6f || random.nextFloat() > 0.4f)) {
                            BlockPos leafPos = crownBranchEnd.offset(dx, dy, dz);
                            BlockState existingState = world.getBlockState(leafPos);
                            if (existingState.isAir() || existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {
                                world.setBlock(leafPos, oakLeaves, Block.UPDATE_ALL);
                            }
                        }
                    }
                }
            }
        }

        int crownHeightUp = crownRadius + trunkSize / 2;
        for (int dx = -crownRadius; dx <= crownRadius; dx++) {
            for (int dy = -2; dy <= crownHeightUp; dy++) {
                for (int dz = -crownRadius; dz <= crownRadius; dz++) {
                    float heightFactor = dy > 0 ? 1.0f - ((float)dy / crownHeightUp) * 0.6f : 1.0f;
                    float effectiveRadius = crownRadius * heightFactor;

                    float dist = Mth.sqrt(dx * dx + dz * dz);
                    if (dist <= effectiveRadius && (dist < effectiveRadius - 0.5f || random.nextFloat() < 0.5f)) {
                        BlockPos leafPos = topPos.offset(dx, dy, dz);
                        BlockState existingState = world.getBlockState(leafPos);
                        if (existingState.isAir() || existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {
                            world.setBlock(leafPos, oakLeaves, Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }

    @Unique
    private void deeperdark$generateLeafSupportNetwork(ServerLevel world, BlockPos center, int radius, RandomSource random) {
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();

        int interiorRadius = Math.max(1, radius - 2);

        for (int dy = -1; dy <= 1; dy++) {
            BlockPos logPos = center.offset(0, dy, 0);
            BlockState existingState = world.getBlockState(logPos);
            if (existingState.isAir() || existingState.is(Blocks.OAK_LEAVES) ||
                existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {
                world.setBlock(logPos, oakLog, Block.UPDATE_ALL);
            }
        }

        float[] angles = {0, (float)(Math.PI * 0.5), (float)Math.PI, (float)(Math.PI * 1.5)};

        for (float angle : angles) {
            float dx = Mth.cos(angle);
            float dz = Mth.sin(angle);

            for (int dist = 3; dist <= interiorRadius; dist += 3) {
                int x = (int)(dx * dist);
                int z = (int)(dz * dist);

                BlockPos logPos = center.offset(x, 0, z);
                BlockState existingState = world.getBlockState(logPos);

                if (existingState.isAir() || existingState.is(Blocks.OAK_LEAVES) ||
                    existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {

                    Direction.Axis axis = Direction.Axis.Y;
                    if (Math.abs(dx) > Math.abs(dz)) {
                        axis = Direction.Axis.X;
                    } else if (Math.abs(dz) > 0.1f) {
                        axis = Direction.Axis.Z;
                    }

                    world.setBlock(logPos, oakLog.setValue(RotatedPillarBlock.AXIS, axis), Block.UPDATE_ALL);
                }
            }
        }

        if (radius > 4) {
            int extraSupports = radius / 2;
            for (int i = 0; i < extraSupports; i++) {
                int x = random.nextInt(interiorRadius * 2 + 1) - interiorRadius;
                int z = random.nextInt(interiorRadius * 2 + 1) - interiorRadius;
                int y = random.nextInt(3) - 1;
                if (x * x + z * z <= interiorRadius * interiorRadius * 0.6f) {
                    BlockPos supportPos = center.offset(x, y, z);
                    if (world.getBlockState(supportPos).isAir() || world.getBlockState(supportPos).is(Blocks.OAK_LEAVES)) {
                        world.setBlock(supportPos, oakLog, Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    @Unique
    private List<BlockPos> deeperdark$generateDiagonalCrownBranches(ServerLevel world, BlockPos topPos, int trunkSize, RandomSource random) {
        List<BlockPos> branchEnds = new ArrayList<>();
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();

        int tierCount = 2 + (trunkSize / 3);
        int baseHeight = topPos.getY();

        for (int tier = 0; tier < tierCount; tier++) {
            int tierHeight = baseHeight - (int)((tier + 1) * trunkSize * 0.8f);

            int branchCount = 4 + random.nextInt(3);
            float startAngle = random.nextFloat() * (float)Math.PI * 2;

            int branchLength = trunkSize + 4 + random.nextInt(4) + (tierCount - tier);

            for (int b = 0; b < branchCount; b++) {
                float angle = startAngle + (b * (float)Math.PI * 2 / branchCount) + (random.nextFloat() - 0.5f) * 0.4f;

                float x = topPos.getX() + 0.5f;
                float y = tierHeight + 0.5f;
                float z = topPos.getZ() + 0.5f;

                float dx = Mth.cos(angle);
                float dz = Mth.sin(angle);
                float dy = 0.4f + random.nextFloat() * 0.3f + (tier * 0.1f);

                float mag = Mth.sqrt(dx * dx + dy * dy + dz * dz);
                dx /= mag;
                dy /= mag;
                dz /= mag;

                BlockPos lastPos = new BlockPos((int)x, tierHeight, (int)z);

                for (int i = 0; i < branchLength; i++) {
                    x += dx;
                    y += dy;
                    z += dz;

                    if (i > 2) {
                        x += (random.nextFloat() - 0.5f) * 0.15f;
                        z += (random.nextFloat() - 0.5f) * 0.15f;
                    }

                    BlockPos branchPos = new BlockPos((int)x, (int)y, (int)z);
                    BlockState existingState = world.getBlockState(branchPos);

                    if (existingState.isAir() || existingState.is(Blocks.OAK_LEAVES) ||
                        existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {

                        Direction.Axis axis = Direction.Axis.Y;
                        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
                            axis = Direction.Axis.X;
                        } else if (Math.abs(dz) > Math.abs(dy)) {
                            axis = Direction.Axis.Z;
                        }

                        world.setBlock(branchPos, oakLog.setValue(RotatedPillarBlock.AXIS, axis), Block.UPDATE_ALL);
                        lastPos = branchPos;
                    }
                }

                branchEnds.add(lastPos);
            }
        }

        int peakBranches = 3 + random.nextInt(2);
        float peakStartAngle = random.nextFloat() * (float)Math.PI * 2;

        for (int b = 0; b < peakBranches; b++) {
            float angle = peakStartAngle + (b * (float)Math.PI * 2 / peakBranches);
            float x = topPos.getX() + 0.5f;
            float y = topPos.getY() + 0.5f;
            float z = topPos.getZ() + 0.5f;

            float dx = Mth.cos(angle) * 0.3f;
            float dz = Mth.sin(angle) * 0.3f;
            float dy = 0.9f;

            int peakLength = 3 + random.nextInt(3);
            BlockPos lastPos = topPos;

            for (int i = 0; i < peakLength; i++) {
                x += dx;
                y += dy;
                z += dz;

                BlockPos branchPos = new BlockPos((int)x, (int)y, (int)z);
                BlockState existingState = world.getBlockState(branchPos);

                if (existingState.isAir() || existingState.is(Blocks.OAK_LEAVES) ||
                    existingState.is(net.minecraft.tags.BlockTags.REPLACEABLE_BY_TREES)) {
                    world.setBlock(branchPos, oakLog, Block.UPDATE_ALL);
                    lastPos = branchPos;
                }
            }

            branchEnds.add(lastPos);
        }

        return branchEnds;
    }

    @Unique
    private boolean deeperdark$generateGiantFancyOak(ServerLevel world, BlockPos corner, BlockState state, RandomSource random) {
        List<Player> playersToTeleport = new ArrayList<>();
        AABB saplingArea = new AABB(
            corner.getX(), corner.getY(), corner.getZ(),
            corner.getX() + 2, corner.getY() + 2, corner.getZ() + 2
        );
        playersToTeleport.addAll(world.getEntitiesOfClass(Player.class, saplingArea, p -> true));

        Holder<ConfiguredFeature<?, ?>> fancyOakFeature = world.registryAccess()
            .lookupOrThrow(Registries.CONFIGURED_FEATURE)
            .get(TreeFeatures.FANCY_OAK)
            .orElse(null);

        if (fancyOakFeature == null) {
            return false;
        }

        ChunkGenerator chunkGenerator = world.getChunkSource().getGenerator();

        BlockState air = Blocks.AIR.defaultBlockState();
        world.setBlock(corner, air, Block.UPDATE_NONE);
        world.setBlock(corner.east(), air, Block.UPDATE_NONE);
        world.setBlock(corner.south(), air, Block.UPDATE_NONE);
        world.setBlock(corner.south().east(), air, Block.UPDATE_NONE);

        BlockPos treePos = corner;

        if (fancyOakFeature.value().place(world, chunkGenerator, random, treePos)) {
            int treeHeight = deeperdark$findTreeHeight(world, corner);

            deeperdark$fill2x2TrunkBase(world, corner, treeHeight);

            if (!playersToTeleport.isEmpty() && treeHeight > 0) {
                BlockPos topPos = corner.above(treeHeight + 1);
                for (Player player : playersToTeleport) {
                    player.teleportTo(topPos.getX() + 0.5, topPos.getY(), topPos.getZ() + 0.5);
                    Deeperdark.LOGGER.info("[Deeper Dark] Teleported player {} to top of growing tree", player.getName().getString());
                }
            }

            return true;
        }

        world.setBlock(corner, state, Block.UPDATE_NONE);
        world.setBlock(corner.east(), state, Block.UPDATE_NONE);
        world.setBlock(corner.south(), state, Block.UPDATE_NONE);
        world.setBlock(corner.south().east(), state, Block.UPDATE_NONE);
        return false;
    }

    @Unique
    private int deeperdark$findTreeHeight(ServerLevel world, BlockPos base) {
        int height = 0;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int y = 0; y < 40; y++) {
            mutable.set(base.getX(), base.getY() + y, base.getZ());
            if (world.getBlockState(mutable).is(Blocks.OAK_LOG)) {
                height = y;
            }
        }
        return height;
    }

    @Unique
    private void deeperdark$fill2x2TrunkBase(ServerLevel world, BlockPos corner, int height) {
        int trunkHeight = Math.min(height / 2 + 2, height);
        BlockState oakLog = Blocks.OAK_LOG.defaultBlockState();

        for (int y = 0; y < trunkHeight; y++) {
            BlockPos pos = corner.above(y);
            if (!world.getBlockState(pos).is(Blocks.OAK_LOG)) {
                world.setBlock(pos, oakLog, Block.UPDATE_ALL);
            }
            if (!world.getBlockState(pos.east()).is(Blocks.OAK_LOG)) {
                world.setBlock(pos.east(), oakLog, Block.UPDATE_ALL);
            }
            if (!world.getBlockState(pos.south()).is(Blocks.OAK_LOG)) {
                world.setBlock(pos.south(), oakLog, Block.UPDATE_ALL);
            }
            if (!world.getBlockState(pos.south().east()).is(Blocks.OAK_LOG)) {
                world.setBlock(pos.south().east(), oakLog, Block.UPDATE_ALL);
            }
        }

        BlockState dirt = Blocks.DIRT.defaultBlockState();
        world.setBlock(corner.below(), dirt, Block.UPDATE_ALL);
        world.setBlock(corner.east().below(), dirt, Block.UPDATE_ALL);
        world.setBlock(corner.south().below(), dirt, Block.UPDATE_ALL);
        world.setBlock(corner.south().east().below(), dirt, Block.UPDATE_ALL);
    }

    @Unique
    private void deeperdark$teleportPlayersAfterGrowth(ServerLevel world, BlockPos pos) {
        AABB saplingArea = new AABB(pos).inflate(0.5, 1, 0.5);
        List<Player> playersToTeleport = new ArrayList<>(
            world.getEntitiesOfClass(Player.class, saplingArea, p -> true)
        );

        if (playersToTeleport.isEmpty()) {
            return;
        }

        var server = world.getServer();
        if (server != null) {
            server.execute(() -> {
                BlockState newState = world.getBlockState(pos);
                if (newState.is(Blocks.OAK_LOG) || newState.is(Blocks.BIRCH_LOG) ||
                    newState.is(Blocks.SPRUCE_LOG) || newState.is(Blocks.JUNGLE_LOG) ||
                    newState.is(Blocks.ACACIA_LOG) || newState.is(Blocks.DARK_OAK_LOG) ||
                    newState.is(Blocks.CHERRY_LOG) || newState.is(Blocks.MANGROVE_LOG) ||
                    newState.is(Blocks.PALE_OAK_LOG)) {

                    int treeHeight = 0;
                    for (int y = 0; y < 40; y++) {
                        if (world.getBlockState(pos.above(y)).is(net.minecraft.tags.BlockTags.LOGS)) {
                            treeHeight = y;
                        }
                    }

                    if (treeHeight > 0) {
                        BlockPos topPos = pos.above(treeHeight + 1);
                        for (Player player : playersToTeleport) {
                            if (player.isAlive()) {
                                player.teleportTo(topPos.getX() + 0.5, topPos.getY(), topPos.getZ() + 0.5);
                                Deeperdark.LOGGER.info("[Deeper Dark] Teleported player {} to top of growing tree", player.getName().getString());
                            }
                        }
                    }
                }
            });
        }
    }
}
