/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer
 *  net.minecraft.client.render.entity.feature.EyesFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EndermanEntityModel
 *  net.minecraft.client.render.entity.state.EndermanEntityRenderState
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EndermanEyesFeatureRenderer
extends EyesFeatureRenderer<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
    private static final RenderLayer SKIN = RenderLayers.eyes((Identifier)Identifier.ofVanilla((String)"textures/entity/enderman/enderman_eyes.png"));

    public EndermanEyesFeatureRenderer(FeatureRendererContext<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> featureRendererContext) {
        super(featureRendererContext);
    }

    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}

