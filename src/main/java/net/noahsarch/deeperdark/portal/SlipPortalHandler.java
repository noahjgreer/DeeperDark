package net.noahsarch.deeperdark.portal;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.*;

/**
 * Handles the Slip dimension portal mechanic.
 * Portal structure:
 * - End: Horizontal 5x5 frame made of blue ice with 3x3 water center and light block above
 * - Slip: Vertical 5x5 frame made of blue ice with 3x3 flowing water portal
 * - End portals lead to the Slip (1:64 scale)
 * - Slip portals lead to the Overworld (64:1 scale) with waypoint markers
 */
public class SlipPortalHandler {
    private static final RegistryKey<World> THE_END = World.END;
    private static final RegistryKey<World> THE_SLIP = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("minecraft", "the_slip"));
    private static final RegistryKey<World> OVERWORLD = World.OVERWORLD;

    // Cooldown to prevent rapid teleportation (100 ticks = 5 seconds)
    private static final int TELEPORT_COOLDOWN = 100;

    // Track when each player last used a portal
    private static final Map<UUID, Long> playerCooldowns = new HashMap<>();

    // Track which players are currently in portal water (to pause cooldown)
    private static final Set<UUID> playersInPortal = new HashSet<>();

    // Track active horizontal portals in the End (center position -> portal data)
    private static final Map<BlockPos, HorizontalPortalData> activeHorizontalPortals = new HashMap<>();

    // Track active vertical portals in the Slip (center position -> portal data)
    private static final Map<BlockPos, VerticalPortalData> activeVerticalPortals = new HashMap<>();

    // Track waypoint locations in the Overworld (Slip portal center -> Overworld waypoint position)
    private static final Map<BlockPos, BlockPos> slipToOverworldWaypoints = new HashMap<>();

    // Search radius for finding suitable portal location
    private static final int SEARCH_RADIUS = 16;
    private static final int VERTICAL_SEARCH_RANGE = 128;

    // Coordinate scale: 1 block in Slip = 64 blocks in End/Overworld
    private static final int COORDINATE_SCALE = 64;

    // Extended search radius for portals when returning from Slip to End (COORDINATE_SCALE * 3)
    private static final int EXTENDED_SEARCH_RADIUS = COORDINATE_SCALE * 3; // 192 blocks

    private static class HorizontalPortalData {
        final BlockPos centerPos;
        final Set<BlockPos> frameBlocks;
        final Set<BlockPos> waterBlocks;
        final BlockPos lightBlockPos;

        HorizontalPortalData(BlockPos centerPos, Set<BlockPos> frameBlocks, Set<BlockPos> waterBlocks, BlockPos lightBlockPos) {
            this.centerPos = centerPos;
            this.frameBlocks = frameBlocks;
            this.waterBlocks = waterBlocks;
            this.lightBlockPos = lightBlockPos;
        }
    }

    private static class VerticalPortalData {
        final BlockPos centerPos;
        final Direction facing; // North, South, East, or West
        final Set<BlockPos> frameBlocks;
        final Set<BlockPos> waterBlocks;
        final BlockPos lightBlockPos;

        VerticalPortalData(BlockPos centerPos, Direction facing, Set<BlockPos> frameBlocks, Set<BlockPos> waterBlocks, BlockPos lightBlockPos) {
            this.centerPos = centerPos;
            this.facing = facing;
            this.frameBlocks = frameBlocks;
            this.waterBlocks = waterBlocks;
            this.lightBlockPos = lightBlockPos;
        }
    }

    /**
     * Public method to check if a block position is part of an active portal's water blocks.
     * Used by FluidBlockMixin to prevent portal water from being converted to ice.
     */
    public static boolean isPortalWaterBlock(BlockPos pos) {
        // Check horizontal portals (in the End)
        for (HorizontalPortalData portal : activeHorizontalPortals.values()) {
            if (portal.waterBlocks.contains(pos)) {
                return true;
            }
        }

        // Check vertical portals (in the Slip)
        for (VerticalPortalData portal : activeVerticalPortals.values()) {
            if (portal.waterBlocks.contains(pos)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a position is near any active portal (within the given radius).
     * Used to prevent water from flowing out of portals and creating ice cocoons.
     */
    public static boolean isNearPortal(BlockPos pos, int radius) {
        // Check horizontal portals (in the End)
        for (HorizontalPortalData portal : activeHorizontalPortals.values()) {
            if (portal.centerPos.isWithinDistance(pos, radius)) {
                return true;
            }
        }

        // Check vertical portals (only these are in the Slip where ice conversion happens)
        for (VerticalPortalData portal : activeVerticalPortals.values()) {
            if (portal.centerPos.isWithinDistance(pos, radius)) {
                return true;
            }
        }
        return false;
    }

    public static void register() {
        // Handle water bucket placement to activate portals
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            // Only work in the End dimension
            if (!world.getRegistryKey().equals(THE_END)) {
                return ActionResult.PASS;
            }

            ItemStack heldItem = player.getStackInHand(hand);
            if (!heldItem.isOf(Items.WATER_BUCKET)) {
                return ActionResult.PASS;
            }

            BlockPos clickedPos = hitResult.getBlockPos();
            BlockPos placePos = clickedPos.up(); // Water will be placed above the clicked block

            // Check if this could be anywhere in a valid portal frame (not just center)
            BlockPos portalCenter = findPortalCenter(world, placePos);
            if (portalCenter != null && isValidHorizontalPortalFrame(world, portalCenter)) {
                // Activate the portal!
                activateHorizontalPortal((ServerWorld) world, portalCenter);

                // Replace water bucket with empty bucket
                if (!player.isCreative()) {
                    heldItem.decrement(1);
                    if (heldItem.isEmpty()) {
                        player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                    } else {
                        player.giveItemStack(new ItemStack(Items.BUCKET));
                    }
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        // Handle flint and steel activation for vertical portals in the Slip
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) {
                return ActionResult.PASS;
            }

            // Only work in the Slip dimension
            if (!world.getRegistryKey().equals(THE_SLIP)) {
                return ActionResult.PASS;
            }

            ItemStack heldItem = player.getStackInHand(hand);
            if (!heldItem.isOf(Items.FLINT_AND_STEEL)) {
                return ActionResult.PASS;
            }

            BlockPos clickedPos = hitResult.getBlockPos();
            BlockState clickedState = world.getBlockState(clickedPos);

            // Check if player clicked on packed ice
            if (!clickedState.isOf(Blocks.PACKED_ICE)) {
                return ActionResult.PASS;
            }

            // Try to find a valid vertical portal frame with packed ice center
            BlockPos portalCenter = findVerticalPortalCenter(world, clickedPos);
            if (portalCenter != null && isValidVerticalPortalFrameWithPackedIce(world, portalCenter)) {
                // Activate the portal!
                activateVerticalPortalFromPackedIce((ServerWorld) world, portalCenter);

                // Damage the flint and steel
                if (!player.isCreative()) {
                    heldItem.damage(1, player, player.getPreferredEquipmentSlot(heldItem));
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });

        // Handle player teleportation and portal maintenance
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Handle horizontal portals in the End
            ServerWorld endWorld = server.getWorld(THE_END);
            if (endWorld != null) {
                handleHorizontalPortals(server, endWorld);
            }

            // Handle vertical portals in the Slip
            ServerWorld slipWorld = server.getWorld(THE_SLIP);
            if (slipWorld != null) {
                handleVerticalPortals(server, slipWorld);
            }

            // Handle waypoint particle effects in the Overworld
            ServerWorld overworldWorld = server.getWorld(OVERWORLD);
            if (overworldWorld != null) {
                handleOverworldWaypoints(overworldWorld);
            }
        });
    }

    /**
     * Finds the center of a portal frame if the given position is within the 3x3 interior
     */
    private static BlockPos findPortalCenter(World world, BlockPos pos) {
        // Try all possible center positions that would include this position in the 3x3 area
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                BlockPos potentialCenter = pos.add(-xOffset, 0, -zOffset);
                if (isValidHorizontalPortalFrame(world, potentialCenter)) {
                    return potentialCenter;
                }
            }
        }
        return null;
    }

    /**
     * Finds the center of a vertical portal frame if the given position is within the 3x3 interior
     */
    private static BlockPos findVerticalPortalCenter(World world, BlockPos pos) {
        // Try all possible center positions that would include this position in the 3x3 area
        // Check for North-South orientation (X axis)
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                BlockPos potentialCenter = pos.add(-xOffset, -yOffset, 0);
                if (isValidVerticalPortalFrameWithPackedIce(world, potentialCenter)) {
                    return potentialCenter;
                }
            }
        }

        // Check for East-West orientation (Z axis)
        for (int zOffset = -1; zOffset <= 1; zOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                BlockPos potentialCenter = pos.add(0, -yOffset, -zOffset);
                if (isValidVerticalPortalFrameWithPackedIce(world, potentialCenter)) {
                    return potentialCenter;
                }
            }
        }

        return null;
    }

    /**
     * Handles horizontal portals in the End dimension
     */
    private static void handleHorizontalPortals(net.minecraft.server.MinecraftServer server, ServerWorld endWorld) {
        // Check players for teleportation
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!player.getWorld().getRegistryKey().equals(THE_END)) {
                continue;
            }

            BlockPos playerPos = player.getBlockPos();
            UUID playerId = player.getUuid();
            boolean playerInPortalNow = false;

            for (HorizontalPortalData portal : activeHorizontalPortals.values()) {
                if (portal.waterBlocks.contains(playerPos)) {
                    playerInPortalNow = true;
                    long currentTime = player.getWorld().getTime();

                    if (playerCooldowns.containsKey(playerId)) {
                        long lastTeleport = playerCooldowns.get(playerId);
                        if (currentTime - lastTeleport < TELEPORT_COOLDOWN) {
                            continue;
                        }
                    }

                    if (teleportToSlip(player)) {
                        playerCooldowns.put(playerId, currentTime);
                        playersInPortal.add(playerId);
                    }
                    break;
                }
            }

            // Remove player from portal tracking if they left
            if (!playerInPortalNow) {
                playersInPortal.remove(playerId);
            }
        }

        // Check portal integrity, maintain water below, and spawn particles
        Iterator<Map.Entry<BlockPos, HorizontalPortalData>> iterator = activeHorizontalPortals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, HorizontalPortalData> entry = iterator.next();
            HorizontalPortalData portal = entry.getValue();

            if (!isHorizontalPortalFrameIntact(endWorld, portal)) {
                deactivateHorizontalPortal(endWorld, portal);
                iterator.remove();
                continue;
            }

            // Maintain water blocks and prevent flow below
            maintainHorizontalPortalWater(endWorld, portal);

            if (endWorld.getTime() % 2 == 0) {
                spawnHorizontalPortalParticles(endWorld, portal);
            }
        }
    }

    /**
     * Handles vertical portals in the Slip dimension
     */
    private static void handleVerticalPortals(net.minecraft.server.MinecraftServer server, ServerWorld slipWorld) {
        // Check players for teleportation to the Overworld
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!player.getWorld().getRegistryKey().equals(THE_SLIP)) {
                continue;
            }

            BlockPos playerPos = player.getBlockPos();
            UUID playerId = player.getUuid();
            boolean playerInPortalNow = false;

            for (VerticalPortalData portal : activeVerticalPortals.values()) {
                if (portal.waterBlocks.contains(playerPos)) {
                    playerInPortalNow = true;
                    long currentTime = player.getWorld().getTime();

                    // Only check cooldown if player is not currently marked as in portal
                    if (!playersInPortal.contains(playerId)) {
                        if (playerCooldowns.containsKey(playerId)) {
                            long lastTeleport = playerCooldowns.get(playerId);
                            if (currentTime - lastTeleport < TELEPORT_COOLDOWN) {
                                continue;
                            }
                        }

                        if (teleportToOverworld(player, portal.centerPos)) {
                            playerCooldowns.put(playerId, currentTime);
                            playersInPortal.add(playerId);
                        }
                    }
                    break;
                }
            }

            // Remove player from portal tracking if they left
            if (!playerInPortalNow) {
                playersInPortal.remove(playerId);
            }
        }

        // Check portal integrity, maintain water, and spawn particles
        Iterator<Map.Entry<BlockPos, VerticalPortalData>> iterator = activeVerticalPortals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, VerticalPortalData> entry = iterator.next();
            VerticalPortalData portal = entry.getValue();

            if (!isVerticalPortalFrameIntact(slipWorld, portal)) {
                deactivateVerticalPortal(slipWorld, portal);
                iterator.remove();
                continue;
            }

            // Maintain contained water blocks
            maintainVerticalPortalWater(slipWorld, portal);

            if (slipWorld.getTime() % 2 == 0) {
                spawnVerticalPortalParticles(slipWorld, portal);
            }
        }
    }

    /**
     * Checks if there's a valid blue ice frame around the given center position (horizontal)
     */
    private static boolean isValidHorizontalPortalFrame(World world, BlockPos center) {
        // North side (z = -2)
        for (int x = -1; x <= 1; x++) {
            if (!world.getBlockState(center.add(x, 0, -2)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        // South side (z = 2)
        for (int x = -1; x <= 1; x++) {
            if (!world.getBlockState(center.add(x, 0, 2)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        // West side (x = -2)
        for (int z = -1; z <= 1; z++) {
            if (!world.getBlockState(center.add(-2, 0, z)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        // East side (x = 2)
        for (int z = -1; z <= 1; z++) {
            if (!world.getBlockState(center.add(2, 0, z)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if there's a valid blue ice frame around the given center position with packed ice in the center (vertical portal)
     */
    private static boolean isValidVerticalPortalFrameWithPackedIce(World world, BlockPos center) {
        // Try North-South orientation first (frame parallel to X axis)
        boolean isNorthSouthValid = true;

        // Check horizontal bars (top and bottom)
        for (int x = -1; x <= 1; x++) {
            if (!world.getBlockState(center.add(x, -2, 0)).isOf(Blocks.BLUE_ICE)) {
                isNorthSouthValid = false;
                break;
            }
            if (!world.getBlockState(center.add(x, 2, 0)).isOf(Blocks.BLUE_ICE)) {
                isNorthSouthValid = false;
                break;
            }
        }

        // Check vertical bars (left and right)
        if (isNorthSouthValid) {
            for (int y = -1; y <= 1; y++) {
                if (!world.getBlockState(center.add(-2, y, 0)).isOf(Blocks.BLUE_ICE)) {
                    isNorthSouthValid = false;
                    break;
                }
                if (!world.getBlockState(center.add(2, y, 0)).isOf(Blocks.BLUE_ICE)) {
                    isNorthSouthValid = false;
                    break;
                }
            }
        }

        // Check that at least ONE block in the 3x3 center is packed ice (relaxed requirement)
        if (isNorthSouthValid) {
            boolean hasPackedIce = false;
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (world.getBlockState(center.add(x, y, 0)).isOf(Blocks.PACKED_ICE)) {
                        hasPackedIce = true;
                        break;
                    }
                }
                if (hasPackedIce) break;
            }
            if (!hasPackedIce) {
                isNorthSouthValid = false;
            }
        }

        if (isNorthSouthValid) {
            return true;
        }

        // Try East-West orientation (frame parallel to Z axis)
        boolean isEastWestValid = true;

        // Check horizontal bars (top and bottom)
        for (int z = -1; z <= 1; z++) {
            if (!world.getBlockState(center.add(0, -2, z)).isOf(Blocks.BLUE_ICE)) {
                isEastWestValid = false;
                break;
            }
            if (!world.getBlockState(center.add(0, 2, z)).isOf(Blocks.BLUE_ICE)) {
                isEastWestValid = false;
                break;
            }
        }

        // Check vertical bars (left and right)
        if (isEastWestValid) {
            for (int y = -1; y <= 1; y++) {
                if (!world.getBlockState(center.add(0, y, -2)).isOf(Blocks.BLUE_ICE)) {
                    isEastWestValid = false;
                    break;
                }
                if (!world.getBlockState(center.add(0, y, 2)).isOf(Blocks.BLUE_ICE)) {
                    isEastWestValid = false;
                    break;
                }
            }
        }

        // Check that at least ONE block in the 3x3 center is packed ice (relaxed requirement)
        if (isEastWestValid) {
            boolean hasPackedIce = false;
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    if (world.getBlockState(center.add(0, y, z)).isOf(Blocks.PACKED_ICE)) {
                        hasPackedIce = true;
                        break;
                    }
                }
                if (hasPackedIce) break;
            }
            if (!hasPackedIce) {
                isEastWestValid = false;
            }
        }

        return isEastWestValid;
    }

    /**
     * Checks if a horizontal portal's frame is still intact
     */
    private static boolean isHorizontalPortalFrameIntact(ServerWorld world, HorizontalPortalData portal) {
        for (BlockPos framePos : portal.frameBlocks) {
            if (!world.getBlockState(framePos).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a vertical portal's frame is still intact
     */
    private static boolean isVerticalPortalFrameIntact(ServerWorld world, VerticalPortalData portal) {
        for (BlockPos framePos : portal.frameBlocks) {
            if (!world.getBlockState(framePos).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Activates a horizontal portal by filling the center with water and placing a light block above
     */
    private static void activateHorizontalPortal(ServerWorld world, BlockPos center) {
        Set<BlockPos> frameBlocks = new HashSet<>();
        Set<BlockPos> waterBlocks = new HashSet<>();

        // Collect frame blocks
        for (int x = -1; x <= 1; x++) {
            frameBlocks.add(center.add(x, 0, -2));
            frameBlocks.add(center.add(x, 0, 2));
        }
        for (int z = -1; z <= 1; z++) {
            frameBlocks.add(center.add(-2, 0, z));
            frameBlocks.add(center.add(2, 0, z));
        }

        // Fill the 3x3 center with water
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos waterPos = center.add(x, 0, z);
                world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 2);
                waterBlocks.add(waterPos);
            }
        }

        // Place light block above the center for illumination
        BlockPos lightBlockPos = center.up();
        world.setBlockState(lightBlockPos, Blocks.LIGHT.getDefaultState(), 2);

        // Register the portal
        activeHorizontalPortals.put(center, new HorizontalPortalData(center, frameBlocks, waterBlocks, lightBlockPos));

        // Play activation sound
        world.playSound(
            null,
            center,
            SoundEvents.BLOCK_PORTAL_TRIGGER,
            SoundCategory.BLOCKS,
            1.0F,
            1.0F
        );

        // Spawn initial particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 3;
            double offsetY = world.random.nextDouble() * 0.5;
            double offsetZ = (world.random.nextDouble() - 0.5) * 3;

            world.spawnParticles(
                ParticleTypes.PORTAL,
                center.getX() + 0.5 + offsetX,
                center.getY() + offsetY,
                center.getZ() + 0.5 + offsetZ,
                10,
                0, 0, 0,
                0.1
            );
        }
    }

    /**
     * Maintains the water blocks in a horizontal portal and prevents water from flowing below
     */
    private static void maintainHorizontalPortalWater(ServerWorld world, HorizontalPortalData portal) {
        // Replace any water blocks that appear below the portal (similar to vertical portal containment)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos belowPos = portal.centerPos.add(x, -1, z);
                if (world.getBlockState(belowPos).isOf(Blocks.WATER)) {
                    world.setBlockState(belowPos, Blocks.AIR.getDefaultState(), 2);
                }
            }
        }
    }

    /**
     * Deactivates a horizontal portal by removing the water and light block
     */
    private static void deactivateHorizontalPortal(ServerWorld world, HorizontalPortalData portal) {
        // Remove water blocks
        for (BlockPos waterPos : portal.waterBlocks) {
            BlockState state = world.getBlockState(waterPos);
            if (state.isOf(Blocks.WATER)) {
                world.setBlockState(waterPos, Blocks.AIR.getDefaultState(), 3);
            }
        }

        // Remove light block
        if (world.getBlockState(portal.lightBlockPos).isOf(Blocks.LIGHT)) {
            world.setBlockState(portal.lightBlockPos, Blocks.AIR.getDefaultState(), 3);
        }

        // Play deactivation sound
        world.playSound(
            null,
            portal.centerPos,
            SoundEvents.BLOCK_FIRE_EXTINGUISH,
            SoundCategory.BLOCKS,
            1.0F,
            0.8F
        );
    }

    /**
     * Spawns particles around an active horizontal portal
     */
    private static void spawnHorizontalPortalParticles(ServerWorld world, HorizontalPortalData portal) {
        // Spawn particles in the water area
        for (BlockPos waterPos : portal.waterBlocks) {
            if (world.random.nextInt(3) == 0) {
                double x = waterPos.getX() + world.random.nextDouble();
                double y = waterPos.getY() + 0.1;
                double z = waterPos.getZ() + world.random.nextDouble();

                world.spawnParticles(
                    ParticleTypes.PORTAL,
                    x, y, z,
                    5,
                    0, 0.1, 0,
                    0.15
                );
            }
        }

        // Spawn particles around the frame
        if (world.random.nextInt(2) == 0) {
            BlockPos randomFrame = portal.frameBlocks.stream()
                .skip(world.random.nextInt(portal.frameBlocks.size()))
                .findFirst()
                .orElse(portal.centerPos);

            double x = randomFrame.getX() + world.random.nextDouble();
            double y = randomFrame.getY() + world.random.nextDouble();
            double z = randomFrame.getZ() + world.random.nextDouble();

            world.spawnParticles(
                ParticleTypes.SNOWFLAKE,
                x, y, z,
                5,
                0, 0, 0,
                0.02
            );
        }
    }

    /**
     * Creates a vertical portal in the Slip dimension
     */
    private static void activateVerticalPortal(ServerWorld world, BlockPos center, Direction facing) {
        Set<BlockPos> frameBlocks = new HashSet<>();
        Set<BlockPos> waterBlocks = new HashSet<>();

        // Determine frame orientation based on facing direction
        boolean isNorthSouth = facing == Direction.NORTH || facing == Direction.SOUTH;

        if (isNorthSouth) {
            // Frame parallel to X axis (North/South facing)
            // Horizontal bars
            for (int x = -1; x <= 1; x++) {
                frameBlocks.add(center.add(x, -2, 0));
                frameBlocks.add(center.add(x, 2, 0));
            }
            // Vertical bars
            for (int y = -1; y <= 1; y++) {
                frameBlocks.add(center.add(-2, y, 0));
                frameBlocks.add(center.add(2, y, 0));
            }

            // Fill the 3x3 center with flowing water
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos waterPos = center.add(x, y, 0);
                    world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 2);
                    waterBlocks.add(waterPos);
                }
            }

            // Place light block in front of the portal
            BlockPos lightBlockPos = center.offset(facing);
            world.setBlockState(lightBlockPos, Blocks.LIGHT.getDefaultState(), 2);

            activeVerticalPortals.put(center, new VerticalPortalData(center, facing, frameBlocks, waterBlocks, lightBlockPos));
        } else {
            // Frame parallel to Z axis (East/West facing)
            // Horizontal bars
            for (int z = -1; z <= 1; z++) {
                frameBlocks.add(center.add(0, -2, z));
                frameBlocks.add(center.add(0, 2, z));
            }
            // Vertical bars
            for (int y = -1; y <= 1; y++) {
                frameBlocks.add(center.add(0, y, -2));
                frameBlocks.add(center.add(0, y, 2));
            }

            // Fill the 3x3 center with flowing water
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos waterPos = center.add(0, y, z);
                    world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 2);
                    waterBlocks.add(waterPos);
                }
            }

            // Place light block in front of the portal
            BlockPos lightBlockPos = center.offset(facing);
            world.setBlockState(lightBlockPos, Blocks.LIGHT.getDefaultState(), 2);

            activeVerticalPortals.put(center, new VerticalPortalData(center, facing, frameBlocks, waterBlocks, lightBlockPos));
        }

        // Play activation sound
        world.playSound(
            null,
            center,
            SoundEvents.BLOCK_PORTAL_TRIGGER,
            SoundCategory.BLOCKS,
            1.0F,
            1.0F
        );

        // Spawn initial particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 3;
            double offsetY = (world.random.nextDouble() - 0.5) * 3;
            double offsetZ = (world.random.nextDouble() - 0.5) * 3;

            world.spawnParticles(
                ParticleTypes.PORTAL,
                center.getX() + 0.5 + offsetX,
                center.getY() + offsetY,
                center.getZ() + 0.5 + offsetZ,
                10,
                0, 0, 0,
                0.1
            );
        }
    }

    /**
     * Activates a vertical portal by converting packed ice to water
     */
    private static void activateVerticalPortalFromPackedIce(ServerWorld world, BlockPos center) {
        Set<BlockPos> frameBlocks = new HashSet<>();
        Set<BlockPos> waterBlocks = new HashSet<>();

        // Determine orientation by checking which frame layout is valid
        // This ensures we always generate the portal facing the right direction
        boolean isNorthSouth = isValidVerticalPortalFrameWithPackedIce_NorthSouth(world, center);
        Direction facing = isNorthSouth ? Direction.NORTH : Direction.EAST;

        if (isNorthSouth) {
            // Frame parallel to X axis (North/South facing)
            // Collect frame blocks
            for (int x = -1; x <= 1; x++) {
                frameBlocks.add(center.add(x, -2, 0));
                frameBlocks.add(center.add(x, 2, 0));
            }
            for (int y = -1; y <= 1; y++) {
                frameBlocks.add(center.add(-2, y, 0));
                frameBlocks.add(center.add(2, y, 0));
            }

            // Convert packed ice to water in the 3x3 center
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos waterPos = center.add(x, y, 0);
                    world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 2);
                    waterBlocks.add(waterPos);
                }
            }

            // Place light block in front of the portal
            BlockPos lightBlockPos = center.offset(facing);
            world.setBlockState(lightBlockPos, Blocks.LIGHT.getDefaultState(), 2);

            activeVerticalPortals.put(center, new VerticalPortalData(center, facing, frameBlocks, waterBlocks, lightBlockPos));
        } else {
            // Frame parallel to Z axis (East/West facing)
            // Collect frame blocks
            for (int z = -1; z <= 1; z++) {
                frameBlocks.add(center.add(0, -2, z));
                frameBlocks.add(center.add(0, 2, z));
            }
            for (int y = -1; y <= 1; y++) {
                frameBlocks.add(center.add(0, y, -2));
                frameBlocks.add(center.add(0, y, 2));
            }

            // Convert packed ice to water in the 3x3 center
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    BlockPos waterPos = center.add(0, y, z);
                    world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 2);
                    waterBlocks.add(waterPos);
                }
            }

            // Place light block in front of the portal
            BlockPos lightBlockPos = center.offset(facing);
            world.setBlockState(lightBlockPos, Blocks.LIGHT.getDefaultState(), 2);

            activeVerticalPortals.put(center, new VerticalPortalData(center, facing, frameBlocks, waterBlocks, lightBlockPos));
        }

        // Play activation sound
        world.playSound(
            null,
            center,
            SoundEvents.BLOCK_PORTAL_TRIGGER,
            SoundCategory.BLOCKS,
            1.0F,
            1.0F
        );

        // Spawn initial particle burst
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 3;
            double offsetY = (world.random.nextDouble() - 0.5) * 3;
            double offsetZ = (world.random.nextDouble() - 0.5) * 3;

            world.spawnParticles(
                ParticleTypes.PORTAL,
                center.getX() + 0.5 + offsetX,
                center.getY() + offsetY,
                center.getZ() + 0.5 + offsetZ,
                10,
                0, 0, 0,
                0.1
            );
        }
    }

    /**
     * Maintains the water blocks in a vertical portal (prevents them from flowing out)
     */
    private static void maintainVerticalPortalWater(ServerWorld world, VerticalPortalData portal) {
        // Re-place any missing water blocks to keep the portal contained
        for (BlockPos waterPos : portal.waterBlocks) {
            if (!world.getBlockState(waterPos).isOf(Blocks.WATER)) {
                world.setBlockState(waterPos, Blocks.WATER.getDefaultState(), 2);
            }
        }
    }

    /**
     * Deactivates a vertical portal by removing the water and light block
     */
    private static void deactivateVerticalPortal(ServerWorld world, VerticalPortalData portal) {
        // Remove water blocks
        for (BlockPos waterPos : portal.waterBlocks) {
            BlockState state = world.getBlockState(waterPos);
            if (state.isOf(Blocks.WATER)) {
                world.setBlockState(waterPos, Blocks.AIR.getDefaultState(), 3);
            }
        }

        // Remove light block
        if (world.getBlockState(portal.lightBlockPos).isOf(Blocks.LIGHT)) {
            world.setBlockState(portal.lightBlockPos, Blocks.AIR.getDefaultState(), 3);
        }

        // Play deactivation sound
        world.playSound(
            null,
            portal.centerPos,
            SoundEvents.BLOCK_FIRE_EXTINGUISH,
            SoundCategory.BLOCKS,
            1.0F,
            0.8F
        );
    }

    /**
     * Spawns particles around an active vertical portal
     */
    private static void spawnVerticalPortalParticles(ServerWorld world, VerticalPortalData portal) {
        // Spawn particles in the water area
        for (BlockPos waterPos : portal.waterBlocks) {
            if (world.random.nextInt(3) == 0) {
                double x = waterPos.getX() + world.random.nextDouble();
                double y = waterPos.getY() + world.random.nextDouble();
                double z = waterPos.getZ() + world.random.nextDouble();

                world.spawnParticles(
                    ParticleTypes.PORTAL,
                    x, y, z,
                    5,
                    0, 0, 0,
                    0.15
                );
            }
        }

        // Spawn particles around the frame
        if (world.random.nextInt(2) == 0) {
            BlockPos randomFrame = portal.frameBlocks.stream()
                .skip(world.random.nextInt(portal.frameBlocks.size()))
                .findFirst()
                .orElse(portal.centerPos);

            double x = randomFrame.getX() + world.random.nextDouble();
            double y = randomFrame.getY() + world.random.nextDouble();
            double z = randomFrame.getZ() + world.random.nextDouble();

            world.spawnParticles(
                ParticleTypes.SNOWFLAKE,
                x, y, z,
                5,
                0, 0, 0,
                0.02
            );
        }
    }

    /**
     * Checks if there's a valid blue ice frame around the given center position with packed ice in the center (vertical portal)
     * North-South orientation check
     */
    private static boolean isValidVerticalPortalFrameWithPackedIce_NorthSouth(World world, BlockPos center) {
        // Check horizontal bars (top and bottom)
        for (int x = -1; x <= 1; x++) {
            if (!world.getBlockState(center.add(x, -2, 0)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
            if (!world.getBlockState(center.add(x, 2, 0)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        // Check vertical bars (left and right)
        for (int y = -1; y <= 1; y++) {
            if (!world.getBlockState(center.add(-2, y, 0)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
            if (!world.getBlockState(center.add(2, y, 0)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        // Check that at least ONE block in the 3x3 center is packed ice (relaxed requirement)
        boolean hasPackedIce = false;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (world.getBlockState(center.add(x, y, 0)).isOf(Blocks.PACKED_ICE)) {
                    hasPackedIce = true;
                    break;
                }
            }
            if (hasPackedIce) break;
        }
        return hasPackedIce;
    }

    /**
     * Checks if there's a valid blue ice frame around the given center position with packed ice in the center (vertical portal)
     * East-West orientation check
     */
    private static boolean isValidVerticalPortalFrameWithPackedIce_EastWest(World world, BlockPos center) {
        // Check horizontal bars (top and bottom)
        for (int z = -1; z <= 1; z++) {
            if (!world.getBlockState(center.add(0, -2, z)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
            if (!world.getBlockState(center.add(0, 2, z)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        // Check vertical bars (left and right)
        for (int y = -1; y <= 1; y++) {
            if (!world.getBlockState(center.add(0, y, -2)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
            if (!world.getBlockState(center.add(0, y, 2)).isOf(Blocks.BLUE_ICE)) {
                return false;
            }
        }

        // Check that at least ONE block in the 3x3 center is packed ice (relaxed requirement)
        boolean hasPackedIce = false;
        for (int z = -1; z <= 1; z++) {
            for (int y = -1; y <= 1; y++) {
                if (world.getBlockState(center.add(0, y, z)).isOf(Blocks.PACKED_ICE)) {
                    hasPackedIce = true;
                    break;
                }
            }
            if (hasPackedIce) break;
        }
        return hasPackedIce;
    }

    /**
     * Teleports the player to The Slip dimension
     */
    private static boolean teleportToSlip(ServerPlayerEntity player) {
        if (player.getServer() == null) {
            return false;
        }

        ServerWorld slipWorld = player.getServer().getWorld(THE_SLIP);
        if (slipWorld == null) {
            return false;
        }

        BlockPos playerPos = player.getBlockPos();

        // Find which portal the player is in and use its center position
        BlockPos portalCenter = null;
        for (HorizontalPortalData portal : activeHorizontalPortals.values()) {
            if (portal.waterBlocks.contains(playerPos)) {
                portalCenter = portal.centerPos;
                break;
            }
        }

        // Fallback to player position if not in a tracked portal (shouldn't happen)
        BlockPos sourcePos = portalCenter != null ? portalCenter : playerPos;

        // Apply coordinate scaling: divide by 64 when going from End to Slip
        BlockPos scaledPos = new BlockPos(sourcePos.getX() / COORDINATE_SCALE, sourcePos.getY(), sourcePos.getZ() / COORDINATE_SCALE);
        BlockPos targetPos = findOrCreateVerticalPortalLocation(slipWorld, scaledPos);

        if (targetPos == null) {
            return false;
        }

        // Play portal sound in source dimension
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

        // Create teleport target - spawn player IN the portal water
        Vec3d targetVec = new Vec3d(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        TeleportTarget target = new TeleportTarget(
            slipWorld,
            targetVec,
            Vec3d.ZERO,
            player.getYaw(),
            player.getPitch(),
            TeleportTarget.NO_OP
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
     * Teleports the player to the Overworld dimension with a waypoint marker
     */
    private static boolean teleportToOverworld(ServerPlayerEntity player, BlockPos slipPortalCenter) {
        if (player.getServer() == null) {
            return false;
        }

        ServerWorld overworldWorld = player.getServer().getWorld(OVERWORLD);
        if (overworldWorld == null) {
            return false;
        }

        // Check if this portal already has a waypoint
        BlockPos waypointPos = slipToOverworldWaypoints.get(slipPortalCenter);

        if (waypointPos != null) {
            // Verify waypoint still exists
            BlockPos flowerPos = waypointPos.up();
            if (overworldWorld.getBlockState(waypointPos).isOf(Blocks.BLUE_ICE) &&
                overworldWorld.getBlockState(flowerPos).isOf(Blocks.WILDFLOWERS)) {
                // Use existing waypoint
                teleportPlayerToWaypoint(player, overworldWorld, waypointPos);
                return true;
            } else {
                // Waypoint was destroyed, remove from map
                slipToOverworldWaypoints.remove(slipPortalCenter);
            }
        }

        // Create new waypoint
        // Apply coordinate scaling: multiply by 64 when going from Slip to Overworld
        BlockPos scaledPos = new BlockPos(
            slipPortalCenter.getX() * COORDINATE_SCALE,
            64, // Start search at y=64
            slipPortalCenter.getZ() * COORDINATE_SCALE
        );

        // Find surface location
        BlockPos surfacePos = findSurfaceLocation(overworldWorld, scaledPos);
        if (surfacePos == null) {
            return false;
        }

        // Create waypoint marker (one block lower so ice is in ground)
        BlockPos waypointBasePos = surfacePos.down();
        createWaypointMarker(overworldWorld, waypointBasePos);

        // Store waypoint location
        slipToOverworldWaypoints.put(slipPortalCenter, waypointBasePos);

        // Teleport player to waypoint
        teleportPlayerToWaypoint(player, overworldWorld, waypointBasePos);

        return true;
    }

    /**
     * Finds a suitable surface location in the Overworld
     */
    private static BlockPos findSurfaceLocation(ServerWorld world, BlockPos startPos) {
        // Search in a spiral pattern for a suitable surface
        for (int radius = 0; radius <= SEARCH_RADIUS; radius++) {
            for (int xOffset = -radius; xOffset <= radius; xOffset++) {
                for (int zOffset = -radius; zOffset <= radius; zOffset++) {
                    // Only check the perimeter of each radius level
                    if (Math.abs(xOffset) != radius && Math.abs(zOffset) != radius) {
                        continue;
                    }

                    BlockPos checkPos = new BlockPos(
                        startPos.getX() + xOffset,
                        startPos.getY(),
                        startPos.getZ() + zOffset
                    );

                    // Find the highest solid block (surface)
                    BlockPos surfacePos = world.getTopPosition(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, checkPos);

                    // Make sure there's solid ground and air above
                    if (world.getBlockState(surfacePos).isSolidBlock(world, surfacePos) &&
                        world.getBlockState(surfacePos.up()).isAir() &&
                        world.getBlockState(surfacePos.up(2)).isAir()) {
                        return surfacePos;
                    }
                }
            }
        }

        // Fallback: use the top position at the scaled location
        return world.getTopPosition(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, startPos);
    }

    /**
     * Creates a waypoint marker (blue ice + pink petals)
     */
    private static void createWaypointMarker(ServerWorld world, BlockPos pos) {
        // Place blue ice block (in the ground)
        world.setBlockState(pos, Blocks.BLUE_ICE.getDefaultState(), 3);

        // Place pink petals on top (at surface level)
        BlockPos flowerPos = pos.up();
        world.setBlockState(flowerPos, Blocks.WILDFLOWERS.getDefaultState(), 3);

        // Play a magical sound
        world.playSound(
            null,
            pos,
            SoundEvents.BLOCK_PORTAL_TRIGGER,
            SoundCategory.BLOCKS,
            0.5F,
            1.5F
        );

        // Spawn particle burst
        for (int i = 0; i < 10; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2;
            double offsetY = world.random.nextDouble() * 2;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2;

            world.spawnParticles(
                ParticleTypes.END_ROD,
                flowerPos.getX() + 0.5 + offsetX,
                flowerPos.getY() + offsetY,
                flowerPos.getZ() + 0.5 + offsetZ,
                5,
                0, 0, 0,
                0.05
            );
        }
    }

    /**
     * Teleports a player to a waypoint location
     */
    private static void teleportPlayerToWaypoint(ServerPlayerEntity player, ServerWorld overworldWorld, BlockPos waypointPos) {
        // Play portal sound in source dimension
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

        // Teleport player to stand on the waypoint
        BlockPos spawnPos = waypointPos.up();
        Vec3d targetVec = new Vec3d(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        TeleportTarget target = new TeleportTarget(
            overworldWorld,
            targetVec,
            Vec3d.ZERO,
            player.getYaw(),
            player.getPitch(),
            TeleportTarget.NO_OP
        );

        // Teleport the player
        player.teleportTo(target);

        // Play sound in destination world
        overworldWorld.playSound(
            null,
            spawnPos,
            SoundEvents.BLOCK_PORTAL_TRAVEL,
            SoundCategory.PLAYERS,
            1.0F,
            1.0F
        );
    }

    /**
     * Handles waypoint particle effects in the Overworld
     */
    private static void handleOverworldWaypoints(ServerWorld overworldWorld) {
        // Spawn particles around waypoint flowers
        for (BlockPos waypointPos : slipToOverworldWaypoints.values()) {
            BlockPos flowerPos = waypointPos.up();

            // Check if the waypoint is still intact (blue ice + pink petals)
            if (!overworldWorld.getBlockState(waypointPos).isOf(Blocks.BLUE_ICE) ||
                !overworldWorld.getBlockState(flowerPos).isOf(Blocks.WILDFLOWERS)) {
                continue;
            }

            // Spawn particles across the entire top surface of the block
            // Moved half a block down (from 0.9 to 0.4)
            double x = flowerPos.getX() + overworldWorld.random.nextDouble();
            double y = flowerPos.getY() + 0.4;
            double z = flowerPos.getZ() + overworldWorld.random.nextDouble();

            // Half the count (was 1, now spawning half as often which effectively halves count)
            // Half the speed (was 0.005, now 0.0025)
            if (overworldWorld.random.nextInt(2) == 0) {
                overworldWorld.spawnParticles(
                    ParticleTypes.END_ROD,
                    x, y, z,
                    1,
                    0, 0, 0,
                    0.0025
                );
            }
        }
    }

    /**
     * Finds an existing vertical portal or creates a new one in the Slip dimension
     */
    private static BlockPos findOrCreateVerticalPortalLocation(ServerWorld world, BlockPos sourcePos) {
        // Use the scaled position for search
        BlockPos searchCenter = new BlockPos(sourcePos.getX(), 64, sourcePos.getZ());

        // Try to find existing portal with extended search radius to prevent adjacent portals
        // Only check X and Z coordinates (ignore Y) to find portals at any height
        int extendedRadius = SEARCH_RADIUS * 2;
        for (BlockPos portalCenter : activeVerticalPortals.keySet()) {
            int xDiff = Math.abs(portalCenter.getX() - searchCenter.getX());
            int zDiff = Math.abs(portalCenter.getZ() - searchCenter.getZ());
            double horizontalDistance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

            if (horizontalDistance <= extendedRadius) {
                // Found an existing portal within range, use it!
                return portalCenter;
            }
        }

        // Search for suitable location, prioritizing higher Y levels to avoid void spawning
        // Start from top of world and work down
        BlockPos bestLocation = null;
        int bestY = Integer.MIN_VALUE;

        for (int radius = 0; radius <= SEARCH_RADIUS; radius++) {
            for (int xOffset = -radius; xOffset <= radius; xOffset++) {
                for (int zOffset = -radius; zOffset <= radius; zOffset++) {
                    // Only check the perimeter of each radius level
                    if (Math.abs(xOffset) != radius && Math.abs(zOffset) != radius) {
                        continue;
                    }

                    BlockPos basePos = new BlockPos(
                        searchCenter.getX() + xOffset,
                        searchCenter.getY(),
                        searchCenter.getZ() + zOffset
                    );

                    // Search vertically from high to low to prefer spawning at higher Y levels
                    for (int yOffset = VERTICAL_SEARCH_RANGE; yOffset >= -VERTICAL_SEARCH_RANGE; yOffset--) {
                        BlockPos checkPos = basePos.add(0, yOffset, 0);

                        if (isSuitableVerticalPortalLocation(world, checkPos)) {
                            // Prefer locations with higher Y values
                            if (checkPos.getY() > bestY) {
                                bestY = checkPos.getY();
                                bestLocation = checkPos;
                            }
                        }
                    }
                }
            }

            // If we found a good location in this radius, use it
            if (bestLocation != null) {
                Direction facing = Direction.NORTH;
                createVerticalPortalStructure(world, bestLocation, facing);
                return bestLocation;
            }
        }

        // Fallback: create portal in mid-air with platform at a high Y level (prefer top of world over void)
        BlockPos fallbackPos = new BlockPos(searchCenter.getX(), 128, searchCenter.getZ());
        createVerticalPortalWithPlatform(world, fallbackPos, Direction.NORTH);
        return fallbackPos;
    }

    /**
     * Checks if a location is suitable for vertical portal placement
     */
    private static boolean isSuitableVerticalPortalLocation(ServerWorld world, BlockPos pos) {
        // Need a 5x5 vertical area clear (checking North-South orientation)
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                if (!world.getBlockState(pos.add(x, y, 0)).isAir()) {
                    return false;
                }
            }
        }

        // Check for solid ground below the portal (3 blocks down)
        BlockPos groundCheck = pos.down(3);
        if (!world.getBlockState(groundCheck).isSolidBlock(world, groundCheck)) {
            return false;
        }

        // Additional check: Make sure there's open air above and to the sides
        // This helps avoid spawning in narrow caves or inside walls
        for (int x = -3; x <= 3; x++) {
            for (int z = -1; z <= 1; z++) {
                // Check a few blocks above the portal
                for (int y = 3; y <= 5; y++) {
                    BlockPos checkPos = pos.add(x, y, z);
                    if (!world.getBlockState(checkPos).isAir()) {
                        // There's a block above - might be inside a cave/wall
                        // Only reject if we're completely enclosed
                        int enclosedCount = 0;
                        // Check all directions around the portal
                        for (int dx = -3; dx <= 3; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (!world.getBlockState(pos.add(dx, 2, dz)).isAir()) {
                                    enclosedCount++;
                                }
                            }
                        }
                        // If more than 70% enclosed, reject this location
                        if (enclosedCount > 24) { // 24 out of 35 blocks (7x5)
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Creates a vertical portal structure at the given position
     */
    private static void createVerticalPortalStructure(ServerWorld world, BlockPos center, Direction facing) {
        boolean isNorthSouth = facing == Direction.NORTH || facing == Direction.SOUTH;

        if (isNorthSouth) {
            // Frame parallel to X axis
            for (int x = -1; x <= 1; x++) {
                world.setBlockState(center.add(x, -2, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(x, 2, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
            for (int y = -1; y <= 1; y++) {
                world.setBlockState(center.add(-2, y, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(2, y, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
        } else {
            // Frame parallel to Z axis
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(center.add(0, -2, z), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(0, 2, z), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
            for (int y = -1; y <= 1; y++) {
                world.setBlockState(center.add(0, y, -2), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(0, y, 2), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
        }

        // Activate the portal (fill with water)
        activateVerticalPortal(world, center, facing);

        // Play placement sound
        world.playSound(
            null,
            center,
            SoundEvents.BLOCK_GLASS_PLACE,
            SoundCategory.BLOCKS,
            1.0F,
            0.8F
        );
    }

    /**
     * Creates a vertical portal structure with a packed ice platform (for void/air placement)
     */
    private static void createVerticalPortalWithPlatform(ServerWorld world, BlockPos center, Direction facing) {
        boolean isNorthSouth = facing == Direction.NORTH || facing == Direction.SOUTH;

        // Create 5x5 packed ice platform below the portal
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos platformPos = center.add(x, -3, z);
                world.setBlockState(platformPos, Blocks.PACKED_ICE.getDefaultState(), 3);
            }
        }

        if (isNorthSouth) {
            // Frame parallel to X axis
            for (int x = -1; x <= 1; x++) {
                world.setBlockState(center.add(x, -2, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(x, 2, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
            for (int y = -1; y <= 1; y++) {
                world.setBlockState(center.add(-2, y, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(2, y, 0), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
        } else {
            // Frame parallel to Z axis
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(center.add(0, -2, z), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(0, 2, z), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
            for (int y = -1; y <= 1; y++) {
                world.setBlockState(center.add(0, y, -2), Blocks.BLUE_ICE.getDefaultState(), 3);
                world.setBlockState(center.add(0, y, 2), Blocks.BLUE_ICE.getDefaultState(), 3);
            }
        }

        // Activate the portal (fill with water)
        activateVerticalPortal(world, center, facing);

        // Play placement sound
        world.playSound(
            null,
            center,
            SoundEvents.BLOCK_GLASS_PLACE,
            SoundCategory.BLOCKS,
            1.0F,
            0.8F
        );
    }
}

