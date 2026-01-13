/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.SalmonEntityRenderer
 *  net.minecraft.client.render.entity.SalmonEntityRenderer$1
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SalmonEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SalmonEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.SalmonEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.SalmonEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SalmonEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityRenderer
extends MobEntityRenderer<SalmonEntity, SalmonEntityRenderState, SalmonEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/salmon.png");
    private final SalmonEntityModel smallModel;
    private final SalmonEntityModel mediumModel;
    private final SalmonEntityModel largeModel;

    public SalmonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON)), 0.4f);
        this.smallModel = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON_SMALL));
        this.mediumModel = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON));
        this.largeModel = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON_LARGE));
    }

    public void updateRenderState(SalmonEntity salmonEntity, SalmonEntityRenderState salmonEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)salmonEntity, (LivingEntityRenderState)salmonEntityRenderState, f);
        salmonEntityRenderState.variant = salmonEntity.getVariant();
    }

    public Identifier getTexture(SalmonEntityRenderState salmonEntityRenderState) {
        return TEXTURE;
    }

    public SalmonEntityRenderState createRenderState() {
        return new SalmonEntityRenderState();
    }

    protected void setupTransforms(SalmonEntityRenderState salmonEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)salmonEntityRenderState, matrixStack, f, g);
        float h = 1.0f;
        float i = 1.0f;
        if (!salmonEntityRenderState.touchingWater) {
            h = 1.3f;
            i = 1.7f;
        }
        float j = h * 4.3f * MathHelper.sin((double)(i * 0.6f * salmonEntityRenderState.age));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(j));
        if (!salmonEntityRenderState.touchingWater) {
            matrixStack.translate(0.2f, 0.1f, 0.0f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }

    public void render(SalmonEntityRenderState salmonEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        this.model = switch (1.field_61802[salmonEntityRenderState.variant.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> this.smallModel;
            case 2 -> this.mediumModel;
            case 3 -> this.largeModel;
        };
        super.render((LivingEntityRenderState)salmonEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SalmonEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

