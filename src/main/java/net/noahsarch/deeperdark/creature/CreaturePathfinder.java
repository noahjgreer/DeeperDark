package net.noahsarch.deeperdark.creature;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.noahsarch.deeperdark.Deeperdark;

import java.util.*;

/**
 * Handles pathfinding algorithms for creature spawning and movement.
 * Implements both the pathtracing algorithm (primary) and radial placement algorithm (fallback).
 */
public class CreaturePathfinder {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("CreaturePathfinder");
    private static final Direction[] HORIZONTAL_DIRS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    /**
     * Result from a pathfinding operation.
     */
    public record PathResult(Vec3d position, List<BlockPos> path, boolean success, String failureReason) {
        public static PathResult failure(String reason) {
            return new PathResult(null, List.of(), false, reason);
        }
        public static PathResult success(Vec3d position, List<BlockPos> path) {
            return new PathResult(position, path, true, null);
        }
    }

    /**
     * Checks if a position meets the creature's placement criteria.
     * - 3 blocks of vertical space
     * - Not in the open (adjacent to at least one wall)
     * - Below the configured max Y
     */
    public static boolean isValidPlacement(ServerWorld world, BlockPos pos, int maxY, boolean debug) {
        if (pos.getY() > maxY) {
            if (debug) LOGGER.info("[Creature] Position {} above max Y {}", pos, maxY);
            return false;
        }

        // Needs 3 blocks of air vertically (no solid blocks, no fluids)
        for (int dy = 0; dy < 3; dy++) {
            BlockPos check = pos.up(dy);
            BlockState state = world.getBlockState(check);
            if (!state.isAir()) {
                if (debug) LOGGER.info("[Creature] Position {} blocked at y+{} by {}", pos, dy, state.getBlock());
                return false;
            }
        }

        // Must have solid ground below
        BlockPos below = pos.down();
        if (!world.getBlockState(below).isSolidBlock(world, below)) {
            if (debug) LOGGER.info("[Creature] Position {} has no solid floor", pos);
            return false;
        }

        // Must have a ceiling at exactly 3 blocks (preferred height)
        BlockPos ceiling = pos.up(3);
        boolean hasCeiling = world.getBlockState(ceiling).isSolidBlock(world, ceiling);

        // Must be adjacent to at least one solid block (wall/corner) - not standing in the open
        int adjacentWalls = 0;
        for (Direction dir : HORIZONTAL_DIRS) {
            BlockPos adjacent = pos.offset(dir);
            if (world.getBlockState(adjacent).isSolidBlock(world, adjacent)) {
                adjacentWalls++;
            }
        }

        if (adjacentWalls == 0) {
            if (debug) LOGGER.info("[Creature] Position {} has no adjacent walls (too exposed)", pos);
            return false;
        }

        return true;
    }

    /**
     * Scores a placement position for the creature based on preferences.
     * Higher score = better position.
     */
    public static int scorePlacement(ServerWorld world, BlockPos pos) {
        int score = 0;

        // Prefer positions with ceiling at exactly 3 blocks
        BlockPos ceiling = pos.up(3);
        if (world.getBlockState(ceiling).isSolidBlock(world, ceiling)) {
            score += 10;
        }

        // Count adjacent walls - more walls = more hidden
        int adjacentWalls = 0;
        for (Direction dir : HORIZONTAL_DIRS) {
            BlockPos adjacent = pos.offset(dir);
            if (world.getBlockState(adjacent).isSolidBlock(world, adjacent)) {
                adjacentWalls++;
            }
        }
        score += adjacentWalls * 5;

        // Bonus for corner positions (2+ adjacent walls)
        if (adjacentWalls >= 2) {
            score += 15;
        }

        // Bonus for darker areas (no sky access)
        if (!world.isSkyVisible(pos)) {
            score += 5;
        }

        // Slight randomness to prevent predictability
        score += world.getRandom().nextInt(5);

        return score;
    }

