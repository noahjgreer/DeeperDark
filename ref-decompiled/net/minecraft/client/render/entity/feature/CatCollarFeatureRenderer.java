/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.CatEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.state.CatEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CatCollarFeatureRenderer
extends FeatureRenderer<CatEntityRenderState, CatEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla((String)"textures/entity/cat/cat_collar.png");
    private final CatEntityModel model;
    private final CatEntityModel babyModel;

    public CatCollarFeatureRenderer(FeatureRendererContext<CatEntityRenderState, CatEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new CatEntityModel(loader.getModelPart(EntityModelLayers.CAT_COLLAR));
        this.babyModel = new CatEntityModel(loader.getModelPart(EntityModelLayers.CAT_BABY_COLLAR));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, CatEntityRenderState catEntityRenderState, float f, float g) {
        DyeColor dyeColor = catEntityRenderState.collarColor;
        if (dyeColor == null) {
            return;
        }
        int j = dyeColor.getEntityColor();
        CatEntityModel catEntityModel = catEntityRenderState.baby ? this.babyModel : this.model;
        CatCollarFeatureRenderer.render((Model)catEntityModel, (Identifier)SKIN, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (LivingEntityRenderState)catEntityRenderState, (int)j, (int)1);
    }
}

