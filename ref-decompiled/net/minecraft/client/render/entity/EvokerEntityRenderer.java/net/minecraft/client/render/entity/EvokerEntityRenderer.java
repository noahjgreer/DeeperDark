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
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EvokerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerEntityRenderer<T extends SpellcastingIllagerEntity>
extends IllagerEntityRenderer<T, EvokerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker.png");

    public EvokerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.EVOKER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<EvokerEntityRenderState, IllagerEntityModel<EvokerEntityRenderState>>(this, (FeatureRendererContext)this){

            @Override
            public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, EvokerEntityRenderState evokerEntityRenderState, float f, float g) {
                if (evokerEntityRenderState.spellcasting) {
                    super.render(matrixStack, orderedRenderCommandQueue, i, evokerEntityRenderState, f, g);
                }
            }
        });
    }

    @Override
    public Identifier getTexture(EvokerEntityRenderState evokerEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public EvokerEntityRenderState createRenderState() {
        return new EvokerEntityRenderState();
    }

    @Override
    public void updateRenderState(T spellcastingIllagerEntity, EvokerEntityRenderState evokerEntityRenderState, float f) {
        super.updateRenderState(spellcastingIllagerEntity, evokerEntityRenderState, f);
        evokerEntityRenderState.spellcasting = ((SpellcastingIllagerEntity)spellcastingIllagerEntity).isSpellcasting();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((EvokerEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
