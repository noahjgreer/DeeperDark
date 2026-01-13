/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        ClientWorld world = this.client.world;
        BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
        LongOpenHashSet longSet = new LongOpenHashSet();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            int j;
            int i = world.getLightLevel(LightType.SKY, blockPos2);
            long l = ChunkSectionPos.fromBlockPos(blockPos2.asLong());
            if (longSet.add(l)) {
                GizmoDrawing.text(world.getChunkManager().getLightingProvider().displaySectionLevel(LightType.SKY, ChunkSectionPos.from(l)), new Vec3d(ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackX(l), 8), ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackY(l), 8), ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackZ(l), 8)), TextGizmo.Style.left(-65536).scaled(4.8f));
            }
            if (i != 15 && this.visualizeSkyLightLevels) {
                j = ColorHelper.lerp((float)i / 15.0f, -16776961, -16711681);
                GizmoDrawing.text(String.valueOf(i), Vec3d.add(blockPos2, 0.5, 0.25, 0.5), TextGizmo.Style.left(j));
            }
            if (!this.visualizeBlockLightLevels || (j = world.getLightLevel(LightType.BLOCK, blockPos2)) == 0) continue;
            int k = ColorHelper.lerp((float)j / 15.0f, -5636096, -256);
            GizmoDrawing.text(String.valueOf(world.getLightLevel(LightType.BLOCK, blockPos2)), Vec3d.ofCenter(blockPos2), TextGizmo.Style.left(k));
        }
    }
}
