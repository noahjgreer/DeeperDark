/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.TropicalFishEntityRenderer
 *  net.minecraft.client.render.entity.TropicalFishEntityRenderer$1
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel
 *  net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.TropicalFishEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.TropicalFishEntity
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
import net.minecraft.client.render.entity.TropicalFishEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class TropicalFishEntityRenderer
extends MobEntityRenderer<TropicalFishEntity, TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> {
    private final EntityModel<TropicalFishEntityRenderState> smallModel = this.getModel();
    private final EntityModel<TropicalFishEntityRenderState> largeModel;
    private static final Identifier A_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_a.png");
    private static final Identifier B_TEXTURE = Identifier.ofVanilla((String)"textures/entity/fish/tropical_b.png");

    public TropicalFishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new SmallTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15f);
        this.largeModel = new LargeTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
        this.addFeature((FeatureRenderer)new TropicalFishColorFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    public Identifier getTexture(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
        return switch (1.field_41645[tropicalFishEntityRenderState.variety.getSize().ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> A_TEXTURE;
            case 2 -> B_TEXTURE;
        };
    }

    public TropicalFishEntityRenderState createRenderState() {
        return new TropicalFishEntityRenderState();
    }

    public void updateRenderState(TropicalFishEntity tropicalFishEntity, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)tropicalFishEntity, (LivingEntityRenderState)tropicalFishEntityRenderState, f);
        tropicalFishEntityRenderState.variety = tropicalFishEntity.getVariety();
        tropicalFishEntityRenderState.baseColor = tropicalFishEntity.getBaseColor().getEntityColor();
        tropicalFishEntityRenderState.patternColor = tropicalFishEntity.getPatternColor().getEntityColor();
    }

    public void render(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        this.model = switch (1.field_41645[tropicalFishEntityRenderState.variety.getSize().ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> this.smallModel;
            case 2 -> this.largeModel;
        };
        super.render((LivingEntityRenderState)tropicalFishEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    protected int getMixColor(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
        return tropicalFishEntityRenderState.baseColor;
    }

    protected void setupTransforms(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)tropicalFishEntityRenderState, matrixStack, f, g);
        float h = 4.3f * MathHelper.sin((double)(0.6f * tropicalFishEntityRenderState.age));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(h));
        if (!tropicalFishEntityRenderState.touchingWater) {
            matrixStack.translate(0.2f, 0.1f, 0.0f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((TropicalFishEntityRenderState)state);
    }

    protected /* synthetic */ int getMixColor(LivingEntityRenderState state) {
        return this.getMixColor((TropicalFishEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

