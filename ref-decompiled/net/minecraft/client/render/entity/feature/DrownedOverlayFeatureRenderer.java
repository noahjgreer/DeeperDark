/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.DrownedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DrownedOverlayFeatureRenderer
extends FeatureRenderer<ZombieEntityRenderState, DrownedEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla((String)"textures/entity/zombie/drowned_outer_layer.png");
    private final DrownedEntityModel model;
    private final DrownedEntityModel babyModel;

    public DrownedOverlayFeatureRenderer(FeatureRendererContext<ZombieEntityRenderState, DrownedEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new DrownedEntityModel(loader.getModelPart(EntityModelLayers.DROWNED_OUTER));
        this.babyModel = new DrownedEntityModel(loader.getModelPart(EntityModelLayers.DROWNED_BABY_OUTER));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, ZombieEntityRenderState zombieEntityRenderState, float f, float g) {
        DrownedEntityModel drownedEntityModel = zombieEntityRenderState.baby ? this.babyModel : this.model;
        DrownedOverlayFeatureRenderer.render((Model)drownedEntityModel, (Identifier)SKIN, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)i, (LivingEntityRenderState)zombieEntityRenderState, (int)-1, (int)1);
    }
}

