/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.CloudParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CloudParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    CloudParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        float g;
        this.velocityMultiplier = 0.96f;
        this.spriteProvider = spriteProvider;
        float f = 2.5f;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        this.red = g = 1.0f - this.random.nextFloat() * 0.3f;
        this.green = g;
        this.blue = g;
        this.scale *= 1.875f;
        int i = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.3));
        this.maxAge = (int)Math.max((float)i * 2.5f, 1.0f);
        this.collidesWithWorld = false;
        this.updateSprite(spriteProvider);
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f), (float)0.0f, (float)1.0f);
    }

    public void tick() {
        super.tick();
        if (!this.dead) {
            double d;
            this.updateSprite(this.spriteProvider);
            PlayerEntity playerEntity = this.world.getClosestPlayer(this.x, this.y, this.z, 2.0, false);
            if (playerEntity != null && this.y > (d = playerEntity.getY())) {
                this.y += (d - this.y) * 0.2;
                this.velocityY += (playerEntity.getVelocity().y - this.velocityY) * 0.2;
                this.setPos(this.x, this.y, this.z);
            }
        }
    }
}

