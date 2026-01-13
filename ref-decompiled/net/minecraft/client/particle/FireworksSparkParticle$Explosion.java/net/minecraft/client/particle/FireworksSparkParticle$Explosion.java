/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
static class FireworksSparkParticle.Explosion
extends AnimatedParticle {
    private boolean trail;
    private boolean flicker;
    private final ParticleManager particleManager;
    private float field_3801;
    private float field_3800;
    private float field_3799;
    private boolean field_3802;

    FireworksSparkParticle.Explosion(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ParticleManager particleManager, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.1f);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.particleManager = particleManager;
        this.scale *= 0.75f;
        this.maxAge = 48 + this.random.nextInt(12);
        this.updateSprite(spriteProvider);
    }

    public void setTrail(boolean trail) {
        this.trail = trail;
    }

    public void setFlicker(boolean flicker) {
        this.flicker = flicker;
    }

    @Override
    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
        if (!this.flicker || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
            super.render(submittable, camera, tickProgress);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
            FireworksSparkParticle.Explosion explosion = new FireworksSparkParticle.Explosion(this.world, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.particleManager, this.spriteProvider);
            explosion.setAlpha(0.99f);
            explosion.setColor(this.red, this.green, this.blue);
            explosion.age = explosion.maxAge / 2;
            if (this.field_3802) {
                explosion.field_3802 = true;
                explosion.field_3801 = this.field_3801;
                explosion.field_3800 = this.field_3800;
                explosion.field_3799 = this.field_3799;
            }
            explosion.flicker = this.flicker;
            this.particleManager.addParticle(explosion);
        }
    }
}
