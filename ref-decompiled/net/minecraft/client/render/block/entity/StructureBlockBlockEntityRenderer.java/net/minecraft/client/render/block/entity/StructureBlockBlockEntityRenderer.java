/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T, StructureBlockBlockEntityRenderState> {
    public static final int field_63584 = ColorHelper.fromFloats(0.2f, 0.75f, 0.75f, 1.0f);

    @Override
    public StructureBlockBlockEntityRenderState createRenderState() {
        return new StructureBlockBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(T blockEntity, StructureBlockBlockEntityRenderState structureBlockBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, structureBlockBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        StructureBlockBlockEntityRenderer.updateStructureBoxRenderState(blockEntity, structureBlockBlockEntityRenderState);
    }

    public static <T extends BlockEntity> void updateStructureBoxRenderState(T blockEntity, StructureBlockBlockEntityRenderState state) {
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
        state.visible = clientPlayerEntity.isCreativeLevelTwoOp() || clientPlayerEntity.isSpectator();
        state.structureBox = ((StructureBoxRendering)((Object)blockEntity)).getStructureBox();
        state.renderMode = ((StructureBoxRendering)((Object)blockEntity)).getRenderMode();
        BlockPos blockPos = state.structureBox.localPos();
        Vec3i vec3i = state.structureBox.size();
        BlockPos blockPos2 = state.pos;
        BlockPos blockPos3 = blockPos2.add(blockPos);
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

    @Override
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
        GizmoDrawing.box(new Box(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()).offset(structureBlockBlockEntityRenderState.pos), DrawStyle.stroked(ColorHelper.fromFloats(1.0f, 0.9f, 0.9f, 0.9f)), true);
        this.renderInvisibleBlocks(structureBlockBlockEntityRenderState, blockPos, vec3i);
    }

    private void renderInvisibleBlocks(StructureBlockBlockEntityRenderState state, BlockPos pos, Vec3i size) {
        if (state.invisibleBlocks == null) {
            return;
        }
        BlockPos blockPos = state.pos;
        BlockPos blockPos2 = blockPos.add(pos);
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
                        GizmoDrawing.box(box, DrawStyle.stroked(ColorHelper.fromFloats(1.0f, 0.5f, 0.5f, 1.0f)));
                        continue;
                    }
                    if (invisibleRenderType == StructureBlockBlockEntityRenderState.InvisibleRenderType.STRUCTURE_VOID) {
                        GizmoDrawing.box(box, DrawStyle.stroked(ColorHelper.fromFloats(1.0f, 1.0f, 0.75f, 0.75f)));
                        continue;
                    }
                    if (invisibleRenderType == StructureBlockBlockEntityRenderState.InvisibleRenderType.BARRIER) {
                        GizmoDrawing.box(box, DrawStyle.stroked(-65536));
                        continue;
                    }
                    if (invisibleRenderType != StructureBlockBlockEntityRenderState.InvisibleRenderType.LIGHT) continue;
                    GizmoDrawing.box(box, DrawStyle.stroked(-256));
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
                    ((VoxelSet)voxelSet).set(i2, j2, k2);
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
            GizmoDrawing.face(new Vec3d(g, h, l), new Vec3d(m, n, o), direction, DrawStyle.filled(field_63584));
        });
    }

    @Override
    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 96;
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
