/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.SignBlock
 *  net.minecraft.block.WoodType
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.Model$SinglePartModel
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.SignBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.SignBlockEntityRenderer$SignModelPair
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Unit
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Unit;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SignBlockEntityRenderer
extends AbstractSignBlockEntityRenderer {
    public static final float SCALE = 0.6666667f;
    private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, 0.3333333432674408, 0.046666666865348816);
    private final Map<WoodType, SignModelPair> typeToModelPair = (Map)WoodType.stream().collect(ImmutableMap.toImmutableMap(signType -> signType, signType -> new SignModelPair(SignBlockEntityRenderer.createSignModel((LoadedEntityModels)ctx.loadedEntityModels(), (WoodType)signType, (boolean)true), SignBlockEntityRenderer.createSignModel((LoadedEntityModels)ctx.loadedEntityModels(), (WoodType)signType, (boolean)false))));

    public SignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    protected Model.SinglePartModel getModel(BlockState state, WoodType woodType) {
        SignModelPair signModelPair = (SignModelPair)this.typeToModelPair.get(woodType);
        return state.getBlock() instanceof SignBlock ? signModelPair.standing() : signModelPair.wall();
    }

    protected SpriteIdentifier getTextureId(WoodType woodType) {
        return TexturedRenderLayers.getSignTextureId((WoodType)woodType);
    }

    protected float getSignScale() {
        return 0.6666667f;
    }

    protected float getTextScale() {
        return 0.6666667f;
    }

    private static void setAngles(MatrixStack matrices, float blockRotationDegrees) {
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(blockRotationDegrees));
    }

    protected void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state) {
        SignBlockEntityRenderer.setAngles((MatrixStack)matrices, (float)blockRotationDegrees);
        if (!(state.getBlock() instanceof SignBlock)) {
            matrices.translate(0.0f, -0.3125f, -0.4375f);
        }
    }

    protected Vec3d getTextOffset() {
        return TEXT_OFFSET;
    }

    public static void renderAsItem(SpriteHolder spriteHolder, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, Model.SinglePartModel model, SpriteIdentifier texture) {
        matrices.push();
        SignBlockEntityRenderer.setTransformsForItem((MatrixStack)matrices);
        queue.submitModel((Model)model, (Object)Unit.INSTANCE, matrices, texture.getRenderLayer(arg_0 -> ((Model.SinglePartModel)model).getLayer(arg_0)), light, overlay, -1, spriteHolder.getSprite(texture), 0, null);
        matrices.pop();
    }

    public static void setTransformsForItem(MatrixStack matrices) {
        SignBlockEntityRenderer.setAngles((MatrixStack)matrices, (float)0.0f);
        matrices.scale(0.6666667f, -0.6666667f, -0.6666667f);
    }

    public static Model.SinglePartModel createSignModel(LoadedEntityModels models, WoodType type, boolean standing) {
        EntityModelLayer entityModelLayer = standing ? EntityModelLayers.createStandingSign((WoodType)type) : EntityModelLayers.createWallSign((WoodType)type);
        return new Model.SinglePartModel(models.getModelPart(entityModelLayer), RenderLayers::entityCutoutNoCull);
    }

    public static TexturedModelData getTexturedModelData(boolean standing) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("sign", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0f, -14.0f, -1.0f, 24.0f, 12.0f, 2.0f), ModelTransform.NONE);
        if (standing) {
            modelPartData.addChild("stick", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 14.0f, 2.0f), ModelTransform.NONE);
        }
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }
}

