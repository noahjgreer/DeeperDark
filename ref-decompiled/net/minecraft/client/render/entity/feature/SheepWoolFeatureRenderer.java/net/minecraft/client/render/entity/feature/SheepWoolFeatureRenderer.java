/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SheepWoolFeatureRenderer
extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/sheep/sheep_wool.png");
    private final EntityModel<SheepEntityRenderState> woolModel;
    private final EntityModel<SheepEntityRenderState> babyWoolModel;

    public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.woolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_WOOL));
        this.babyWoolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_BABY_WOOL));
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, SheepEntityRenderState sheepEntityRenderState, float f, float g) {
        EntityModel<SheepEntityRenderState> entityModel;
        if (sheepEntityRenderState.sheared) {
            return;
        }
        EntityModel<SheepEntityRenderState> entityModel2 = entityModel = sheepEntityRenderState.baby ? this.babyWoolModel : this.woolModel;
        if (sheepEntityRenderState.invisible) {
            if (sheepEntityRenderState.hasOutline()) {
                orderedRenderCommandQueue.submitModel(entityModel, sheepEntityRenderState, matrixStack, RenderLayers.outlineNoCull(TEXTURE), i, LivingEntityRenderer.getOverlay(sheepEntityRenderState, 0.0f), -16777216, null, sheepEntityRenderState.outlineColor, null);
            }
            return;
        }
        SheepWoolFeatureRenderer.render(entityModel, TEXTURE, matrixStack, orderedRenderCommandQueue, i, sheepEntityRenderState, sheepEntityRenderState.getRgbColor(), 0);
    }
}
