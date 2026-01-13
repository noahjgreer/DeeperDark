/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.SkyLightDebugRenderer
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.LightType
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 */
package net.minecraft.client.render.debug;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LightType;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class SkyLightDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private final boolean visualizeBlockLightLevels;
    private final boolean visualizeSkyLightLevels;
    private static final int RANGE = 10;

    public SkyLightDebugRenderer(MinecraftClient client, boolean visualizeBlockLightLevels, boolean visualizeSkyLightLevels) {
        this.client = client;
        this.visualizeBlockLightLevels = visualizeBlockLightLevels;
        this.visualizeSkyLightLevels = visualizeSkyLightLevels;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        ClientWorld world = this.client.world;
        BlockPos blockPos = BlockPos.ofFloored((double)cameraX, (double)cameraY, (double)cameraZ);
        LongOpenHashSet longSet = new LongOpenHashSet();
        for (BlockPos blockPos2 : BlockPos.iterate((BlockPos)blockPos.add(-10, -10, -10), (BlockPos)blockPos.add(10, 10, 10))) {
            int j;
            int i = world.getLightLevel(LightType.SKY, blockPos2);
            long l = ChunkSectionPos.fromBlockPos((long)blockPos2.asLong());
            if (longSet.add(l)) {
                GizmoDrawing.text((String)world.getChunkManager().getLightingProvider().displaySectionLevel(LightType.SKY, ChunkSectionPos.from((long)l)), (Vec3d)new Vec3d((double)ChunkSectionPos.getOffsetPos((int)ChunkSectionPos.unpackX((long)l), (int)8), (double)ChunkSectionPos.getOffsetPos((int)ChunkSectionPos.unpackY((long)l), (int)8), (double)ChunkSectionPos.getOffsetPos((int)ChunkSectionPos.unpackZ((long)l), (int)8)), (TextGizmo.Style)TextGizmo.Style.left((int)-65536).scaled(4.8f));
            }
            if (i != 15 && this.visualizeSkyLightLevels) {
                j = ColorHelper.lerp((float)((float)i / 15.0f), (int)-16776961, (int)-16711681);
                GizmoDrawing.text((String)String.valueOf(i), (Vec3d)Vec3d.add((Vec3i)blockPos2, (double)0.5, (double)0.25, (double)0.5), (TextGizmo.Style)TextGizmo.Style.left((int)j));
            }
            if (!this.visualizeBlockLightLevels || (j = world.getLightLevel(LightType.BLOCK, blockPos2)) == 0) continue;
            int k = ColorHelper.lerp((float)((float)j / 15.0f), (int)-5636096, (int)-256);
            GizmoDrawing.text((String)String.valueOf(world.getLightLevel(LightType.BLOCK, blockPos2)), (Vec3d)Vec3d.ofCenter((Vec3i)blockPos2), (TextGizmo.Style)TextGizmo.Style.left((int)k));
        }
    }
}

