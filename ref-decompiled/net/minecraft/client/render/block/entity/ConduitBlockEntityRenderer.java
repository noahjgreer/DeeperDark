/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ConduitBlockEntity
 *  net.minecraft.client.model.Dilation
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.ConduitBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.SpriteMapper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.ConduitBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderer
implements BlockEntityRenderer<ConduitBlockEntity, ConduitBlockEntityRenderState> {
    public static final SpriteMapper SPRITE_MAPPER = new SpriteMapper(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, "entity/conduit");
    public static final SpriteIdentifier BASE_TEXTURE = SPRITE_MAPPER.mapVanilla("base");
    public static final SpriteIdentifier CAGE_TEXTURE = SPRITE_MAPPER.mapVanilla("cage");
    public static final SpriteIdentifier WIND_TEXTURE = SPRITE_MAPPER.mapVanilla("wind");
    public static final SpriteIdentifier WIND_VERTICAL_TEXTURE = SPRITE_MAPPER.mapVanilla("wind_vertical");
    public static final SpriteIdentifier OPEN_EYE_TEXTURE = SPRITE_MAPPER.mapVanilla("open_eye");
    public static final SpriteIdentifier CLOSED_EYE_TEXTURE = SPRITE_MAPPER.mapVanilla("closed_eye");
    private final SpriteHolder materials;
    private final ModelPart conduitEye;
    private final ModelPart conduitWind;
    private final ModelPart conduitShell;
    private final ModelPart conduit;

    public ConduitBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.materials = ctx.spriteHolder();
        this.conduitEye = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_EYE);
        this.conduitWind = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_WIND);
        this.conduitShell = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_SHELL);
        this.conduit = ctx.getLayerModelPart(EntityModelLayers.CONDUIT);
    }

    public static TexturedModelData getEyeTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("eye", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, new Dilation(0.01f)), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)16, (int)16);
    }

    public static TexturedModelData getWindTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("wind", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }

    public static TexturedModelData getShellTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)16);
    }

    public static TexturedModelData getPlainTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of((ModelData)modelData, (int)32, (int)16);
    }

    public ConduitBlockEntityRenderState createRenderState() {
        return new ConduitBlockEntityRenderState();
    }

    public void updateRenderState(ConduitBlockEntity conduitBlockEntity, ConduitBlockEntityRenderState conduitBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)conduitBlockEntity, (BlockEntityRenderState)conduitBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        conduitBlockEntityRenderState.active = conduitBlockEntity.isActive();
        conduitBlockEntityRenderState.rotation = conduitBlockEntity.getRotation(conduitBlockEntity.isActive() ? f : 0.0f);
        conduitBlockEntityRenderState.ticks = (float)conduitBlockEntity.ticks + f;
        conduitBlockEntityRenderState.rotationPhase = conduitBlockEntity.ticks / 66 % 3;
        conduitBlockEntityRenderState.eyeOpen = conduitBlockEntity.isEyeOpen();
    }

    public void render(ConduitBlockEntityRenderState conduitBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (!conduitBlockEntityRenderState.active) {
            matrixStack.push();
            matrixStack.translate(0.5f, 0.5f, 0.5f);
            matrixStack.multiply((Quaternionfc)new Quaternionf().rotationY(conduitBlockEntityRenderState.rotation * ((float)Math.PI / 180)));
            orderedRenderCommandQueue.submitModelPart(this.conduitShell, matrixStack, BASE_TEXTURE.getRenderLayer(RenderLayers::entitySolid), conduitBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, this.materials.getSprite(BASE_TEXTURE), -1, conduitBlockEntityRenderState.crumblingOverlay);
            matrixStack.pop();
            return;
        }
        float f = conduitBlockEntityRenderState.rotation * 57.295776f;
        float g = MathHelper.sin((double)(conduitBlockEntityRenderState.ticks * 0.1f)) / 2.0f + 0.5f;
        g = g * g + g;
        matrixStack.push();
        matrixStack.translate(0.5f, 0.3f + g * 0.2f, 0.5f);
        Vector3f vector3f = new Vector3f(0.5f, 1.0f, 0.5f).normalize();
        matrixStack.multiply((Quaternionfc)new Quaternionf().rotationAxis(f * ((float)Math.PI / 180), (Vector3fc)vector3f));
        orderedRenderCommandQueue.submitModelPart(this.conduit, matrixStack, CAGE_TEXTURE.getRenderLayer(RenderLayers::entityCutoutNoCull), conduitBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, this.materials.getSprite(CAGE_TEXTURE), -1, conduitBlockEntityRenderState.crumblingOverlay);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        if (conduitBlockEntityRenderState.rotationPhase == 1) {
            matrixStack.multiply((Quaternionfc)new Quaternionf().rotationX(1.5707964f));
        } else if (conduitBlockEntityRenderState.rotationPhase == 2) {
            matrixStack.multiply((Quaternionfc)new Quaternionf().rotationZ(1.5707964f));
        }
        SpriteIdentifier spriteIdentifier = conduitBlockEntityRenderState.rotationPhase == 1 ? WIND_VERTICAL_TEXTURE : WIND_TEXTURE;
        RenderLayer renderLayer = spriteIdentifier.getRenderLayer(RenderLayers::entityCutoutNoCull);
        Sprite sprite = this.materials.getSprite(spriteIdentifier);
        orderedRenderCommandQueue.submitModelPart(this.conduitWind, matrixStack, renderLayer, conduitBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, sprite);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply((Quaternionfc)new Quaternionf().rotationXYZ((float)Math.PI, 0.0f, (float)Math.PI));
        orderedRenderCommandQueue.submitModelPart(this.conduitWind, matrixStack, renderLayer, conduitBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, sprite);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.5f, 0.3f + g * 0.2f, 0.5f);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)cameraRenderState.orientation);
        matrixStack.multiply((Quaternionfc)new Quaternionf().rotationZ((float)Math.PI).rotateY((float)Math.PI));
        float h = 1.3333334f;
        matrixStack.scale(1.3333334f, 1.3333334f, 1.3333334f);
        SpriteIdentifier spriteIdentifier2 = conduitBlockEntityRenderState.eyeOpen ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE;
        orderedRenderCommandQueue.submitModelPart(this.conduitEye, matrixStack, spriteIdentifier2.getRenderLayer(RenderLayers::entityCutoutNoCull), conduitBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, this.materials.getSprite(spriteIdentifier2));
        matrixStack.pop();
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

