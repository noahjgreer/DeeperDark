/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudLines
 *  net.minecraft.client.gui.hud.debug.HeightmapDebugHudEntry
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.WorldChunk
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class HeightmapDebugHudEntry
implements DebugHudEntry {
    private static final Map<Heightmap.Type, String> HEIGHTMAP_TYPE_TO_STRING = Maps.newEnumMap(Map.of(Heightmap.Type.WORLD_SURFACE_WG, "SW", Heightmap.Type.WORLD_SURFACE, "S", Heightmap.Type.OCEAN_FLOOR_WG, "OW", Heightmap.Type.OCEAN_FLOOR, "O", Heightmap.Type.MOTION_BLOCKING, "M", Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, "ML"));
    private static final Identifier SECTION_ID = Identifier.ofVanilla((String)"heightmaps");

    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Entity entity = minecraftClient.getCameraEntity();
        if (entity == null || minecraftClient.world == null || clientChunk == null) {
            return;
        }
        BlockPos blockPos = entity.getBlockPos();
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder stringBuilder = new StringBuilder("CH");
        for (Heightmap.Type type : Heightmap.Type.values()) {
            if (!type.shouldSendToClient()) continue;
            stringBuilder.append(" ").append((String)HEIGHTMAP_TYPE_TO_STRING.get(type)).append(": ").append(clientChunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
        }
        list.add(stringBuilder.toString());
        stringBuilder.setLength(0);
        stringBuilder.append("SH");
        for (Heightmap.Type type : Heightmap.Type.values()) {
            if (!type.isStoredServerSide()) continue;
            stringBuilder.append(" ").append((String)HEIGHTMAP_TYPE_TO_STRING.get(type)).append(": ");
            if (chunk != null) {
                stringBuilder.append(chunk.sampleHeightmap(type, blockPos.getX(), blockPos.getZ()));
                continue;
            }
            stringBuilder.append("??");
        }
        list.add(stringBuilder.toString());
        lines.addLinesToSection(SECTION_ID, list);
    }
}

