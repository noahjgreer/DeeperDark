/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.SheepEntityModel
 *  net.minecraft.client.render.entity.model.SheepWoolEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SheepEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class SheepWoolFeatureRenderer
extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/sheep/sheep_wool.png");
    private final EntityModel<SheepEntityRenderState> woolModel;
    private final EntityModel<SheepEntityRenderState> babyWoolModel;

    public SheepWoolFeatureRenderer(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.woolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_WOOL));
        this.babyWoolModel = new SheepWoolEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_BABY_WOOL));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, SheepEntityRenderState sheepEntityRenderState, float f, float g) {
        EntityModel entityModel;
        if (sheepEntityRenderState.sheared) {
            return;
        }
        EntityModel entityModel2 = entityModel = sheepEntityRenderState.baby ? this.babyWoolModel : this.woolModel;
        if (sheepEntityRenderState.invisible) {
            if (sheepEntityRenderState.hasOutline()) {
                orderedRenderCommandQueue.submitModel((Model)entityModel, (Object)sheepEntityRenderState, matrixStack, RenderLayers.outlineNoCull((Identifier)TEXTURE), i, LivingEntityRenderer.getOverlay((LivingEntityRenderState)sheepEntityRenderState, (float)0.0f), -16777216, null, sheepEntityRenderState.outlineColor, null);
            }
            return;
        }
        SheepWoolFeatureRenderer.render((Model)entityModel, (Identifier)TEXTURE, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (LivingEntityRenderState)sheepEntityRenderState, (int)sheepEntityRenderState.getRgbColor(), (int)0);
    }
}

