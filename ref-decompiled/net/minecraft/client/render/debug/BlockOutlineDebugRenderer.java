/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.BlockOutlineDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
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

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BlockOutlineDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public BlockOutlineDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        World blockView = this.client.player.getEntityWorld();
        BlockPos blockPos = BlockPos.ofFloored((double)cameraX, (double)cameraY, (double)cameraZ);
        for (BlockPos blockPos2 : BlockPos.iterate((BlockPos)blockPos.add(-6, -6, -6), (BlockPos)blockPos.add(6, 6, 6))) {
            BlockState blockState = blockView.getBlockState(blockPos2);
            if (blockState.isOf(Blocks.AIR)) continue;
            VoxelShape voxelShape = blockState.getOutlineShape((BlockView)blockView, blockPos2);
            for (Box box : voxelShape.getBoundingBoxes()) {
                Box box2 = box.offset(blockPos2).expand(0.002);
                int i = -2130771968;
                Vec3d vec3d = box2.getMinPos();
                Vec3d vec3d2 = box2.getMaxPos();
                BlockOutlineDebugRenderer.drawFace((BlockPos)blockPos2, (BlockState)blockState, (BlockView)blockView, (Direction)Direction.WEST, (Vec3d)vec3d, (Vec3d)vec3d2, (int)-2130771968);
                BlockOutlineDebugRenderer.drawFace((BlockPos)blockPos2, (BlockState)blockState, (BlockView)blockView, (Direction)Direction.SOUTH, (Vec3d)vec3d, (Vec3d)vec3d2, (int)-2130771968);
                BlockOutlineDebugRenderer.drawFace((BlockPos)blockPos2, (BlockState)blockState, (BlockView)blockView, (Direction)Direction.EAST, (Vec3d)vec3d, (Vec3d)vec3d2, (int)-2130771968);
                BlockOutlineDebugRenderer.drawFace((BlockPos)blockPos2, (BlockState)blockState, (BlockView)blockView, (Direction)Direction.NORTH, (Vec3d)vec3d, (Vec3d)vec3d2, (int)-2130771968);
                BlockOutlineDebugRenderer.drawFace((BlockPos)blockPos2, (BlockState)blockState, (BlockView)blockView, (Direction)Direction.DOWN, (Vec3d)vec3d, (Vec3d)vec3d2, (int)-2130771968);
                BlockOutlineDebugRenderer.drawFace((BlockPos)blockPos2, (BlockState)blockState, (BlockView)blockView, (Direction)Direction.UP, (Vec3d)vec3d, (Vec3d)vec3d2, (int)-2130771968);
            }
        }
    }

    private static void drawFace(BlockPos pos, BlockState state, BlockView world, Direction direction, Vec3d minPos, Vec3d maxPos, int color) {
        if (state.isSideSolidFullSquare(world, pos, direction)) {
            GizmoDrawing.face((Vec3d)minPos, (Vec3d)maxPos, (Direction)direction, (DrawStyle)DrawStyle.filled((int)color));
        }
    }
}

