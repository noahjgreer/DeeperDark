/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.LightmapTextureManager
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.DisplayEntityRenderer
 *  net.minecraft.client.render.entity.DisplayEntityRenderer$1
 *  net.minecraft.client.render.entity.EntityRenderManager
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.state.DisplayEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.DisplayEntity
 *  net.minecraft.entity.decoration.DisplayEntity$RenderState
 *  net.minecraft.util.math.AffineTransformation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.DisplayEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class DisplayEntityRenderer<T extends DisplayEntity, S, ST extends DisplayEntityRenderState>
extends EntityRenderer<T, ST> {
    private final EntityRenderManager renderDispatcher;

    protected DisplayEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.renderDispatcher = context.getRenderDispatcher();
    }

    protected Box getBoundingBox(T displayEntity) {
        return displayEntity.getVisibilityBoundingBox();
    }

    protected boolean canBeCulled(T displayEntity) {
        return displayEntity.shouldRender();
    }

    private static int getBrightnessOverride(DisplayEntity entity) {
        DisplayEntity.RenderState renderState = entity.getRenderState();
        return renderState != null ? renderState.brightnessOverride() : -1;
    }

    protected int getSkyLight(T displayEntity, BlockPos blockPos) {
        int i = DisplayEntityRenderer.getBrightnessOverride(displayEntity);
        if (i != -1) {
            return LightmapTextureManager.getSkyLightCoordinates((int)i);
        }
        return super.getSkyLight(displayEntity, blockPos);
    }

    protected int getBlockLight(T displayEntity, BlockPos blockPos) {
        int i = DisplayEntityRenderer.getBrightnessOverride(displayEntity);
        if (i != -1) {
            return LightmapTextureManager.getBlockLightCoordinates((int)i);
        }
        return super.getBlockLight(displayEntity, blockPos);
    }

    protected float getShadowRadius(ST displayEntityRenderState) {
        DisplayEntity.RenderState renderState = ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState;
        if (renderState == null) {
            return 0.0f;
        }
        return renderState.shadowRadius().lerp(((DisplayEntityRenderState)displayEntityRenderState).lerpProgress);
    }

    protected float getShadowOpacity(ST displayEntityRenderState) {
        DisplayEntity.RenderState renderState = ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState;
        if (renderState == null) {
            return 0.0f;
        }
        return renderState.shadowStrength().lerp(((DisplayEntityRenderState)displayEntityRenderState).lerpProgress);
    }

    public void render(ST displayEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        DisplayEntity.RenderState renderState = ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState;
        if (renderState == null || !displayEntityRenderState.canRender()) {
            return;
        }
        float f = ((DisplayEntityRenderState)displayEntityRenderState).lerpProgress;
        super.render(displayEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)this.getBillboardRotation(renderState, displayEntityRenderState, new Quaternionf()));
        AffineTransformation affineTransformation = (AffineTransformation)renderState.transformation().interpolate(f);
        matrixStack.multiplyPositionMatrix(affineTransformation.getMatrix());
        this.render(displayEntityRenderState, matrixStack, orderedRenderCommandQueue, ((DisplayEntityRenderState)displayEntityRenderState).light, f);
        matrixStack.pop();
    }

    private Quaternionf getBillboardRotation(DisplayEntity.RenderState renderState, ST state, Quaternionf rotation) {
        return switch (1.field_42526[renderState.billboardConstraints().ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> rotation.rotationYXZ((float)(-Math.PI) / 180 * ((DisplayEntityRenderState)state).yaw, (float)Math.PI / 180 * ((DisplayEntityRenderState)state).pitch, 0.0f);
            case 2 -> rotation.rotationYXZ((float)(-Math.PI) / 180 * ((DisplayEntityRenderState)state).yaw, (float)Math.PI / 180 * DisplayEntityRenderer.getNegatedPitch((float)((DisplayEntityRenderState)state).cameraPitch), 0.0f);
            case 3 -> rotation.rotationYXZ((float)(-Math.PI) / 180 * DisplayEntityRenderer.getBackwardsYaw((float)((DisplayEntityRenderState)state).cameraYaw), (float)Math.PI / 180 * ((DisplayEntityRenderState)state).pitch, 0.0f);
            case 4 -> rotation.rotationYXZ((float)(-Math.PI) / 180 * DisplayEntityRenderer.getBackwardsYaw((float)((DisplayEntityRenderState)state).cameraYaw), (float)Math.PI / 180 * DisplayEntityRenderer.getNegatedPitch((float)((DisplayEntityRenderState)state).cameraPitch), 0.0f);
        };
    }

    private static float getBackwardsYaw(float yaw) {
        return yaw - 180.0f;
    }

    private static float getNegatedPitch(float pitch) {
        return -pitch;
    }

    private static <T extends DisplayEntity> float lerpYaw(T entity, float deltaTicks) {
        return entity.getLerpedYaw(deltaTicks);
    }

    private static <T extends DisplayEntity> float lerpPitch(T entity, float deltaTicks) {
        return entity.getLerpedPitch(deltaTicks);
    }

    protected abstract void render(ST var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4, float var5);

    public void updateRenderState(T displayEntity, ST displayEntityRenderState, float f) {
        super.updateRenderState(displayEntity, displayEntityRenderState, f);
        ((DisplayEntityRenderState)displayEntityRenderState).displayRenderState = displayEntity.getRenderState();
        ((DisplayEntityRenderState)displayEntityRenderState).lerpProgress = displayEntity.getLerpProgress(f);
        ((DisplayEntityRenderState)displayEntityRenderState).yaw = DisplayEntityRenderer.lerpYaw(displayEntity, (float)f);
        ((DisplayEntityRenderState)displayEntityRenderState).pitch = DisplayEntityRenderer.lerpPitch(displayEntity, (float)f);
        Camera camera = this.renderDispatcher.camera;
        ((DisplayEntityRenderState)displayEntityRenderState).cameraPitch = camera.getPitch();
        ((DisplayEntityRenderState)displayEntityRenderState).cameraYaw = camera.getYaw();
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((DisplayEntityRenderState)state);
    }

    protected /* synthetic */ int getBlockLight(Entity entity, BlockPos pos) {
        return this.getBlockLight((DisplayEntity)entity, pos);
    }

    protected /* synthetic */ int getSkyLight(Entity entity, BlockPos pos) {
        return this.getSkyLight((DisplayEntity)entity, pos);
    }
}

