/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.VillageSectionsDebugRenderer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class VillageSectionsDebugRenderer
implements DebugRenderer.Renderer {
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        store.forEachBlockData(DebugSubscriptionTypes.VILLAGE_SECTIONS, (pos, sections) -> {
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from((BlockPos)pos);
            GizmoDrawing.box((BlockPos)chunkSectionPos.getCenterPos(), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.15f, (float)0.2f, (float)1.0f, (float)0.2f)));
        });
    }
}

