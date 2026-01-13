/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.TestInstanceBlockEntity
 *  net.minecraft.block.entity.TestInstanceBlockEntity$Error
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.TestInstanceBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.TestInstanceBlockEntityRenderState
 *  net.minecraft.client.render.command.ModelCommandRenderer$CrumblingOverlayCommand
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.StructureBlockBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.TestInstanceBlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TestInstanceBlockEntityRenderer
implements BlockEntityRenderer<TestInstanceBlockEntity, TestInstanceBlockEntityRenderState> {
    private static final float field_62965 = 0.02f;
    private final BeaconBlockEntityRenderer<TestInstanceBlockEntity> beaconBlockEntityRenderer = new BeaconBlockEntityRenderer();
    private final StructureBlockBlockEntityRenderer<TestInstanceBlockEntity> structureBlockBlockEntityRenderer = new StructureBlockBlockEntityRenderer();

    public TestInstanceBlockEntityRenderState createRenderState() {
        return new TestInstanceBlockEntityRenderState();
    }

    public void updateRenderState(TestInstanceBlockEntity testInstanceBlockEntity, TestInstanceBlockEntityRenderState testInstanceBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)testInstanceBlockEntity, (BlockEntityRenderState)testInstanceBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        testInstanceBlockEntityRenderState.beaconState = new BeaconBlockEntityRenderState();
        BlockEntityRenderState.updateBlockEntityRenderState((BlockEntity)testInstanceBlockEntity, (BlockEntityRenderState)testInstanceBlockEntityRenderState.beaconState, (ModelCommandRenderer.CrumblingOverlayCommand)crumblingOverlayCommand);
        BeaconBlockEntityRenderer.updateBeaconRenderState((BlockEntity)testInstanceBlockEntity, (BeaconBlockEntityRenderState)testInstanceBlockEntityRenderState.beaconState, (float)f, (Vec3d)vec3d);
        testInstanceBlockEntityRenderState.structureState = new StructureBlockBlockEntityRenderState();
        BlockEntityRenderState.updateBlockEntityRenderState((BlockEntity)testInstanceBlockEntity, (BlockEntityRenderState)testInstanceBlockEntityRenderState.structureState, (ModelCommandRenderer.CrumblingOverlayCommand)crumblingOverlayCommand);
        StructureBlockBlockEntityRenderer.updateStructureBoxRenderState((BlockEntity)testInstanceBlockEntity, (StructureBlockBlockEntityRenderState)testInstanceBlockEntityRenderState.structureState);
        testInstanceBlockEntityRenderState.errors.clear();
        for (TestInstanceBlockEntity.Error error : testInstanceBlockEntity.getErrors()) {
            testInstanceBlockEntityRenderState.errors.add(new TestInstanceBlockEntity.Error(error.pos(), error.text()));
        }
    }

    public void render(TestInstanceBlockEntityRenderState testInstanceBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        this.beaconBlockEntityRenderer.render(testInstanceBlockEntityRenderState.beaconState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        this.structureBlockBlockEntityRenderer.render(testInstanceBlockEntityRenderState.structureState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        for (TestInstanceBlockEntity.Error error : testInstanceBlockEntityRenderState.errors) {
            this.renderError(error);
        }
    }

    private void renderError(TestInstanceBlockEntity.Error error) {
        BlockPos blockPos = error.pos();
        GizmoDrawing.box((Box)new Box(blockPos).expand((double)0.02f), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.375f, (float)1.0f, (float)0.0f, (float)0.0f)));
        String string = error.text().getString();
        float f = 0.16f;
        GizmoDrawing.text((String)string, (Vec3d)Vec3d.add((Vec3i)blockPos, (double)0.5, (double)1.2, (double)0.5), (TextGizmo.Style)TextGizmo.Style.left().scaled(0.16f)).ignoreOcclusion();
    }

    public boolean rendersOutsideBoundingBox() {
        return this.beaconBlockEntityRenderer.rendersOutsideBoundingBox() || this.structureBlockBlockEntityRenderer.rendersOutsideBoundingBox();
    }

    public int getRenderDistance() {
        return Math.max(this.beaconBlockEntityRenderer.getRenderDistance(), this.structureBlockBlockEntityRenderer.getRenderDistance());
    }

    public boolean isInRenderDistance(TestInstanceBlockEntity testInstanceBlockEntity, Vec3d vec3d) {
        return this.beaconBlockEntityRenderer.isInRenderDistance((BlockEntity)testInstanceBlockEntity, vec3d) || this.structureBlockBlockEntityRenderer.isInRenderDistance((BlockEntity)testInstanceBlockEntity, vec3d);
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

