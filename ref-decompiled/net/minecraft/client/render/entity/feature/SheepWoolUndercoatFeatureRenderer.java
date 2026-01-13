/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SheepWoolUndercoatFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.SheepEntityModel
 *  net.minecraft.client.render.entity.model.SheepWoolEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SheepEntityRenderState
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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SheepWoolUndercoatFeatureRenderer
extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/sheep/sheep_wool_undercoat.png");
    private final EntityModel<SheepEntityRenderState> model;
    private final EntityModel<SheepEntityRenderState> babyModel;

    public SheepWoolUndercoatFeatureRenderer(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_WOOL_UNDERCOAT));
        this.babyModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_BABY_WOOL_UNDERCOAT));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, SheepEntityRenderState sheepEntityRenderState, float f, float g) {
        if (sheepEntityRenderState.invisible || !sheepEntityRenderState.rainbow && sheepEntityRenderState.color == DyeColor.WHITE) {
            return;
        }
        EntityModel entityModel = sheepEntityRenderState.baby ? this.babyModel : this.model;
        SheepWoolUndercoatFeatureRenderer.render((Model)entityModel, (Identifier)TEXTURE, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (LivingEntityRenderState)sheepEntityRenderState, (int)sheepEntityRenderState.getRgbColor(), (int)1);
    }
}

