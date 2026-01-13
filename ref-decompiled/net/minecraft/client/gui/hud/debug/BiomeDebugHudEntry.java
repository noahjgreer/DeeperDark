/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.debug.BiomeDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudLines
 *  net.minecraft.entity.Entity
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.chunk.WorldChunk
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BiomeDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla((String)"biome");

    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Entity entity = minecraftClient.getCameraEntity();
        if (entity == null || minecraftClient.world == null) {
            return;
        }
        BlockPos blockPos = entity.getBlockPos();
        if (minecraftClient.world.isInHeightLimit(blockPos.getY())) {
            if (SharedConstants.SHOW_SERVER_DEBUG_VALUES && world instanceof ServerWorld) {
                lines.addLinesToSection(SECTION_ID, List.of("Biome: " + BiomeDebugHudEntry.getBiomeAsString((RegistryEntry)minecraftClient.world.getBiome(blockPos)), "Server Biome: " + BiomeDebugHudEntry.getBiomeAsString((RegistryEntry)world.getBiome(blockPos))));
            } else {
                lines.addLine("Biome: " + BiomeDebugHudEntry.getBiomeAsString((RegistryEntry)minecraftClient.world.getBiome(blockPos)));
            }
        }
    }

    private static String getBiomeAsString(RegistryEntry<Biome> biome) {
        return (String)biome.getKeyOrValue().map(key -> key.getValue().toString(), value -> "[unregistered " + String.valueOf(value) + "]");
    }
}

