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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class BlockOutlineDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public BlockOutlineDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        World blockView = this.client.player.getEntityWorld();
        BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
            BlockState blockState = blockView.getBlockState(blockPos2);
            if (blockState.isOf(Blocks.AIR)) continue;
            VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);
            for (Box box : voxelShape.getBoundingBoxes()) {
                Box box2 = box.offset(blockPos2).expand(0.002);
                int i = -2130771968;
                Vec3d vec3d = box2.getMinPos();
                Vec3d vec3d2 = box2.getMaxPos();
                BlockOutlineDebugRenderer.drawFace(blockPos2, blockState, blockView, Direction.WEST, vec3d, vec3d2, -2130771968);
                BlockOutlineDebugRenderer.drawFace(blockPos2, blockState, blockView, Direction.SOUTH, vec3d, vec3d2, -2130771968);
                BlockOutlineDebugRenderer.drawFace(blockPos2, blockState, blockView, Direction.EAST, vec3d, vec3d2, -2130771968);
                BlockOutlineDebugRenderer.drawFace(blockPos2, blockState, blockView, Direction.NORTH, vec3d, vec3d2, -2130771968);
                BlockOutlineDebugRenderer.drawFace(blockPos2, blockState, blockView, Direction.DOWN, vec3d, vec3d2, -2130771968);
                BlockOutlineDebugRenderer.drawFace(blockPos2, blockState, blockView, Direction.UP, vec3d, vec3d2, -2130771968);
            }
        }
    }

    private static void drawFace(BlockPos pos, BlockState state, BlockView world, Direction direction, Vec3d minPos, Vec3d maxPos, int color) {
        if (state.isSideSolidFullSquare(world, pos, direction)) {
            GizmoDrawing.face(minPos, maxPos, direction, DrawStyle.filled(color));
        }
    }
}
