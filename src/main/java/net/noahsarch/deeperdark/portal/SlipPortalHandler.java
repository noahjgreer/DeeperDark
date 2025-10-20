package net.noahsarch.deeperdark.portal;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the Slip dimension portal mechanic.
 * Players enter The Slip by:
 * 1. Being in the End dimension
 * 2. Standing in a cauldron filled with water (level 3)
 * 3. Having a closed trapdoor above their head
 */
public class SlipPortalHandler {
    private static final RegistryKey<World> THE_END = World.END;
    private static final RegistryKey<World> THE_SLIP = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("minecraft", "the_slip"));

    // Cooldown to prevent rapid teleportation (100 ticks = 5 seconds)
    private static final int TELEPORT_COOLDOWN = 100;

    // Track when each player last used a portal
    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();

    // Search radius for finding suitable portal location
    private static final int SEARCH_RADIUS = 16;
    private static final int VERTICAL_SEARCH_RANGE = 128;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Only check players in the End dimension
                if (!player.getWorld().getRegistryKey().equals(THE_END)) {
                    continue;
                }

                // Check if player meets portal conditions
                if (isPlayerInSlipPortal(player)) {
                    // Check cooldown
                    long currentTime = player.getWorld().getTime();
                    UUID playerId = player.getUuid();

                    if (playerCooldowns.containsKey(playerId)) {
                        long lastTeleport = playerCooldowns.get(playerId);
                        if (currentTime - lastTeleport < TELEPORT_COOLDOWN) {
                            continue; // Still on cooldown
                        }
                    }

                    // Teleport and update cooldown
                    if (teleportToSlip(player)) {
                        playerCooldowns.put(playerId, currentTime);
                    }
                }
            }
        });
    }

    /**
     * Checks if the player is in a valid Slip portal configuration:
     * - Standing in a water-filled cauldron (level 3)
     * - Closed trapdoor directly above the cauldron
     */
    private static boolean isPlayerInSlipPortal(ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        BlockState cauldronState = player.getWorld().getBlockState(playerPos);

        // Check if player is standing in a water cauldron at max level (3)
        if (!cauldronState.isOf(Blocks.WATER_CAULDRON)) {
            return false;
        }

        int waterLevel = cauldronState.get(LeveledCauldronBlock.LEVEL);
        if (waterLevel != 3) {
            return false;
        }

        // Check for closed trapdoor directly above the cauldron
        BlockPos trapdoorPos = playerPos.up();
        BlockState trapdoorState = player.getWorld().getBlockState(trapdoorPos);

        if (!(trapdoorState.getBlock() instanceof TrapdoorBlock)) {
            return false;
        }

        // Check if trapdoor is closed (open = false) and on bottom half
        boolean isOpen = trapdoorState.get(TrapdoorBlock.OPEN);
        BlockHalf half = trapdoorState.get(TrapdoorBlock.HALF);

        // Trapdoor should be closed and on the bottom half (sealing the player in)
        return !isOpen && half == BlockHalf.BOTTOM;
    }

    /**
     * Teleports the player to The Slip dimension and creates a portal structure
     * @return true if teleport was successful
     */
    private static boolean teleportToSlip(ServerPlayerEntity player) {
        if (player.getServer() == null) {
            return false;
        }

        ServerWorld slipWorld = player.getServer().getWorld(THE_SLIP);

        if (slipWorld == null) {
            return false;
        }

        // Get player's current position to use as a reference for destination
        BlockPos sourcePos = player.getBlockPos();

        // Find or create a suitable portal location in The Slip
        BlockPos targetPos = findOrCreatePortalLocation(slipWorld, sourcePos);

        if (targetPos == null) {
            return false;
        }

        // Play portal sound effect in source dimension
        player.getWorld().playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.BLOCK_PORTAL_TRAVEL,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F
        );

        // Create teleport target (player spawns inside the cauldron)
        Vec3d targetVec = new Vec3d(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        TeleportTarget target = new TeleportTarget(
            slipWorld,
            targetVec,
            Vec3d.ZERO, // velocity
            player.getYaw(),
            player.getPitch(),
            TeleportTarget.NO_OP // post-teleport action
        );

        // Teleport the player
        player.teleportTo(target);

        // Play sound in destination world
        slipWorld.playSound(
            null,
            targetPos,
            SoundEvents.BLOCK_PORTAL_TRAVEL,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F
        );

        return true;
    }

    /**
     * Finds an existing portal or creates a new one at a suitable location.
     * Similar to Nether portal logic, but looks for a surface to place the portal on.
     */
    private static BlockPos findOrCreatePortalLocation(ServerWorld world, BlockPos sourcePos) {
        // Scale the coordinates (you can adjust this based on your dimension scaling)
        BlockPos searchCenter = new BlockPos(sourcePos.getX(), 64, sourcePos.getZ());

        // First, try to find an existing portal nearby
        BlockPos existingPortal = findExistingPortal(world, searchCenter);
        if (existingPortal != null) {
            return existingPortal;
        }

        // If no existing portal, find a suitable surface and create one
        BlockPos surfacePos = findSuitableSurface(world, searchCenter);
        if (surfacePos != null) {
            createPortalStructure(world, surfacePos);
            return surfacePos;
        }

        // Fallback: create portal at the search center
        createPortalStructure(world, searchCenter);
        return searchCenter;
    }

    /**
     * Searches for an existing portal (water cauldron with trapdoor) nearby
     */
    private static BlockPos findExistingPortal(ServerWorld world, BlockPos center) {
        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                for (int y = -VERTICAL_SEARCH_RANGE / 2; y <= VERTICAL_SEARCH_RANGE / 2; y++) {
                    BlockPos checkPos = center.add(x, y, z);

                    // Check if this is a valid portal structure
                    if (isValidPortalStructure(world, checkPos)) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks if a position has a valid portal structure (water cauldron + trapdoor)
     */
    private static boolean isValidPortalStructure(ServerWorld world, BlockPos pos) {
        BlockState cauldronState = world.getBlockState(pos);
        BlockState trapdoorState = world.getBlockState(pos.up());

        return cauldronState.isOf(Blocks.WATER_CAULDRON) &&
               cauldronState.get(LeveledCauldronBlock.LEVEL) == 3 &&
               trapdoorState.getBlock() instanceof TrapdoorBlock;
    }

    /**
     * Finds a suitable surface to place the portal on.
     * Looks for a solid block with air above it.
     */
    private static BlockPos findSuitableSurface(ServerWorld world, BlockPos center) {
        // Search in a spiral pattern outward
        for (int radius = 0; radius <= SEARCH_RADIUS; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    // Only check the outer ring of this radius
                    if (Math.abs(x) != radius && Math.abs(z) != radius) {
                        continue;
                    }

                    // Search vertically for a suitable surface
                    for (int yOffset = -VERTICAL_SEARCH_RANGE / 2; yOffset <= VERTICAL_SEARCH_RANGE / 2; yOffset++) {
                        BlockPos checkPos = center.add(x, yOffset, z);

                        if (isSuitablePortalLocation(world, checkPos)) {
                            return checkPos.up(); // Return position above the solid block
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Checks if a location is suitable for portal placement:
     * - Has a solid block below
     * - Has at least 2 blocks of air above for cauldron and trapdoor
     */
    private static boolean isSuitablePortalLocation(ServerWorld world, BlockPos pos) {
        BlockPos below = pos.down();
        BlockPos above = pos.up();
        BlockPos twoAbove = pos.up(2);

        return world.getBlockState(below).isSolidBlock(world, below) &&
               world.getBlockState(pos).isAir() &&
               world.getBlockState(above).isAir() &&
               world.getBlockState(twoAbove).isAir();
    }

    /**
     * Creates the portal structure at the given position:
     * - Water-filled cauldron at the position
     * - Closed trapdoor on top
     */
    private static void createPortalStructure(ServerWorld world, BlockPos pos) {
        // Place water-filled cauldron
        BlockState cauldronState = Blocks.WATER_CAULDRON.getDefaultState()
            .with(LeveledCauldronBlock.LEVEL, 3);
        world.setBlockState(pos, cauldronState, 3);

        // Place closed trapdoor on bottom half above the cauldron
        BlockPos trapdoorPos = pos.up();
        BlockState trapdoorState = Blocks.IRON_TRAPDOOR.getDefaultState()
            .with(TrapdoorBlock.OPEN, false)
            .with(TrapdoorBlock.HALF, BlockHalf.BOTTOM)
            .with(TrapdoorBlock.FACING, Direction.NORTH);
        world.setBlockState(trapdoorPos, trapdoorState, 3);

        // Play placement sound
        world.playSound(
            null,
            pos,
            SoundEvents.BLOCK_METAL_PLACE,
            SoundCategory.BLOCKS,
            1.0F,
            1.0F
        );
    }
}
