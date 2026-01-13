/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.ElderGuardianParticle
 *  net.minecraft.client.particle.ElderGuardianParticleRenderer
 *  net.minecraft.client.particle.ElderGuardianParticleRenderer$Result
 *  net.minecraft.client.particle.ElderGuardianParticleRenderer$State
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.particle.ParticleRenderer
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.Submittable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.particle.ElderGuardianParticleRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Submittable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ElderGuardianParticleRenderer
extends ParticleRenderer<ElderGuardianParticle> {
    public ElderGuardianParticleRenderer(ParticleManager particleManager) {
        super(particleManager);
    }

    public Submittable render(Frustum frustum, Camera camera, float tickProgress) {
        return new Result(this.particles.stream().map(elderGuardianParticle -> State.create((ElderGuardianParticle)elderGuardianParticle, (Camera)camera, (float)tickProgress)).toList());
    }
}

