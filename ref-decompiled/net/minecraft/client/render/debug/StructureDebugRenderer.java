/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.StructureDebugRenderer
 *  net.minecraft.util.math.BlockBox
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.data.StructureDebugData
 *  net.minecraft.world.debug.data.StructureDebugData$Piece
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.data.StructureDebugData;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class StructureDebugRenderer
implements DebugRenderer.Renderer {
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        store.forEachChunkData(DebugSubscriptionTypes.STRUCTURES, (chunkPos, structures) -> {
            for (StructureDebugData structureDebugData : structures) {
                GizmoDrawing.box((Box)Box.from((BlockBox)structureDebugData.boundingBox()), (DrawStyle)DrawStyle.stroked((int)ColorHelper.fromFloats((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f)));
                for (StructureDebugData.Piece piece : structureDebugData.pieces()) {
                    if (piece.isStart()) {
                        GizmoDrawing.box((Box)Box.from((BlockBox)piece.boundingBox()), (DrawStyle)DrawStyle.stroked((int)ColorHelper.fromFloats((float)1.0f, (float)0.0f, (float)1.0f, (float)0.0f)));
                        continue;
                    }
                    GizmoDrawing.box((Box)Box.from((BlockBox)piece.boundingBox()), (DrawStyle)DrawStyle.stroked((int)ColorHelper.fromFloats((float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f)));
                }
            }
        });
    }
}

