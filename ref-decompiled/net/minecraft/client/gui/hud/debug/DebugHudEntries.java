/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.debug.BiomeDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.ChunkGenerationStatsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.ChunkRenderStatsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.ChunkSourceStatsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudEntries
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility
 *  net.minecraft.client.gui.hud.debug.DebugProfileType
 *  net.minecraft.client.gui.hud.debug.EntityRenderStatsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.EntitySpawnCountsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.FpsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.GameVersionDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.GpuUtilizationDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.HeightmapDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.LightLevelsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.LocalDifficultyDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.LookingAtBlockDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.LookingAtEntityDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.LookingAtFluidDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.MemoryDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.ParticleRenderStatsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.PlayerPositionDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.PlayerSectionPositionDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.PostEffectDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.RendererDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.SimplePerformanceImpactorsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.SoundMoodDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.SystemSpecsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.TpsDebugHudEntry
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.debug.BiomeDebugHudEntry;
import net.minecraft.client.gui.hud.debug.ChunkGenerationStatsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.ChunkRenderStatsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.ChunkSourceStatsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugProfileType;
import net.minecraft.client.gui.hud.debug.EntityRenderStatsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.EntitySpawnCountsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.FpsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.GameVersionDebugHudEntry;
import net.minecraft.client.gui.hud.debug.GpuUtilizationDebugHudEntry;
import net.minecraft.client.gui.hud.debug.HeightmapDebugHudEntry;
import net.minecraft.client.gui.hud.debug.LightLevelsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.LocalDifficultyDebugHudEntry;
import net.minecraft.client.gui.hud.debug.LookingAtBlockDebugHudEntry;
import net.minecraft.client.gui.hud.debug.LookingAtEntityDebugHudEntry;
import net.minecraft.client.gui.hud.debug.LookingAtFluidDebugHudEntry;
import net.minecraft.client.gui.hud.debug.MemoryDebugHudEntry;
import net.minecraft.client.gui.hud.debug.ParticleRenderStatsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.PlayerPositionDebugHudEntry;
import net.minecraft.client.gui.hud.debug.PlayerSectionPositionDebugHudEntry;
import net.minecraft.client.gui.hud.debug.PostEffectDebugHudEntry;
import net.minecraft.client.gui.hud.debug.RendererDebugHudEntry;
import net.minecraft.client.gui.hud.debug.SimplePerformanceImpactorsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.SoundMoodDebugHudEntry;
import net.minecraft.client.gui.hud.debug.SystemSpecsDebugHudEntry;
import net.minecraft.client.gui.hud.debug.TpsDebugHudEntry;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DebugHudEntries {
    private static final Map<Identifier, DebugHudEntry> ENTRIES = new HashMap();
    public static final Identifier GAME_VERSION = DebugHudEntries.registerVanilla((String)"game_version", (DebugHudEntry)new GameVersionDebugHudEntry());
    public static final Identifier FPS = DebugHudEntries.registerVanilla((String)"fps", (DebugHudEntry)new FpsDebugHudEntry());
    public static final Identifier TPS = DebugHudEntries.registerVanilla((String)"tps", (DebugHudEntry)new TpsDebugHudEntry());
    public static final Identifier MEMORY = DebugHudEntries.registerVanilla((String)"memory", (DebugHudEntry)new MemoryDebugHudEntry());
    public static final Identifier SYSTEM_SPECS = DebugHudEntries.registerVanilla((String)"system_specs", (DebugHudEntry)new SystemSpecsDebugHudEntry());
    public static final Identifier LOOKING_AT_BLOCK = DebugHudEntries.registerVanilla((String)"looking_at_block", (DebugHudEntry)new LookingAtBlockDebugHudEntry());
    public static final Identifier LOOKING_AT_FLUID = DebugHudEntries.registerVanilla((String)"looking_at_fluid", (DebugHudEntry)new LookingAtFluidDebugHudEntry());
    public static final Identifier LOOKING_AT_ENTITY = DebugHudEntries.registerVanilla((String)"looking_at_entity", (DebugHudEntry)new LookingAtEntityDebugHudEntry());
    public static final Identifier CHUNK_RENDER_STATS = DebugHudEntries.registerVanilla((String)"chunk_render_stats", (DebugHudEntry)new ChunkRenderStatsDebugHudEntry());
    public static final Identifier CHUNK_GENERATION_STATS = DebugHudEntries.registerVanilla((String)"chunk_generation_stats", (DebugHudEntry)new ChunkGenerationStatsDebugHudEntry());
    public static final Identifier ENTITY_RENDER_STATS = DebugHudEntries.registerVanilla((String)"entity_render_stats", (DebugHudEntry)new EntityRenderStatsDebugHudEntry());
    public static final Identifier PARTICLE_RENDER_STATS = DebugHudEntries.registerVanilla((String)"particle_render_stats", (DebugHudEntry)new ParticleRenderStatsDebugHudEntry());
    public static final Identifier CHUNK_SOURCE_STATS = DebugHudEntries.registerVanilla((String)"chunk_source_stats", (DebugHudEntry)new ChunkSourceStatsDebugHudEntry());
    public static final Identifier PLAYER_POSITION = DebugHudEntries.registerVanilla((String)"player_position", (DebugHudEntry)new PlayerPositionDebugHudEntry());
    public static final Identifier PLAYER_SECTION_POSITION = DebugHudEntries.registerVanilla((String)"player_section_position", (DebugHudEntry)new PlayerSectionPositionDebugHudEntry());
    public static final Identifier LIGHT_LEVELS = DebugHudEntries.registerVanilla((String)"light_levels", (DebugHudEntry)new LightLevelsDebugHudEntry());
    public static final Identifier HEIGHTMAP = DebugHudEntries.registerVanilla((String)"heightmap", (DebugHudEntry)new HeightmapDebugHudEntry());
    public static final Identifier BIOME = DebugHudEntries.registerVanilla((String)"biome", (DebugHudEntry)new BiomeDebugHudEntry());
    public static final Identifier LOCAL_DIFFICULTY = DebugHudEntries.registerVanilla((String)"local_difficulty", (DebugHudEntry)new LocalDifficultyDebugHudEntry());
    public static final Identifier ENTITY_SPAWN_COUNTS = DebugHudEntries.registerVanilla((String)"entity_spawn_counts", (DebugHudEntry)new EntitySpawnCountsDebugHudEntry());
    public static final Identifier SOUND_MOOD = DebugHudEntries.registerVanilla((String)"sound_mood", (DebugHudEntry)new SoundMoodDebugHudEntry());
    public static final Identifier POST_EFFECT = DebugHudEntries.registerVanilla((String)"post_effect", (DebugHudEntry)new PostEffectDebugHudEntry());
    public static final Identifier ENTITY_HITBOXES = DebugHudEntries.registerVanilla((String)"entity_hitboxes", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier CHUNK_BORDERS = DebugHudEntries.registerVanilla((String)"chunk_borders", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier THREE_DIMENSIONAL_CROSSHAIR = DebugHudEntries.registerVanilla((String)"3d_crosshair", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier CHUNK_SECTION_PATHS = DebugHudEntries.registerVanilla((String)"chunk_section_paths", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier GPU_UTILIZATION = DebugHudEntries.registerVanilla((String)"gpu_utilization", (DebugHudEntry)new GpuUtilizationDebugHudEntry());
    public static final Identifier SIMPLE_PERFORMANCE_IMPACTORS = DebugHudEntries.registerVanilla((String)"simple_performance_impactors", (DebugHudEntry)new SimplePerformanceImpactorsDebugHudEntry());
    public static final Identifier CHUNK_SECTION_OCTREE = DebugHudEntries.registerVanilla((String)"chunk_section_octree", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_WATER_LEVELS = DebugHudEntries.registerVanilla((String)"visualize_water_levels", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_HEIGHTMAP = DebugHudEntries.registerVanilla((String)"visualize_heightmap", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_COLLISION_BOXES = DebugHudEntries.registerVanilla((String)"visualize_collision_boxes", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_ENTITY_SUPPORTING_BLOCKS = DebugHudEntries.registerVanilla((String)"visualize_entity_supporting_blocks", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_BLOCK_LIGHT_LEVELS = DebugHudEntries.registerVanilla((String)"visualize_block_light_levels", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_SKY_LIGHT_LEVELS = DebugHudEntries.registerVanilla((String)"visualize_sky_light_levels", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_SOLID_FACES = DebugHudEntries.registerVanilla((String)"visualize_solid_faces", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_CHUNKS_ON_SERVER = DebugHudEntries.registerVanilla((String)"visualize_chunks_on_server", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier VISUALIZE_SKY_LIGHT_SECTIONS = DebugHudEntries.registerVanilla((String)"visualize_sky_light_sections", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Identifier CHUNK_SECTION_VISIBILITY = DebugHudEntries.registerVanilla((String)"chunk_section_visibility", (DebugHudEntry)new RendererDebugHudEntry());
    public static final Map<DebugProfileType, Map<Identifier, DebugHudEntryVisibility>> PROFILES;

    private static Identifier registerVanilla(String id, DebugHudEntry entry) {
        return DebugHudEntries.register((Identifier)Identifier.ofVanilla((String)id), (DebugHudEntry)entry);
    }

    public static Identifier register(Identifier id, DebugHudEntry entry) {
        ENTRIES.put(id, entry);
        return id;
    }

    public static Map<Identifier, DebugHudEntry> getEntries() {
        return Map.copyOf(ENTRIES);
    }

    public static @Nullable DebugHudEntry get(Identifier id) {
        return (DebugHudEntry)ENTRIES.get(id);
    }

    static {
        Map<Identifier, DebugHudEntryVisibility> map = Map.of(THREE_DIMENSIONAL_CROSSHAIR, DebugHudEntryVisibility.IN_OVERLAY, GAME_VERSION, DebugHudEntryVisibility.IN_OVERLAY, TPS, DebugHudEntryVisibility.IN_OVERLAY, FPS, DebugHudEntryVisibility.IN_OVERLAY, MEMORY, DebugHudEntryVisibility.IN_OVERLAY, SYSTEM_SPECS, DebugHudEntryVisibility.IN_OVERLAY, PLAYER_POSITION, DebugHudEntryVisibility.IN_OVERLAY, PLAYER_SECTION_POSITION, DebugHudEntryVisibility.IN_OVERLAY, SIMPLE_PERFORMANCE_IMPACTORS, DebugHudEntryVisibility.IN_OVERLAY);
        Map<Identifier, DebugHudEntryVisibility> map2 = Map.of(TPS, DebugHudEntryVisibility.IN_OVERLAY, FPS, DebugHudEntryVisibility.ALWAYS_ON, GPU_UTILIZATION, DebugHudEntryVisibility.IN_OVERLAY, MEMORY, DebugHudEntryVisibility.IN_OVERLAY, SIMPLE_PERFORMANCE_IMPACTORS, DebugHudEntryVisibility.IN_OVERLAY);
        PROFILES = Map.of(DebugProfileType.DEFAULT, map, DebugProfileType.PERFORMANCE, map2);
    }
}

