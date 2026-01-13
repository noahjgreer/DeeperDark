/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.TrialSpawnerBlockEntity
 *  net.minecraft.block.spawner.TrialSpawnerData
 *  net.minecraft.block.spawner.TrialSpawnerLogic
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
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.MobSpawnerBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TrialSpawnerBlockEntityRenderer
implements BlockEntityRenderer<TrialSpawnerBlockEntity, MobSpawnerBlockEntityRenderState> {
    private final EntityRenderManager entityRenderDispatcher;

    public TrialSpawnerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.entityRenderDispatcher = context.entityRenderDispatcher();
    }

    public MobSpawnerBlockEntityRenderState createRenderState() {
        return new MobSpawnerBlockEntityRenderState();
    }

    public void updateRenderState(TrialSpawnerBlockEntity trialSpawnerBlockEntity, MobSpawnerBlockEntityRenderState mobSpawnerBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)trialSpawnerBlockEntity, (BlockEntityRenderState)mobSpawnerBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        if (trialSpawnerBlockEntity.getWorld() == null) {
            return;
        }
        TrialSpawnerLogic trialSpawnerLogic = trialSpawnerBlockEntity.getSpawner();
        TrialSpawnerData trialSpawnerData = trialSpawnerLogic.getData();
        Entity entity = trialSpawnerData.setDisplayEntity(trialSpawnerLogic, trialSpawnerBlockEntity.getWorld(), trialSpawnerLogic.getSpawnerState());
        TrialSpawnerBlockEntityRenderer.updateSpawnerRenderState((MobSpawnerBlockEntityRenderState)mobSpawnerBlockEntityRenderState, (float)f, (Entity)entity, (EntityRenderManager)this.entityRenderDispatcher, (double)trialSpawnerData.getLastDisplayEntityRotation(), (double)trialSpawnerData.getDisplayEntityRotation());
    }

    static void updateSpawnerRenderState(MobSpawnerBlockEntityRenderState state, float tickProgress, @Nullable Entity displayEntity, EntityRenderManager entityRenderDispatcher, double lastDisplayEntityRotation, double displayEntityRotation) {
        if (displayEntity == null) {
            return;
        }
        state.displayEntityRenderState = entityRenderDispatcher.getAndUpdateRenderState(displayEntity, tickProgress);
        state.displayEntityRenderState.light = state.lightmapCoordinates;
        state.displayEntityRotation = (float)MathHelper.lerp((double)tickProgress, (double)lastDisplayEntityRotation, (double)displayEntityRotation) * 10.0f;
        state.displayEntityScale = 0.53125f;
        float f = Math.max(displayEntity.getWidth(), displayEntity.getHeight());
        if ((double)f > 1.0) {
            state.displayEntityScale /= f;
        }
    }

    public void render(MobSpawnerBlockEntityRenderState mobSpawnerBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (mobSpawnerBlockEntityRenderState.displayEntityRenderState != null) {
            MobSpawnerBlockEntityRenderer.renderDisplayEntity((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (EntityRenderState)mobSpawnerBlockEntityRenderState.displayEntityRenderState, (EntityRenderManager)this.entityRenderDispatcher, (float)mobSpawnerBlockEntityRenderState.displayEntityRotation, (float)mobSpawnerBlockEntityRenderState.displayEntityScale, (CameraRenderState)cameraRenderState);
        }
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

