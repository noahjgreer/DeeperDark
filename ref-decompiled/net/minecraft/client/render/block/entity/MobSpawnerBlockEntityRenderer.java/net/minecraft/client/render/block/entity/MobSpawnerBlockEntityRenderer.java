/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.MobSpawnerBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
implements BlockEntityRenderer<MobSpawnerBlockEntity, MobSpawnerBlockEntityRenderState> {
    private final EntityRenderManager entityRenderDispatcher;

    public MobSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.entityRenderDispatcher();
    }

    @Override
    public MobSpawnerBlockEntityRenderState createRenderState() {
        return new MobSpawnerBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(MobSpawnerBlockEntity mobSpawnerBlockEntity, MobSpawnerBlockEntityRenderState mobSpawnerBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(mobSpawnerBlockEntity, mobSpawnerBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        if (mobSpawnerBlockEntity.getWorld() == null) {
            return;
        }
        MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
        Entity entity = mobSpawnerLogic.getRenderedEntity(mobSpawnerBlockEntity.getWorld(), mobSpawnerBlockEntity.getPos());
        TrialSpawnerBlockEntityRenderer.updateSpawnerRenderState(mobSpawnerBlockEntityRenderState, f, entity, this.entityRenderDispatcher, mobSpawnerLogic.getLastRotation(), mobSpawnerLogic.getRotation());
    }

    @Override
    public void render(MobSpawnerBlockEntityRenderState mobSpawnerBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (mobSpawnerBlockEntityRenderState.displayEntityRenderState != null) {
            MobSpawnerBlockEntityRenderer.renderDisplayEntity(matrixStack, orderedRenderCommandQueue, mobSpawnerBlockEntityRenderState.displayEntityRenderState, this.entityRenderDispatcher, mobSpawnerBlockEntityRenderState.displayEntityRotation, mobSpawnerBlockEntityRenderState.displayEntityScale, cameraRenderState);
        }
    }

    public static void renderDisplayEntity(MatrixStack matrices, OrderedRenderCommandQueue queue, EntityRenderState state, EntityRenderManager entityRenderDispatcher, float rotation, float scale, CameraRenderState cameraRenderState) {
        matrices.push();
        matrices.translate(0.5f, 0.4f, 0.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.translate(0.0f, -0.2f, 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-30.0f));
        matrices.scale(scale, scale, scale);
        entityRenderDispatcher.render(state, cameraRenderState, 0.0, 0.0, 0.0, matrices, queue);
        matrices.pop();
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
