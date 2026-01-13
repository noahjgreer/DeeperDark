/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AbstractDustParticle
 *  net.minecraft.client.particle.BillboardParticleSubmittable
 *  net.minecraft.client.particle.DustColorTransitionParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.particle.AbstractDustParticleEffect
 *  net.minecraft.particle.DustColorTransitionParticleEffect
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AbstractDustParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class DustColorTransitionParticle
extends AbstractDustParticle<DustColorTransitionParticleEffect> {
    private final Vector3f startColor;
    private final Vector3f endColor;

    protected DustColorTransitionParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, DustColorTransitionParticleEffect parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, (AbstractDustParticleEffect)parameters, spriteProvider);
        float f = this.random.nextFloat() * 0.4f + 0.6f;
        this.startColor = this.darken(parameters.getFromColor(), f);
        this.endColor = this.darken(parameters.getToColor(), f);
    }

    private Vector3f darken(Vector3f color, float multiplier) {
        return new Vector3f(this.darken(color.x(), multiplier), this.darken(color.y(), multiplier), this.darken(color.z(), multiplier));
    }

    private void updateColor(float tickProgress) {
        float f = ((float)this.age + tickProgress) / ((float)this.maxAge + 1.0f);
        Vector3f vector3f = new Vector3f((Vector3fc)this.startColor).lerp((Vector3fc)this.endColor, f);
        this.red = vector3f.x();
        this.green = vector3f.y();
        this.blue = vector3f.z();
    }

    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
        this.updateColor(tickProgress);
        super.render(submittable, camera, tickProgress);
    }
}

