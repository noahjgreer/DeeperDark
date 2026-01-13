/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
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
    private static final Identifier A_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a.png");
    private static final Identifier B_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b.png");

    public TropicalFishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SmallTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15f);
        this.largeModel = new LargeTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
        this.addFeature(new TropicalFishColorFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    public Identifier getTexture(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
        return switch (tropicalFishEntityRenderState.variety.getSize()) {
            default -> throw new MatchException(null, null);
            case TropicalFishEntity.Size.SMALL -> A_TEXTURE;
            case TropicalFishEntity.Size.LARGE -> B_TEXTURE;
        };
    }

    @Override
    public TropicalFishEntityRenderState createRenderState() {
        return new TropicalFishEntityRenderState();
    }

    @Override
    public void updateRenderState(TropicalFishEntity tropicalFishEntity, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f) {
        super.updateRenderState(tropicalFishEntity, tropicalFishEntityRenderState, f);
        tropicalFishEntityRenderState.variety = tropicalFishEntity.getVariety();
        tropicalFishEntityRenderState.baseColor = tropicalFishEntity.getBaseColor().getEntityColor();
        tropicalFishEntityRenderState.patternColor = tropicalFishEntity.getPatternColor().getEntityColor();
    }

    @Override
    public void render(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        this.model = switch (tropicalFishEntityRenderState.variety.getSize()) {
            default -> throw new MatchException(null, null);
            case TropicalFishEntity.Size.SMALL -> this.smallModel;
            case TropicalFishEntity.Size.LARGE -> this.largeModel;
        };
        super.render(tropicalFishEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    @Override
    protected int getMixColor(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
        return tropicalFishEntityRenderState.baseColor;
    }

    @Override
    protected void setupTransforms(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(tropicalFishEntityRenderState, matrixStack, f, g);
        float h = 4.3f * MathHelper.sin(0.6f * tropicalFishEntityRenderState.age);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(h));
        if (!tropicalFishEntityRenderState.touchingWater) {
            matrixStack.translate(0.2f, 0.1f, 0.0f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
        }
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((TropicalFishEntityRenderState)state);
    }

    @Override
    protected /* synthetic */ int getMixColor(LivingEntityRenderState state) {
        return this.getMixColor((TropicalFishEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
