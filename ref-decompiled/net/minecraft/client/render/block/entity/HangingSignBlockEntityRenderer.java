/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.WoodType
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.Model$SinglePartModel
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer$AttachmentType
 *  net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer$Variant
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
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
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
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
public class HangingSignBlockEntityRenderer
extends AbstractSignBlockEntityRenderer {
    private static final String PLANK = "plank";
    private static final String V_CHAINS = "vChains";
    private static final String NORMAL_CHAINS = "normalChains";
    private static final String CHAIN_L1 = "chainL1";
    private static final String CHAIN_L2 = "chainL2";
    private static final String CHAIN_R1 = "chainR1";
    private static final String CHAIN_R2 = "chainR2";
    private static final String BOARD = "board";
    public static final float MODEL_SCALE = 1.0f;
    private static final float TEXT_SCALE = 0.9f;
    private static final Vec3d TEXT_OFFSET = new Vec3d(0.0, (double)-0.32f, (double)0.073f);
    private final Map<Variant, Model.SinglePartModel> models;

    public HangingSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
        Stream stream = WoodType.stream().flatMap(woodType -> Arrays.stream(AttachmentType.values()).map(attachmentType -> new Variant(woodType, attachmentType)));
        this.models = (Map)stream.collect(ImmutableMap.toImmutableMap(variant -> variant, variant -> HangingSignBlockEntityRenderer.createModel((LoadedEntityModels)context.loadedEntityModels(), (WoodType)variant.woodType, (AttachmentType)variant.attachmentType)));
    }

    public static Model.SinglePartModel createModel(LoadedEntityModels models, WoodType woodType, AttachmentType attachmentType) {
        return new Model.SinglePartModel(models.getModelPart(EntityModelLayers.createHangingSign((WoodType)woodType, (AttachmentType)attachmentType)), RenderLayers::entityCutoutNoCull);
    }

    protected float getSignScale() {
        return 1.0f;
    }

    protected float getTextScale() {
        return 0.9f;
    }

    public static void setAngles(MatrixStack matrices, float blockRotationDegrees) {
        matrices.translate(0.5, 0.9375, 0.5);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(blockRotationDegrees));
        matrices.translate(0.0f, -0.3125f, 0.0f);
    }

    protected void applyTransforms(MatrixStack matrices, float blockRotationDegrees, BlockState state) {
        HangingSignBlockEntityRenderer.setAngles((MatrixStack)matrices, (float)blockRotationDegrees);
    }

    protected Model.SinglePartModel getModel(BlockState state, WoodType woodType) {
        AttachmentType attachmentType = AttachmentType.from((BlockState)state);
        return (Model.SinglePartModel)this.models.get(new Variant(woodType, attachmentType));
    }

    protected SpriteIdentifier getTextureId(WoodType woodType) {
        return TexturedRenderLayers.getHangingSignTextureId((WoodType)woodType);
    }

    protected Vec3d getTextOffset() {
        return TEXT_OFFSET;
    }

    public static void renderAsItem(SpriteHolder spriteHolder, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, Model.SinglePartModel singlePartModel, SpriteIdentifier spriteIdentifier) {
        matrixStack.push();
        HangingSignBlockEntityRenderer.setAngles((MatrixStack)matrixStack, (float)0.0f);
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        orderedRenderCommandQueue.submitModel((Model)singlePartModel, (Object)Unit.INSTANCE, matrixStack, spriteIdentifier.getRenderLayer(arg_0 -> ((Model.SinglePartModel)singlePartModel).getLayer(arg_0)), i, j, -1, spriteHolder.getSprite(spriteIdentifier), OverlayTexture.DEFAULT_UV, null);
        matrixStack.pop();
    }

    public static TexturedModelData getTexturedModelData(AttachmentType attachmentType) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("board", ModelPartBuilder.create().uv(0, 12).cuboid(-7.0f, 0.0f, -1.0f, 14.0f, 10.0f, 2.0f), ModelTransform.NONE);
        if (attachmentType == AttachmentType.WALL) {
            modelPartData.addChild("plank", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -6.0f, -2.0f, 16.0f, 2.0f, 4.0f), ModelTransform.NONE);
        }
        if (attachmentType == AttachmentType.WALL || attachmentType == AttachmentType.CEILING) {
            ModelPartData modelPartData2 = modelPartData.addChild("normalChains", ModelPartBuilder.create(), ModelTransform.NONE);
            modelPartData2.addChild("chainL1", ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of((float)-5.0f, (float)-6.0f, (float)0.0f, (float)0.0f, (float)-0.7853982f, (float)0.0f));
            modelPartData2.addChild("chainL2", ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of((float)-5.0f, (float)-6.0f, (float)0.0f, (float)0.0f, (float)0.7853982f, (float)0.0f));
            modelPartData2.addChild("chainR1", ModelPartBuilder.create().uv(0, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of((float)5.0f, (float)-6.0f, (float)0.0f, (float)0.0f, (float)-0.7853982f, (float)0.0f));
            modelPartData2.addChild("chainR2", ModelPartBuilder.create().uv(6, 6).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f), ModelTransform.of((float)5.0f, (float)-6.0f, (float)0.0f, (float)0.0f, (float)0.7853982f, (float)0.0f));
        }
        if (attachmentType == AttachmentType.CEILING_MIDDLE) {
            modelPartData.addChild("vChains", ModelPartBuilder.create().uv(14, 6).cuboid(-6.0f, -6.0f, 0.0f, 12.0f, 6.0f, 0.0f), ModelTransform.NONE);
        }
        return TexturedModelData.of((ModelData)modelData, (int)64, (int)32);
    }
}

