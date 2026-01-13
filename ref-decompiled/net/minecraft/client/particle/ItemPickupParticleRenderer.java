/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.ItemPickupParticle
 *  net.minecraft.client.particle.ItemPickupParticleRenderer
 *  net.minecraft.client.particle.ItemPickupParticleRenderer$Instance
 *  net.minecraft.client.particle.ItemPickupParticleRenderer$Result
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.particle.ParticleRenderer
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.Submittable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.particle.ItemPickupParticleRenderer;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Submittable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ItemPickupParticleRenderer
extends ParticleRenderer<ItemPickupParticle> {
    public ItemPickupParticleRenderer(ParticleManager particleManager) {
        super(particleManager);
    }

    public Submittable render(Frustum frustum, Camera camera, float tickProgress) {
        return new Result(this.particles.stream().map(particle -> Instance.create((ItemPickupParticle)particle, (Camera)camera, (float)tickProgress)).toList());
    }
}

