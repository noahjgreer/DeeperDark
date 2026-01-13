/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.debug.ChunkGenerationStatsDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudLines
 *  net.minecraft.entity.Entity
 *  net.minecraft.server.world.ServerChunkManager
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.source.BiomeSource
 *  net.minecraft.world.biome.source.util.MultiNoiseUtil$MultiNoiseSampler
 *  net.minecraft.world.chunk.WorldChunk
 *  net.minecraft.world.gen.chunk.ChunkGenerator
 *  net.minecraft.world.gen.noise.NoiseConfig
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkGenerationStatsDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla((String)"chunk_generation");

    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        ServerWorld serverWorld;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Entity entity = minecraftClient.getCameraEntity();
        ServerWorld serverWorld2 = serverWorld = world instanceof ServerWorld ? (ServerWorld)world : null;
        if (entity == null || serverWorld == null) {
            return;
        }
        BlockPos blockPos = entity.getBlockPos();
        ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
        ArrayList<String> list = new ArrayList<String>();
        ChunkGenerator chunkGenerator = serverChunkManager.getChunkGenerator();
        NoiseConfig noiseConfig = serverChunkManager.getNoiseConfig();
        chunkGenerator.appendDebugHudText(list, noiseConfig, blockPos);
        MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler = noiseConfig.getMultiNoiseSampler();
        BiomeSource biomeSource = chunkGenerator.getBiomeSource();
        biomeSource.addDebugInfo(list, blockPos, multiNoiseSampler);
        if (chunk != null && chunk.usesOldNoise()) {
            list.add("Blending: Old");
        }
        lines.addLinesToSection(SECTION_ID, list);
    }
}

