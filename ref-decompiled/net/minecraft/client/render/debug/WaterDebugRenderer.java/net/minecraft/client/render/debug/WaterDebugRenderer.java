/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        FluidState fluidState;
        BlockPos blockPos = this.client.player.getBlockPos();
        World worldView = this.client.player.getEntityWorld();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            fluidState = worldView.getFluidState(blockPos2);
            if (!fluidState.isIn(FluidTags.WATER)) continue;
            double d = (float)blockPos2.getY() + fluidState.getHeight(worldView, blockPos2);
            GizmoDrawing.box(new Box((float)blockPos2.getX() + 0.01f, (float)blockPos2.getY() + 0.01f, (float)blockPos2.getZ() + 0.01f, (float)blockPos2.getX() + 0.99f, d, (float)blockPos2.getZ() + 0.99f), DrawStyle.filled(ColorHelper.fromFloats(0.15f, 0.0f, 1.0f, 0.0f)));
        }
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            fluidState = worldView.getFluidState(blockPos2);
            if (!fluidState.isIn(FluidTags.WATER)) continue;
            GizmoDrawing.text(String.valueOf(fluidState.getLevel()), Vec3d.add(blockPos2, 0.5, fluidState.getHeight(worldView, blockPos2), 0.5), TextGizmo.Style.left(-16777216));
        }
    }
}
