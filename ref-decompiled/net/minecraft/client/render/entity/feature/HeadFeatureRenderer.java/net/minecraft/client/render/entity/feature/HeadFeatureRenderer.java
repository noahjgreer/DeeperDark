/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class HeadFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    private static final float field_53209 = 0.625f;
    private static final float field_53210 = 1.1875f;
    private final HeadTransformation headTransformation;
    private final Function<SkullBlock.SkullType, SkullBlockEntityModel> headModels;
    private final PlayerSkinCache skinCache;

    public HeadFeatureRenderer(FeatureRendererContext<S, M> context, LoadedEntityModels models, PlayerSkinCache skinCache) {
        this(context, models, skinCache, HeadTransformation.DEFAULT);
    }

    public HeadFeatureRenderer(FeatureRendererContext<S, M> context, LoadedEntityModels models, PlayerSkinCache skinCache, HeadTransformation headTransformation) {
        super(context);
        this.headTransformation = headTransformation;
        this.headModels = Util.memoize(type -> SkullBlockEntityRenderer.getModels(models, type));
        this.skinCache = skinCache;
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S livingEntityRenderState, float f, float g) {
        if (((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.isEmpty() && ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType == null) {
            return;
        }
        matrixStack.push();
        matrixStack.scale(this.headTransformation.horizontalScale(), 1.0f, this.headTransformation.horizontalScale());
        Object entityModel = this.getContextModel();
        ((Model)entityModel).getRootPart().applyTransform(matrixStack);
        ((ModelWithHead)entityModel).applyTransform(matrixStack);
        if (((LivingEntityRenderState)livingEntityRenderState).wearingSkullType != null) {
            matrixStack.translate(0.0f, this.headTransformation.skullYOffset(), 0.0f);
            matrixStack.scale(1.1875f, -1.1875f, -1.1875f);
            matrixStack.translate(-0.5, 0.0, -0.5);
            SkullBlock.SkullType skullType = ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType;
            SkullBlockEntityModel skullBlockEntityModel = this.headModels.apply(skullType);
            RenderLayer renderLayer = this.getRenderLayer((LivingEntityRenderState)livingEntityRenderState, skullType);
            SkullBlockEntityRenderer.render(null, 180.0f, ((LivingEntityRenderState)livingEntityRenderState).headItemAnimationProgress, matrixStack, orderedRenderCommandQueue, i, skullBlockEntityModel, renderLayer, ((LivingEntityRenderState)livingEntityRenderState).outlineColor, null);
        } else {
            HeadFeatureRenderer.translate(matrixStack, this.headTransformation);
            ((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.render(matrixStack, orderedRenderCommandQueue, i, OverlayTexture.DEFAULT_UV, ((LivingEntityRenderState)livingEntityRenderState).outlineColor);
        }
        matrixStack.pop();
    }

    private RenderLayer getRenderLayer(LivingEntityRenderState state, SkullBlock.SkullType skullType) {
        ProfileComponent profileComponent;
        if (skullType == SkullBlock.Type.PLAYER && (profileComponent = state.wearingSkullProfile) != null) {
            return this.skinCache.get(profileComponent).getRenderLayer();
        }
        return SkullBlockEntityRenderer.getCutoutRenderLayer(skullType, null);
    }

    public static void translate(MatrixStack matrices, HeadTransformation transformation) {
        matrices.translate(0.0f, -0.25f + transformation.yOffset(), 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrices.scale(0.625f, -0.625f, -0.625f);
    }

    @Environment(value=EnvType.CLIENT)
    public record HeadTransformation(float yOffset, float skullYOffset, float horizontalScale) {
        public static final HeadTransformation DEFAULT = new HeadTransformation(0.0f, 0.0f, 1.0f);
    }
}