    /**
     * Primary pathtracing algorithm.
     * Crawls through the cave system starting from the player's position,
     * then places the creature between min and max crawl distance, favoring
     * positions that meet the creature's preferences.
     */
    public static PathResult pathtraceFromPlayer(ServerWorld world, BlockPos playerPos,
                                                  int minDist, int maxDist, int maxY,
                                                  List<Vec3d> existingCreaturePositions, int entitySpacing,
                                                  boolean debug) {
        if (debug) LOGGER.info("[Creature] Starting pathtrace from player at {}", playerPos);

        // BFS crawl through connected air blocks
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, Integer> distanceMap = new HashMap<>();
        List<BlockPos> path = new ArrayList<>();

        // Find a valid starting position for the crawl (player's feet position)
        BlockPos startPos = findNearestAirAboveSolid(world, playerPos);
        if (startPos == null) {
            if (debug) LOGGER.info("[Creature] Could not find valid starting position near player");
            return PathResult.failure("No valid starting position near player");
        }

        queue.add(startPos);
        visited.add(startPos);
        distanceMap.put(startPos, 0);

        // Candidate positions between min and max distance
        List<BlockPos> candidates = new ArrayList<>();
        int maxDistReached = 0;

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            int dist = distanceMap.get(current);

            if (dist > maxDist) continue;
            if (dist > maxDistReached) maxDistReached = dist;

            // If within the valid range, check if this is a suitable placement position
            if (dist >= minDist && dist <= maxDist) {
                if (isValidPlacement(world, current, maxY, false)) {
                    // Check entity spacing
                    if (isSpacingValid(current, existingCreaturePositions, entitySpacing)) {
                        candidates.add(current);
                    }
                }
            }

            // Explore neighbors (horizontal + up/down through air)
            for (Direction dir : HORIZONTAL_DIRS) {
                exploreNeighbor(world, current, dir, queue, visited, distanceMap, dist + 1, maxDist, maxY);
            }
            // Also crawl up and down through air
            exploreVertical(world, current, queue, visited, distanceMap, dist + 1, maxDist, maxY);
        }

        if (debug) LOGGER.info("[Creature] Pathtrace completed: {} candidates found, max distance reached: {}", candidates.size(), maxDistReached);

        if (candidates.isEmpty()) {
            String reason = maxDistReached < minDist
                ? "Space too compact (max reach: " + maxDistReached + ", needed: " + minDist + ")"
                : "No valid placement positions found in range";
            if (debug) LOGGER.info("[Creature] Pathtrace failed: {}", reason);
            return PathResult.failure(reason);
        }

        // Score all candidates and pick the best one with weighted randomness
        BlockPos bestPos = selectWeightedCandidate(world, candidates);
        if (debug) LOGGER.info("[Creature] Selected position: {} (from {} candidates)", bestPos, candidates.size());

        // Build the actual path from player to creature for copper trail
        List<BlockPos> trailPath = buildPathBFS(world, startPos, bestPos, maxY);

