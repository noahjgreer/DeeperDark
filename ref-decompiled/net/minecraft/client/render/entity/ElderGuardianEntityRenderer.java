/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.ElderGuardianEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.GuardianEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.GuardianEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianEntityRenderer
extends GuardianEntityRenderer {
    public static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/guardian_elder.png");

    public ElderGuardianEntityRenderer(EntityRendererFactory.Context context) {
        super(context, 1.2f, EntityModelLayers.ELDER_GUARDIAN);
    }

    public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
        return TEXTURE;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GuardianEntityRenderState)state);
    }
}

