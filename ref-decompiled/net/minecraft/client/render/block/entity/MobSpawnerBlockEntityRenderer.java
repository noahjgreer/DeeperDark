/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.MobSpawnerBlockEntity
 *  net.minecraft.block.spawner.MobSpawnerLogic
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.TrialSpawnerBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.MobSpawnerBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderManager
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
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

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class MobSpawnerBlockEntityRenderer
implements BlockEntityRenderer<MobSpawnerBlockEntity, MobSpawnerBlockEntityRenderState> {
    private final EntityRenderManager entityRenderDispatcher;

    public MobSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.entityRenderDispatcher = ctx.entityRenderDispatcher();
    }

    public MobSpawnerBlockEntityRenderState createRenderState() {
        return new MobSpawnerBlockEntityRenderState();
    }

    public void updateRenderState(MobSpawnerBlockEntity mobSpawnerBlockEntity, MobSpawnerBlockEntityRenderState mobSpawnerBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)mobSpawnerBlockEntity, (BlockEntityRenderState)mobSpawnerBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        if (mobSpawnerBlockEntity.getWorld() == null) {
            return;
        }
        MobSpawnerLogic mobSpawnerLogic = mobSpawnerBlockEntity.getLogic();
        Entity entity = mobSpawnerLogic.getRenderedEntity(mobSpawnerBlockEntity.getWorld(), mobSpawnerBlockEntity.getPos());
        TrialSpawnerBlockEntityRenderer.updateSpawnerRenderState((MobSpawnerBlockEntityRenderState)mobSpawnerBlockEntityRenderState, (float)f, (Entity)entity, (EntityRenderManager)this.entityRenderDispatcher, (double)mobSpawnerLogic.getLastRotation(), (double)mobSpawnerLogic.getRotation());
    }

    public void render(MobSpawnerBlockEntityRenderState mobSpawnerBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (mobSpawnerBlockEntityRenderState.displayEntityRenderState != null) {
            MobSpawnerBlockEntityRenderer.renderDisplayEntity((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (EntityRenderState)mobSpawnerBlockEntityRenderState.displayEntityRenderState, (EntityRenderManager)this.entityRenderDispatcher, (float)mobSpawnerBlockEntityRenderState.displayEntityRotation, (float)mobSpawnerBlockEntityRenderState.displayEntityScale, (CameraRenderState)cameraRenderState);
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

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