        return PathResult.success(Vec3d.ofBottomCenter(bestPos), trailPath);
    }

    /**
     * Fallback radial placement algorithm.
     * Casts a sphere from the player's position searching for nearby cave systems.
     */
    public static PathResult radialPlacement(ServerWorld world, BlockPos playerPos,
                                              int minDist, int maxDist, int maxY,
                                              List<Vec3d> existingCreaturePositions, int entitySpacing,
                                              boolean debug) {
        if (debug) LOGGER.info("[Creature] Starting radial placement from {}", playerPos);

        Random rand = new Random();
        List<BlockPos> candidates = new ArrayList<>();

        // Sample random points within the sphere
        int sampleAttempts = 500;
        for (int i = 0; i < sampleAttempts; i++) {
            // Random point in spherical shell between minDist and maxDist
            double distance = minDist + rand.nextDouble() * (maxDist - minDist);
            double theta = rand.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * rand.nextDouble() - 1);

            int dx = (int) (distance * Math.sin(phi) * Math.cos(theta));
            int dy = (int) (distance * Math.cos(phi));
            int dz = (int) (distance * Math.sin(phi) * Math.sin(theta));

            BlockPos candidate = playerPos.add(dx, dy, dz);

            // Skip if above max Y
            if (candidate.getY() > maxY) continue;
            if (candidate.getY() < world.getBottomY()) continue;

            // Check if this is a valid placement
            if (isValidPlacement(world, candidate, maxY, false)) {
                if (isSpacingValid(candidate, existingCreaturePositions, entitySpacing)) {
                    candidates.add(candidate);
                }
            }
        }

        if (debug) LOGGER.info("[Creature] Radial placement: {} candidates found from {} samples", candidates.size(), sampleAttempts);

        if (candidates.isEmpty()) {
            if (debug) LOGGER.info("[Creature] Radial placement failed: no valid positions found");
            return PathResult.failure("Radial placement found no valid positions");
        }

        // Pick best candidate
        BlockPos bestPos = selectWeightedCandidate(world, candidates);

        // Build path for copper trail
        List<BlockPos> trailPath = buildPathBFS(world, findNearestAirAboveSolid(world, playerPos), bestPos, maxY);

        if (debug) LOGGER.info("[Creature] Radial placement selected: {}", bestPos);
        return PathResult.success(Vec3d.ofBottomCenter(bestPos), trailPath);
    }

    /**
     * Finds a path between two positions for the creature's chase pathfinding.
     * Returns a list of positions the creature should walk through.
     */
    public static List<BlockPos> findChasePath(ServerWorld world, BlockPos from, BlockPos to, int maxY) {
        return buildPathBFS(world, from, to, maxY);
    }

    /**
     * Finds a chase placement position within the specified distance range.
     */
    public static BlockPos findChasePosition(ServerWorld world, BlockPos playerPos, int minDist, int maxDist, int maxY, boolean debug) {
        Random rand = new Random();
        List<BlockPos> candidates = new ArrayList<>();

        int sampleAttempts = 300;
        for (int i = 0; i < sampleAttempts; i++) {
            double distance = minDist + rand.nextDouble() * (maxDist - minDist);
            double theta = rand.nextDouble() * 2 * Math.PI;

            int dx = (int) (distance * Math.cos(theta));
            int dz = (int) (distance * Math.sin(theta));

            // Search for valid Y level
            for (int dy = -10; dy <= 10; dy++) {
                BlockPos candidate = playerPos.add(dx, dy, dz);
                if (candidate.getY() > maxY || candidate.getY() < world.getBottomY()) continue;

                if (isValidPlacement(world, candidate, maxY, false)) {
                    candidates.add(candidate);
                    break;
                }
            }
        }

        if (candidates.isEmpty()) {
            if (debug) LOGGER.info("[Creature] Chase position search failed, returning null");
            return null;
        }

        return candidates.get(rand.nextInt(candidates.size()));
    }

    // ===== Private Helpers =====

    private static void exploreNeighbor(ServerWorld world, BlockPos current, Direction dir,
                                         Queue<BlockPos> queue, Set<BlockPos> visited,
                                         Map<BlockPos, Integer> distanceMap, int newDist,
                                         int maxDist, int maxY) {
        BlockPos next = current.offset(dir);
        if (visited.contains(next) || next.getY() > maxY) return;
        if (newDist > maxDist) return;

        // The neighbor must be passable (air-like)
        BlockState state = world.getBlockState(next);
        if (!state.isAir()) {
            // Try stepping up or down
            BlockPos stepUp = next.up();
            BlockPos stepDown = next.down();

            if (!visited.contains(stepUp) && world.getBlockState(stepUp).isAir()
                    && world.getBlockState(stepUp.up()).isAir() && stepUp.getY() <= maxY) {
                visited.add(stepUp);
                distanceMap.put(stepUp, newDist);
                queue.add(stepUp);
            }
            if (!visited.contains(stepDown) && world.getBlockState(stepDown).isAir()
                    && world.getBlockState(stepDown.up()).isAir()
                    && world.getBlockState(stepDown.down()).isSolidBlock(world, stepDown.down())) {
                visited.add(stepDown);
                distanceMap.put(stepDown, newDist);
                queue.add(stepDown);
            }
            return;
        }

        // Check that we can stand here (solid block below, or at least air going down to solid)
        BlockPos below = next.down();
        if (!world.getBlockState(below).isSolidBlock(world, below)) {
            // Check if we can stand after dropping 1-2 blocks
            BlockPos dropOne = next.down(2);
            if (world.getBlockState(next.down()).isAir() && world.getBlockState(dropOne).isSolidBlock(world, dropOne)) {
                BlockPos landPos = next.down();
                if (!visited.contains(landPos)) {
                    visited.add(landPos);
                    distanceMap.put(landPos, newDist);
                    queue.add(landPos);
                }
                return;
            }
            // Can't traverse through unstable ground
            return;
        }

        visited.add(next);
        distanceMap.put(next, newDist);
        queue.add(next);
    }

    private static void exploreVertical(ServerWorld world, BlockPos current,
                                         Queue<BlockPos> queue, Set<BlockPos> visited,
                                         Map<BlockPos, Integer> distanceMap, int newDist,
                                         int maxDist, int maxY) {
        // Check going up (jumping/climbing)
        BlockPos up = current.up(2);
        if (!visited.contains(up) && world.getBlockState(up).isAir()
                && world.getBlockState(up.up()).isAir() && up.getY() <= maxY
                && newDist <= maxDist) {
            // Can jump up if there's a solid block to stand on
            BlockPos upBelow = up.down();
            if (world.getBlockState(upBelow).isSolidBlock(world, upBelow) ||
                    world.getBlockState(current.up()).isSolidBlock(world, current.up())) {
                visited.add(up);
                distanceMap.put(up, newDist);
                queue.add(up);
            }
        }

        // Check going down (falling)
        BlockPos down = current.down(2);
        if (!visited.contains(down) && down.getY() >= world.getBottomY()
                && world.getBlockState(down).isAir()
                && world.getBlockState(down.down()).isSolidBlock(world, down.down())
                && newDist <= maxDist) {
            visited.add(down);
            distanceMap.put(down, newDist);
            queue.add(down);
        }
    }

    private static BlockPos findNearestAirAboveSolid(ServerWorld world, BlockPos pos) {
        // Search for the nearest valid standing position
        for (int dy = -3; dy <= 3; dy++) {
            BlockPos check = pos.add(0, dy, 0);
            if (world.getBlockState(check).isAir()
                    && world.getBlockState(check.down()).isSolidBlock(world, check.down())) {
                return check;
            }
        }
        return pos;
    }

    private static boolean isSpacingValid(BlockPos pos, List<Vec3d> existingPositions, int minSpacing) {
        Vec3d posVec = Vec3d.ofBottomCenter(pos);
        for (Vec3d existing : existingPositions) {
            if (posVec.distanceTo(existing) < minSpacing) {
                return false;
            }
        }
        return true;
    }

    /**
     * Selects a candidate with weighted randomness based on placement score.
     */
    private static BlockPos selectWeightedCandidate(ServerWorld world, List<BlockPos> candidates) {
        if (candidates.size() == 1) return candidates.getFirst();

        // Score all candidates
        int[] scores = new int[candidates.size()];
        int totalScore = 0;
        for (int i = 0; i < candidates.size(); i++) {
            scores[i] = Math.max(1, scorePlacement(world, candidates.get(i)));
            totalScore += scores[i];
        }

        // Weighted random selection
        int roll = world.getRandom().nextInt(totalScore);
        int accumulated = 0;
        for (int i = 0; i < candidates.size(); i++) {
            accumulated += scores[i];
            if (roll < accumulated) {
                return candidates.get(i);
            }
        }

        return candidates.getLast();
    }

    /**
     * BFS pathfinding between two positions, returning a walkable path.
     * Used for copper nugget trail placement and chase movement.
     */
    private static List<BlockPos> buildPathBFS(ServerWorld world, BlockPos from, BlockPos to, int maxY) {
        if (from == null || to == null) return List.of();

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        Map<BlockPos, BlockPos> parentMap = new HashMap<>();

        queue.add(from);
        visited.add(from);

        int maxSearchDist = (int)(from.getManhattanDistance(to) * 2.5) + 50;
        int searched = 0;

        while (!queue.isEmpty() && searched < maxSearchDist * 10) {
            BlockPos current = queue.poll();
            searched++;

            if (current.equals(to) || current.isWithinDistance(to, 2)) {
                // Reconstruct path
                List<BlockPos> path = new ArrayList<>();
                BlockPos node = current;
                while (node != null && !node.equals(from)) {
                    path.add(node);
                    node = parentMap.get(node);
                }
                path.add(from);
                Collections.reverse(path);
                return path;
            }

            // Explore neighbors
            for (Direction dir : HORIZONTAL_DIRS) {
                BlockPos next = current.offset(dir);
                if (visited.contains(next) || next.getY() > maxY) continue;

                BlockState state = world.getBlockState(next);
                if (state.isAir() && world.getBlockState(next.down()).isSolidBlock(world, next.down())) {
                    visited.add(next);
                    parentMap.put(next, current);
                    queue.add(next);
                    continue;
                }

                // Step up
                BlockPos up = next.up();
                if (!visited.contains(up) && world.getBlockState(up).isAir()
                        && world.getBlockState(up.up()).isAir() && up.getY() <= maxY) {
                    visited.add(up);
                    parentMap.put(up, current);
                    queue.add(up);
                }

                // Step down
                BlockPos down = next.down();
                if (!visited.contains(down) && world.getBlockState(down).isAir()
                        && world.getBlockState(down.down()).isSolidBlock(world, down.down())) {
                    visited.add(down);
                    parentMap.put(down, current);
                    queue.add(down);
                }
            }
        }

        // Path not found — return empty
        return List.of();
    }
}
