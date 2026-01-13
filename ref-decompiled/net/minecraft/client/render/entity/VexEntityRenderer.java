/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.VexEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.VexEntityModel
 *  net.minecraft.client.render.entity.state.ArmedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.VexEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.VexEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VexEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class VexEntityRenderer
extends MobEntityRenderer<VexEntity, VexEntityRenderState, VexEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/illager/vex.png");
    private static final Identifier CHARGING_TEXTURE = Identifier.ofVanilla((String)"textures/entity/illager/vex_charging.png");

    public VexEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new VexEntityModel(context.getPart(EntityModelLayers.VEX)), 0.3f);
        this.addFeature((FeatureRenderer)new HeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    protected int getBlockLight(VexEntity vexEntity, BlockPos blockPos) {
        return 15;
    }

    public Identifier getTexture(VexEntityRenderState vexEntityRenderState) {
        if (vexEntityRenderState.charging) {
            return CHARGING_TEXTURE;
        }
        return TEXTURE;
    }

    public VexEntityRenderState createRenderState() {
        return new VexEntityRenderState();
    }

    public void updateRenderState(VexEntity vexEntity, VexEntityRenderState vexEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)vexEntity, (LivingEntityRenderState)vexEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState((LivingEntity)vexEntity, (ArmedEntityRenderState)vexEntityRenderState, (ItemModelManager)this.itemModelResolver, (float)f);
        vexEntityRenderState.charging = vexEntity.isCharging();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((VexEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

