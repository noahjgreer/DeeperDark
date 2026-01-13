/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.ExplosionLargeParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class ExplosionLargeParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected ExplosionLargeParticle(ClientWorld world, double x, double y, double z, double velocityX, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        float f;
        this.maxAge = 6 + this.random.nextInt(4);
        this.red = f = this.random.nextFloat() * 0.6f + 0.4f;
        this.green = f;
        this.blue = f;
        this.scale = 2.0f * (1.0f - (float)velocityX * 0.5f);
        this.spriteProvider = spriteProvider;
        this.updateSprite(spriteProvider);
    }

    public int getBrightness(float tint) {
        return 0xF000F0;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.updateSprite(this.spriteProvider);
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }
}

