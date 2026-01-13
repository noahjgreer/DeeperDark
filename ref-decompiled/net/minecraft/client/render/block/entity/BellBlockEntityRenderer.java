/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BellBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.BellBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.model.BellBlockModel
 *  net.minecraft.client.render.block.entity.model.BellBlockModel$BellModelState
 *  net.minecraft.client.render.block.entity.state.BellBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.model.BellBlockModel;
import net.minecraft.client.render.block.entity.state.BellBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BellBlockEntityRenderer
implements BlockEntityRenderer<BellBlockEntity, BellBlockEntityRenderState> {
    public static final SpriteIdentifier BELL_BODY_TEXTURE = TexturedRenderLayers.ENTITY_SPRITE_MAPPER.mapVanilla("bell/bell_body");
    private final SpriteHolder materials;
    private final BellBlockModel bellBody;

    public BellBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.materials = context.spriteHolder();
        this.bellBody = new BellBlockModel(context.getLayerModelPart(EntityModelLayers.BELL));
    }

    public BellBlockEntityRenderState createRenderState() {
        return new BellBlockEntityRenderState();
    }

    public void updateRenderState(BellBlockEntity bellBlockEntity, BellBlockEntityRenderState bellBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)bellBlockEntity, (BlockEntityRenderState)bellBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        bellBlockEntityRenderState.ringTicks = (float)bellBlockEntity.ringTicks + f;
        bellBlockEntityRenderState.shakeDirection = bellBlockEntity.ringing ? bellBlockEntity.lastSideHit : null;
    }

    public void render(BellBlockEntityRenderState bellBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        BellBlockModel.BellModelState bellModelState = new BellBlockModel.BellModelState(bellBlockEntityRenderState.ringTicks, bellBlockEntityRenderState.shakeDirection);
        this.bellBody.setAngles(bellModelState);
        RenderLayer renderLayer = BELL_BODY_TEXTURE.getRenderLayer(RenderLayers::entitySolid);
        orderedRenderCommandQueue.submitModel((Model)this.bellBody, (Object)bellModelState, matrixStack, renderLayer, bellBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, this.materials.getSprite(BELL_BODY_TEXTURE), 0, bellBlockEntityRenderState.crumblingOverlay);
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

