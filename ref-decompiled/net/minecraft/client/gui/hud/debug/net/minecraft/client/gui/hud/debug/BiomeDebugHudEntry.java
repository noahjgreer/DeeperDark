/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

@Environment(value=EnvType.CLIENT)
public class BiomeDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla("biome");

    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Entity entity = minecraftClient.getCameraEntity();
        if (entity == null || minecraftClient.world == null) {
            return;
        }
        BlockPos blockPos = entity.getBlockPos();
        if (minecraftClient.world.isInHeightLimit(blockPos.getY())) {
            if (SharedConstants.SHOW_SERVER_DEBUG_VALUES && world instanceof ServerWorld) {
                lines.addLinesToSection(SECTION_ID, List.of("Biome: " + BiomeDebugHudEntry.getBiomeAsString(minecraftClient.world.getBiome(blockPos)), "Server Biome: " + BiomeDebugHudEntry.getBiomeAsString(world.getBiome(blockPos))));
            } else {
                lines.addLine("Biome: " + BiomeDebugHudEntry.getBiomeAsString(minecraftClient.world.getBiome(blockPos)));
            }
        }
    }

    private static String getBiomeAsString(RegistryEntry<Biome> biome) {
        return (String)biome.getKeyOrValue().map(key -> key.getValue().toString(), value -> "[unregistered " + String.valueOf(value) + "]");
    }
}
