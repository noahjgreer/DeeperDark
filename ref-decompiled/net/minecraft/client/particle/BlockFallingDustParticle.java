/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BlockFallingDustParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BlockFallingDustParticle
extends BillboardParticle {
    private final float rotationSpeed;
    private final SpriteProvider spriteProvider;

    BlockFallingDustParticle(ClientWorld world, double x, double y, double z, float red, float green, float blue, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.red = red;
        this.green = green;
        this.blue = blue;
        float f = 0.9f;
        this.scale *= 0.67499995f;
        int i = (int)(32.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)i * 0.9f, 1.0f);
        this.updateSprite(spriteProvider);
        this.rotationSpeed = (this.random.nextFloat() - 0.5f) * 0.1f;
        this.zRotation = this.random.nextFloat() * ((float)Math.PI * 2);
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f), (float)0.0f, (float)1.0f);
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
        this.lastZRotation = this.zRotation;
        this.zRotation += (float)Math.PI * this.rotationSpeed * 2.0f;
        if (this.onGround) {
            this.zRotation = 0.0f;
            this.lastZRotation = 0.0f;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityY -= (double)0.003f;
        this.velocityY = Math.max(this.velocityY, (double)-0.14f);
    }
}

