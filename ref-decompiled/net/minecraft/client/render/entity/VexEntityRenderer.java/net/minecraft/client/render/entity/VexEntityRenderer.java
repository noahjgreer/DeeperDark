/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VexEntityRenderState;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class VexEntityRenderer
extends MobEntityRenderer<VexEntity, VexEntityRenderState, VexEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/vex.png");
    private static final Identifier CHARGING_TEXTURE = Identifier.ofVanilla("textures/entity/illager/vex_charging.png");

    public VexEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new VexEntityModel(context.getPart(EntityModelLayers.VEX)), 0.3f);
        this.addFeature(new HeldItemFeatureRenderer<VexEntityRenderState, VexEntityModel>(this));
    }

    @Override
    protected int getBlockLight(VexEntity vexEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(VexEntityRenderState vexEntityRenderState) {
        if (vexEntityRenderState.charging) {
            return CHARGING_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    public VexEntityRenderState createRenderState() {
        return new VexEntityRenderState();
    }

    @Override
    public void updateRenderState(VexEntity vexEntity, VexEntityRenderState vexEntityRenderState, float f) {
        super.updateRenderState(vexEntity, vexEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(vexEntity, vexEntityRenderState, this.itemModelResolver, f);
        vexEntityRenderState.charging = vexEntity.isCharging();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((VexEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
