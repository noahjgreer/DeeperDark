/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.WaterDebugRenderer
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class WaterDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public WaterDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        FluidState fluidState;
        BlockPos blockPos = this.client.player.getBlockPos();
        World worldView = this.client.player.getEntityWorld();
        for (BlockPos blockPos2 : BlockPos.iterate((BlockPos)blockPos.add(-10, -10, -10), (BlockPos)blockPos.add(10, 10, 10))) {
            fluidState = worldView.getFluidState(blockPos2);
            if (!fluidState.isIn(FluidTags.WATER)) continue;
            double d = (float)blockPos2.getY() + fluidState.getHeight((BlockView)worldView, blockPos2);
            GizmoDrawing.box((Box)new Box((double)((float)blockPos2.getX() + 0.01f), (double)((float)blockPos2.getY() + 0.01f), (double)((float)blockPos2.getZ() + 0.01f), (double)((float)blockPos2.getX() + 0.99f), d, (double)((float)blockPos2.getZ() + 0.99f)), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.15f, (float)0.0f, (float)1.0f, (float)0.0f)));
        }
        for (BlockPos blockPos2 : BlockPos.iterate((BlockPos)blockPos.add(-10, -10, -10), (BlockPos)blockPos.add(10, 10, 10))) {
            fluidState = worldView.getFluidState(blockPos2);
            if (!fluidState.isIn(FluidTags.WATER)) continue;
            GizmoDrawing.text((String)String.valueOf(fluidState.getLevel()), (Vec3d)Vec3d.add((Vec3i)blockPos2, (double)0.5, (double)fluidState.getHeight((BlockView)worldView, blockPos2), (double)0.5), (TextGizmo.Style)TextGizmo.Style.left((int)-16777216));
        }
    }
}

