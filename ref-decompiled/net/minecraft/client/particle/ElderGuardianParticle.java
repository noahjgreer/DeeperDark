/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.model.ElderGuardianParticleModel
 *  net.minecraft.client.particle.ElderGuardianParticle
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleTextureSheet
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.ElderGuardianEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ElderGuardianParticleModel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.ElderGuardianEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ElderGuardianParticle
extends Particle {
    protected final ElderGuardianParticleModel model;
    protected final RenderLayer renderLayer = RenderLayers.entityTranslucent((Identifier)ElderGuardianEntityRenderer.TEXTURE);

    ElderGuardianParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
        this.model = new ElderGuardianParticleModel(MinecraftClient.getInstance().getLoadedEntityModels().getModelPart(EntityModelLayers.ELDER_GUARDIAN));
        this.gravityStrength = 0.0f;
        this.maxAge = 30;
    }

    public ParticleTextureSheet textureSheet() {
        return ParticleTextureSheet.ELDER_GUARDIANS;
    }
}

