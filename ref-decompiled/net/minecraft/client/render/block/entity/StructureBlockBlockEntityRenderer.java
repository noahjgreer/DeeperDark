/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.StructureBoxRendering
 *  net.minecraft.block.entity.StructureBoxRendering$RenderMode
 *  net.minecraft.block.entity.StructureBoxRendering$StructureBox
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState$InvisibleRenderType
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.shape.BitSetVoxelSet
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBoxRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T, StructureBlockBlockEntityRenderState> {
    public static final int field_63584 = ColorHelper.fromFloats((float)0.2f, (float)0.75f, (float)0.75f, (float)1.0f);

    public StructureBlockBlockEntityRenderState createRenderState() {
        return new StructureBlockBlockEntityRenderState();
    }

    public void updateRenderState(T blockEntity, StructureBlockBlockEntityRenderState structureBlockBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState(blockEntity, (BlockEntityRenderState)structureBlockBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        StructureBlockBlockEntityRenderer.updateStructureBoxRenderState(blockEntity, (StructureBlockBlockEntityRenderState)structureBlockBlockEntityRenderState);
    }

    public static <T extends BlockEntity> void updateStructureBoxRenderState(T blockEntity, StructureBlockBlockEntityRenderState state) {
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        state.visible = clientPlayerEntity.isCreativeLevelTwoOp() || clientPlayerEntity.isSpectator();
        state.structureBox = ((StructureBoxRendering)blockEntity).getStructureBox();
        state.renderMode = ((StructureBoxRendering)blockEntity).getRenderMode();
        BlockPos blockPos = state.structureBox.localPos();
        Vec3i vec3i = state.structureBox.size();
        BlockPos blockPos2 = state.pos;
        BlockPos blockPos3 = blockPos2.add((Vec3i)blockPos);
        if (state.visible && blockEntity.getWorld() != null && state.renderMode == StructureBoxRendering.RenderMode.BOX_AND_INVISIBLE_BLOCKS) {
            state.invisibleBlocks = new StructureBlockBlockEntityRenderState.InvisibleRenderType[vec3i.getX() * vec3i.getY() * vec3i.getZ()];
            for (int i = 0; i < vec3i.getX(); ++i) {
                for (int j = 0; j < vec3i.getY(); ++j) {
                    for (int k = 0; k < vec3i.getZ(); ++k) {
                        int l = k * vec3i.getX() * vec3i.getY() + j * vec3i.getX() + i;
                        BlockState blockState = blockEntity.getWorld().getBlockState(blockPos3.add(i, j, k));
                        if (blockState.isAir()) {
                            state.invisibleBlocks[l] = StructureBlockBlockEntityRenderState.InvisibleRenderType.AIR;
                            continue;
                        }
                        if (blockState.isOf(Blocks.STRUCTURE_VOID)) {
                            state.invisibleBlocks[l] = StructureBlockBlockEntityRenderState.InvisibleRenderType.STRUCTURE_VOID;
                            continue;
                        }
                        if (blockState.isOf(Blocks.BARRIER)) {
                            state.invisibleBlocks[l] = StructureBlockBlockEntityRenderState.InvisibleRenderType.BARRIER;
                            continue;
                        }
                        if (!blockState.isOf(Blocks.LIGHT)) continue;
                        state.invisibleBlocks[l] = StructureBlockBlockEntityRenderState.InvisibleRenderType.LIGHT;
                    }
                }
            }
        } else {
            state.invisibleBlocks = null;
        }
        if (state.visible) {
            // empty if block
        }
        state.field_62682 = null;
    }

    public void render(StructureBlockBlockEntityRenderState structureBlockBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (!structureBlockBlockEntityRenderState.visible) {
            return;
        }
        StructureBoxRendering.RenderMode renderMode = structureBlockBlockEntityRenderState.renderMode;
        if (renderMode == StructureBoxRendering.RenderMode.NONE) {
            return;
        }
        StructureBoxRendering.StructureBox structureBox = structureBlockBlockEntityRenderState.structureBox;
        BlockPos blockPos = structureBox.localPos();
        Vec3i vec3i = structureBox.size();
        if (vec3i.getX() < 1 || vec3i.getY() < 1 || vec3i.getZ() < 1) {
            return;
        }
        float f = 1.0f;
        float g = 0.9f;
        BlockPos blockPos2 = blockPos.add(vec3i);
        GizmoDrawing.box((Box)new Box((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ()).offset(structureBlockBlockEntityRenderState.pos), (DrawStyle)DrawStyle.stroked((int)ColorHelper.fromFloats((float)1.0f, (float)0.9f, (float)0.9f, (float)0.9f)), (boolean)true);
        this.renderInvisibleBlocks(structureBlockBlockEntityRenderState, blockPos, vec3i);
    }

    private void renderInvisibleBlocks(StructureBlockBlockEntityRenderState state, BlockPos pos, Vec3i size) {
        if (state.invisibleBlocks == null) {
            return;
        }
        BlockPos blockPos = state.pos;
        BlockPos blockPos2 = blockPos.add((Vec3i)pos);
        for (int i = 0; i < size.getX(); ++i) {
            for (int j = 0; j < size.getY(); ++j) {
                for (int k = 0; k < size.getZ(); ++k) {
                    int l = k * size.getX() * size.getY() + j * size.getX() + i;
                    StructureBlockBlockEntityRenderState.InvisibleRenderType invisibleRenderType = state.invisibleBlocks[l];
                    if (invisibleRenderType == null) continue;
                    float f = invisibleRenderType == StructureBlockBlockEntityRenderState.InvisibleRenderType.AIR ? 0.05f : 0.0f;
                    double d = (float)(blockPos2.getX() + i) + 0.45f - f;
                    double e = (float)(blockPos2.getY() + j) + 0.45f - f;
                    double g = (float)(blockPos2.getZ() + k) + 0.45f - f;
                    double h = (float)(blockPos2.getX() + i) + 0.55f + f;
                    double m = (float)(blockPos2.getY() + j) + 0.55f + f;
                    double n = (float)(blockPos2.getZ() + k) + 0.55f + f;
                    Box box = new Box(d, e, g, h, m, n);
                    if (invisibleRenderType == StructureBlockBlockEntityRenderState.InvisibleRenderType.AIR) {
                        GizmoDrawing.box((Box)box, (DrawStyle)DrawStyle.stroked((int)ColorHelper.fromFloats((float)1.0f, (float)0.5f, (float)0.5f, (float)1.0f)));
                        continue;
                    }
                    if (invisibleRenderType == StructureBlockBlockEntityRenderState.InvisibleRenderType.STRUCTURE_VOID) {
                        GizmoDrawing.box((Box)box, (DrawStyle)DrawStyle.stroked((int)ColorHelper.fromFloats((float)1.0f, (float)1.0f, (float)0.75f, (float)0.75f)));
                        continue;
                    }
                    if (invisibleRenderType == StructureBlockBlockEntityRenderState.InvisibleRenderType.BARRIER) {
                        GizmoDrawing.box((Box)box, (DrawStyle)DrawStyle.stroked((int)-65536));
                        continue;
                    }
                    if (invisibleRenderType != StructureBlockBlockEntityRenderState.InvisibleRenderType.LIGHT) continue;
                    GizmoDrawing.box((Box)box, (DrawStyle)DrawStyle.stroked((int)-256));
                }
            }
        }
    }

    private void renderStructureVoids(StructureBlockBlockEntityRenderState state, BlockPos pos, Vec3i size) {
        if (state.field_62682 == null) {
            return;
        }
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(size.getX(), size.getY(), size.getZ());
        for (int i2 = 0; i2 < size.getX(); ++i2) {
            for (int j2 = 0; j2 < size.getY(); ++j2) {
                for (int k2 = 0; k2 < size.getZ(); ++k2) {
                    int l = k2 * size.getX() * size.getY() + j2 * size.getX() + i2;
                    if (!state.field_62682[l]) continue;
                    voxelSet.set(i2, j2, k2);
                }
            }
        }
        voxelSet.forEachDirection((direction, i, j, k) -> {
            float f = 0.48f;
            float g = (float)(i + pos.getX()) + 0.5f - 0.48f;
            float h = (float)(j + pos.getY()) + 0.5f - 0.48f;
            float l = (float)(k + pos.getZ()) + 0.5f - 0.48f;
            float m = (float)(i + pos.getX()) + 0.5f + 0.48f;
            float n = (float)(j + pos.getY()) + 0.5f + 0.48f;
            float o = (float)(k + pos.getZ()) + 0.5f + 0.48f;
            GizmoDrawing.face((Vec3d)new Vec3d((double)g, (double)h, (double)l), (Vec3d)new Vec3d((double)m, (double)n, (double)o), (Direction)direction, (DrawStyle)DrawStyle.filled((int)field_63584));
        });
    }

    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    public int getRenderDistance() {
        return 96;
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

