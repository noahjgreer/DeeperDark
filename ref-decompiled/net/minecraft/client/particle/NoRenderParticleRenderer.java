/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.NoRenderParticle
 *  net.minecraft.client.particle.NoRenderParticleRenderer
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.particle.ParticleRenderer
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.Submittable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Submittable;

@Environment(value=EnvType.CLIENT)
public class NoRenderParticleRenderer
extends ParticleRenderer<NoRenderParticle> {
    private static final Submittable EMPTY = (queue, cameraRenderState) -> {};

    public NoRenderParticleRenderer(ParticleManager particleManager) {
        super(particleManager);
    }

    public Submittable render(Frustum frustum, Camera camera, float tickProgress) {
        return EMPTY;
    }
}

