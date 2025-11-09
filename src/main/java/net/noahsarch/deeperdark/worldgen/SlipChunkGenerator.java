package net.noahsarch.deeperdark.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.noahsarch.deeperdark.Deeperdark;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SlipChunkGenerator extends ChunkGenerator {
    public static final MapCodec<SlipChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, SlipChunkGenerator::new)
    );

    private StructureTemplate roomTemplate;
    private StructureTemplate room1Template;
    private StructureTemplate roomHoleTemplate;
    private Vec3i roomSize;
    private Vec3i room1Size;
    private Vec3i roomHoleSize;
    private MinecraftServer cachedServer;
    private static final int DEFAULT_ROOM_SIZE = 16; // Default until we load the template
    private boolean templatesLoaded = false;

    public SlipChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    private void ensureTemplateLoaded(MinecraftServer server) {
        if (!templatesLoaded && server != null) {
            try {
                StructureTemplateManager manager = server.getStructureTemplateManager();
                Deeperdark.LOGGER.info("Attempting to load templates from StructureTemplateManager: {}", manager != null);

                if (manager == null) {
                    Deeperdark.LOGGER.error("StructureTemplateManager is null! Cannot load templates.");
                    templatesLoaded = true;
                    return;
                }

                // Load room0 (default room) - trying minecraft namespace
                // Try multiple possible paths
                String[] possiblePaths = {
                    "the_slip/room0",
                    "structure/the_slip/room0"
                };

                for (String path : possiblePaths) {
                    Identifier templateId = Identifier.of("minecraft", path);
                    Deeperdark.LOGGER.info("Trying to load template: {}", templateId);
                    Optional<StructureTemplate> template = manager.getTemplate(templateId);

                    if (template.isPresent()) {
                        roomTemplate = template.get();
                        roomSize = roomTemplate.getSize();
                        Deeperdark.LOGGER.info("✓ Loaded Slip room0 template from {} with size: {}x{}x{}",
                                templateId, roomSize.getX(), roomSize.getY(), roomSize.getZ());
                        break;
                    }
                }

                if (roomTemplate == null) {
                    Deeperdark.LOGGER.error("✗ Failed to load room0 template from any path");
                    roomSize = new Vec3i(DEFAULT_ROOM_SIZE, DEFAULT_ROOM_SIZE, DEFAULT_ROOM_SIZE);
                }

                // Load room1 (additional common room)
                for (String path : new String[]{"the_slip/room1", "structure/the_slip/room1"}) {
                    Identifier room1TemplateId = Identifier.of("minecraft", path);
                    Deeperdark.LOGGER.info("Trying to load template: {}", room1TemplateId);
                    Optional<StructureTemplate> room1Template = manager.getTemplate(room1TemplateId);

                    if (room1Template.isPresent()) {
                        this.room1Template = room1Template.get();
                        room1Size = this.room1Template.getSize();
                        Deeperdark.LOGGER.info("✓ Loaded Slip room1 template from {} with size: {}x{}x{}",
                                room1TemplateId, room1Size.getX(), room1Size.getY(), room1Size.getZ());
                        break;
                    }
                }

                if (room1Template == null) {
                    Deeperdark.LOGGER.warn("✗ room1 template not found - will only use room0");
                }

                // Load room_hole (special vertical shaft room)
                for (String path : new String[]{"the_slip/room_hole", "structure/the_slip/room_hole"}) {
                    Identifier holeTemplateId = Identifier.of("minecraft", path);
                    Deeperdark.LOGGER.info("Trying to load template: {}", holeTemplateId);
                    Optional<StructureTemplate> holeTemplate = manager.getTemplate(holeTemplateId);

                    if (holeTemplate.isPresent()) {
                        roomHoleTemplate = holeTemplate.get();
                        roomHoleSize = roomHoleTemplate.getSize();
                        Deeperdark.LOGGER.info("✓ Loaded Slip room_hole template from {} with size: {}x{}x{}",
                                holeTemplateId, roomHoleSize.getX(), roomHoleSize.getY(), roomHoleSize.getZ());
                        break;
                    }
                }

                if (roomHoleTemplate == null) {
                    Deeperdark.LOGGER.warn("✗ room_hole template not found - will only use room0");
                }

                templatesLoaded = true;
                Deeperdark.LOGGER.info("Template loading complete. Success: {}", roomTemplate != null);
            } catch (Exception e) {
                Deeperdark.LOGGER.error("Error loading Slip room templates", e);
                roomSize = new Vec3i(DEFAULT_ROOM_SIZE, DEFAULT_ROOM_SIZE, DEFAULT_ROOM_SIZE);
                templatesLoaded = true;
            }
        }
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig,
                      net.minecraft.world.biome.source.BiomeAccess biomeAccess,
                      StructureAccessor structureAccessor, Chunk chunk) {
        // No carving needed for room-based generation
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        // Cache the server reference if we don't have it yet
        if (cachedServer == null) {
            cachedServer = region.getServer();
            Deeperdark.LOGGER.info("Caching server reference in buildSurface: {}", cachedServer != null);
            // Immediately try to load templates once we have the server
            if (cachedServer != null) {
                ensureTemplateLoaded(cachedServer);
                Deeperdark.LOGGER.info("Templates loaded: room0={}, room1={}, hole={}",
                        roomTemplate != null, room1Template != null, roomHoleTemplate != null);

                // If templates just loaded, we should regenerate this chunk if it used fallback
                if (roomTemplate != null && roomSize != null) {
                    Deeperdark.LOGGER.info("Templates now loaded - regenerating chunk terrain");
                    generateRoomBasedTerrain(chunk);
                }
            } else {
                Deeperdark.LOGGER.error("Failed to get server from ChunkRegion!");
            }
        }
        // No surface building needed - we place the entire structure in populateNoise
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        // Entity population handled by structure template
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Blender blender, NoiseConfig noiseConfig,
                                                   StructureAccessor structureAccessor, Chunk chunk) {
        // If templates aren't loaded yet, try to get the server and load them
        if (!templatesLoaded) {
            // Try to get server from multiple sources
            if (cachedServer == null) {
                // Try to get the server from the chunk's world
                try {
                    // Access the world through the chunk
                    var chunkClass = chunk.getClass();
                    var worldField = chunkClass.getDeclaredField("world");
                    worldField.setAccessible(true);
                    var world = worldField.get(chunk);
                    if (world != null) {
                        var worldClass = world.getClass();
                        var getServerMethod = worldClass.getMethod("getServer");
                        cachedServer = (MinecraftServer) getServerMethod.invoke(world);
                        if (cachedServer != null) {
                            Deeperdark.LOGGER.info("Got server reference from chunk in populateNoise");
                        }
                    }
                } catch (Exception e) {
                    // Reflection failed, will try again in buildSurface
                    Deeperdark.LOGGER.debug("Could not get server from chunk via reflection: {}", e.getMessage());
                }
            }

            if (cachedServer != null) {
                ensureTemplateLoaded(cachedServer);
            }
        }

        // Generate terrain with templates
        if (roomTemplate != null && roomSize != null) {
            generateRoomBasedTerrain(chunk);
        } else {
            // Templates aren't loaded - this shouldn't happen but use fallback
            Deeperdark.LOGGER.warn("Templates not loaded in populateNoise! Server cached: {}, Templates loaded flag: {}. Will retry in buildSurface.",
                    cachedServer != null, templatesLoaded);
            fillChunkWithPlaceholder(chunk);
        }

        return CompletableFuture.completedFuture(chunk);
    }

    private void fillChunkWithPlaceholder(Chunk chunk) {
        // Fill with a placeholder block pattern (stone) instead of air
        // This prevents empty air chunks when templates aren't loaded yet
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getBottomY(); y < chunk.getBottomY() + chunk.getHeight(); y++) {
                    // Create a simple pattern
                    if (y < 0) {
                        chunk.setBlockState(new BlockPos(x, y, z), Blocks.DEEPSLATE.getDefaultState(), 0);
                    } else if (y % DEFAULT_ROOM_SIZE < DEFAULT_ROOM_SIZE - 1) {
                        chunk.setBlockState(new BlockPos(x, y, z), Blocks.STONE.getDefaultState(), 0);
                    } else {
                        chunk.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), 0);
                    }
                }
            }
        }
    }

    private void generateRoomBasedTerrain(Chunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkStartX = chunkPos.getStartX();
        int chunkStartZ = chunkPos.getStartZ();

        int roomSizeX = roomSize.getX();
        int roomSizeY = roomSize.getY();
        int roomSizeZ = roomSize.getZ();

        // Calculate which room(s) this chunk overlaps with
        int minRoomX = Math.floorDiv(chunkStartX, roomSizeX);
        int maxRoomX = Math.floorDiv(chunkStartX + 15, roomSizeX);
        int minRoomZ = Math.floorDiv(chunkStartZ, roomSizeZ);
        int maxRoomZ = Math.floorDiv(chunkStartZ + 15, roomSizeZ);

        // For each potentially overlapping room
        for (int roomX = minRoomX; roomX <= maxRoomX; roomX++) {
            for (int roomZ = minRoomZ; roomZ <= maxRoomZ; roomZ++) {
                // Check if this room should be a hole room (rare occurrence)
                if (shouldBeHoleRoom(roomX, roomZ)) {
                    // For hole rooms, fill the entire vertical space with the same room and rotation
                    placeVerticalShaft(chunk, roomX, roomZ);
                } else {
                    // Stack rooms vertically from -64 to 384 (repeat every roomSizeY blocks)
                    // Each vertical layer can be a different room type with different rotation
                    int minY = -64;
                    int maxY = 384;
                    for (int roomY = minY / roomSizeY; roomY < maxY / roomSizeY; roomY++) {
                        // Select room type and rotation for THIS specific room instance
                        StructureTemplate selectedTemplate;
                        Vec3i selectedSize;

                        if (room1Template != null && shouldBeRoom1(roomX, roomY, roomZ)) {
                            selectedTemplate = room1Template;
                            selectedSize = room1Size;
                        } else {
                            selectedTemplate = roomTemplate;
                            selectedSize = roomSize;
                        }

                        // Get rotation for this specific room instance
                        BlockRotation rotation = getRoomRotation(roomX, roomY, roomZ);

                        placeRoomInChunk(chunk, roomX, roomY, roomZ, selectedTemplate, selectedSize, rotation);
                    }
                }
            }
        }
    }

    /**
     * Determines if a room at the given coordinates should be room1.
     * Now includes Y coordinate for vertical variation.
     */
    private boolean shouldBeRoom1(int roomX, int roomY, int roomZ) {
        if (room1Template == null) return false;

        // Use hash function that includes Y coordinate for vertical variation
        long hash = (long) roomX * 668265263L + (long) roomY * 1640531527L + (long) roomZ * 374761393L;
        hash = (hash ^ (hash >>> 17)) * 1274126177L;
        return (hash & 0x7FFFFFFF) % 5 < 2; // ~40% chance (2 out of 5)
    }

    /**
     * Gets the rotation for a specific room based on its coordinates.
     * Returns one of: NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90
     */
    private BlockRotation getRoomRotation(int roomX, int roomY, int roomZ) {
        // Use a hash to deterministically select rotation
        long hash = (long) roomX * 1640531527L + (long) roomY * 668265263L + (long) roomZ * 374761393L;
        hash = (hash ^ (hash >>> 13)) * 1274126177L;

        int rotationIndex = (int) ((hash & 0x7FFFFFFF) % 4);
        return switch (rotationIndex) {
            case 0 -> BlockRotation.NONE;
            case 1 -> BlockRotation.CLOCKWISE_90;
            case 2 -> BlockRotation.CLOCKWISE_180;
            default -> BlockRotation.COUNTERCLOCKWISE_90;
        };
    }

    /**
     * Determines if a room at the given coordinates should be a hole room.
     * Uses a deterministic hash to make the decision consistent across chunk generation.
     */
    private boolean shouldBeHoleRoom(int roomX, int roomZ) {
        if (roomHoleTemplate == null) return false;

        // Use a simple hash function to deterministically decide if this room is a hole
        // This makes hole rooms somewhat rare (approximately 1 in 20)
        long hash = (long) roomX * 374761393L + (long) roomZ * 668265263L;
        hash = (hash ^ (hash >>> 13)) * 1274126177L;
        return (hash & 0x7FFFFFFF) % 20 == 0; // ~5% chance
    }

    /**
     * Places the room_hole template vertically throughout the entire world height,
     * creating a deep pit effect. Uses consistent rotation for the entire shaft.
     */
    private void placeVerticalShaft(Chunk chunk, int roomX, int roomZ) {
        if (roomHoleTemplate == null || roomHoleSize == null) return;

        int holeRoomSizeY = roomHoleSize.getY();

        // Get a consistent rotation for the entire vertical shaft
        BlockRotation rotation = getRoomRotation(roomX, 0, roomZ);

        // Fill the entire vertical space with hole rooms from -64 to 384
        int minY = -64;
        int maxY = 384;
        for (int roomY = minY / holeRoomSizeY; roomY < maxY / holeRoomSizeY + 1; roomY++) {
            placeRoomInChunk(chunk, roomX, roomY, roomZ, roomHoleTemplate, roomHoleSize, rotation);
        }
    }

    private void placeRoomInChunk(Chunk chunk, int roomX, int roomY, int roomZ, StructureTemplate template, Vec3i size, BlockRotation rotation) {
        if (template == null || size == null) return;

        int roomSizeX = size.getX();
        int roomSizeY = size.getY();
        int roomSizeZ = size.getZ();

        // Calculate the world position of this room
        int roomWorldX = roomX * roomSizeX;
        int roomWorldY = roomY * roomSizeY;
        int roomWorldZ = roomZ * roomSizeZ;

        ChunkPos chunkPos = chunk.getPos();
        int chunkStartX = chunkPos.getStartX();
        int chunkStartZ = chunkPos.getStartZ();

        int chunkTopY = chunk.getBottomY() + chunk.getHeight();

        // Use reflection to access the private blockInfoLists field
        try {
            java.lang.reflect.Field field = StructureTemplate.class.getDeclaredField("blockInfoLists");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<StructureTemplate.PalettedBlockInfoList> palettes =
                (List<StructureTemplate.PalettedBlockInfoList>) field.get(template);

            if (palettes.isEmpty()) return;

            // Get all blocks from the first palette
            java.lang.reflect.Method getAllMethod = StructureTemplate.PalettedBlockInfoList.class.getDeclaredMethod("getAll");
            getAllMethod.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<StructureTemplate.StructureBlockInfo> blockInfos =
                (List<StructureTemplate.StructureBlockInfo>) getAllMethod.invoke(palettes.getFirst());

            // Place each block from the template
            for (StructureTemplate.StructureBlockInfo blockInfo : blockInfos) {
                BlockPos templatePos = blockInfo.pos();

                // Apply rotation to the block position
                BlockPos rotatedPos = applyRotation(templatePos, rotation, size);

                int worldX = roomWorldX + rotatedPos.getX();
                int worldY = roomWorldY + rotatedPos.getY();
                int worldZ = roomWorldZ + rotatedPos.getZ();

                // Check if this block is within the current chunk
                if (worldX >= chunkStartX && worldX < chunkStartX + 16 &&
                    worldZ >= chunkStartZ && worldZ < chunkStartZ + 16 &&
                    worldY >= chunk.getBottomY() && worldY < chunkTopY) {

                    BlockState blockState = blockInfo.state();

                    // Apply rotation to the block state
                    blockState = blockState.rotate(rotation);

                    if (blockState != null && !blockState.isAir()) {
                        // Place the block in the chunk (using chunk-relative coordinates)
                        BlockPos chunkRelativePos = new BlockPos(
                                worldX - chunkStartX,
                                worldY,
                                worldZ - chunkStartZ
                        );
                        chunk.setBlockState(chunkRelativePos, blockState, 0);
                    }
                }
            }
        } catch (Exception e) {
            Deeperdark.LOGGER.error("Failed to access structure template data", e);
        }
    }

    /**
     * Applies rotation to a block position within a structure.
     * Rotates around the center of the structure.
     */
    private BlockPos applyRotation(BlockPos pos, BlockRotation rotation, Vec3i size) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        // Rotate based on the rotation type
        return switch (rotation) {
            case CLOCKWISE_90 -> new BlockPos(size.getZ() - 1 - z, y, x);
            case CLOCKWISE_180 -> new BlockPos(size.getX() - 1 - x, y, size.getZ() - 1 - z);
            case COUNTERCLOCKWISE_90 -> new BlockPos(z, y, size.getX() - 1 - x);
            default -> pos; // NONE
        };
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        // Return a reasonable height based on room structure
        return roomSize != null ? roomSize.getY() : DEFAULT_ROOM_SIZE;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        // Simplified implementation - returns air column
        BlockState[] states = new BlockState[world.getHeight()];
        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(world.getBottomY(), states);
    }

    @Override
    public void appendDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        text.add("Slip Room Generator");
        if (roomSize != null) {
            int roomX = Math.floorDiv(pos.getX(), roomSize.getX());
            int roomY = Math.floorDiv(pos.getY(), roomSize.getY());
            int roomZ = Math.floorDiv(pos.getZ(), roomSize.getZ());
            text.add("Room: " + roomX + ", " + roomY + ", " + roomZ);
            text.add("Room Size: " + roomSize.getX() + "x" + roomSize.getY() + "x" + roomSize.getZ());
        }
    }
}

