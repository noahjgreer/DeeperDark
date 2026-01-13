/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.LeavesParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class LeavesParticle
extends BillboardParticle {
    private static final float SPEED_SCALE = 0.0025f;
    private static final int field_43373 = 300;
    private static final int field_43366 = 300;
    private float angularVelocity;
    private final float angularAcceleration;
    private final float field_55127;
    private final boolean field_55128;
    private final boolean field_55129;
    private final double field_55130;
    private final double field_55131;
    private final double field_55132;

    protected LeavesParticle(ClientWorld world, double x, double y, double z, Sprite sprite, float gravity, float f, boolean bl, boolean bl2, float size, float initialYVelocity) {
        super(world, x, y, z, sprite);
        float g;
        this.angularVelocity = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.angularAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
        this.field_55127 = f;
        this.field_55128 = bl;
        this.field_55129 = bl2;
        this.maxAge = 300;
        this.gravityStrength = gravity * 1.2f * 0.0025f;
        this.scale = g = size * (this.random.nextBoolean() ? 0.05f : 0.075f);
        this.setBoundingBoxSpacing(g, g);
        this.velocityMultiplier = 1.0f;
        this.velocityY = -initialYVelocity;
        float h = this.random.nextFloat();
        this.field_55130 = Math.cos(Math.toRadians(h * 60.0f)) * (double)this.field_55127;
        this.field_55131 = Math.sin(Math.toRadians(h * 60.0f)) * (double)this.field_55127;
        this.field_55132 = Math.toRadians(1000.0f + h * 3000.0f);
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
        }
        if (this.dead) {
            return;
        }
        float f = 300 - this.maxAge;
        float g = Math.min(f / 300.0f, 1.0f);
        double d = 0.0;
        double e = 0.0;
        if (this.field_55129) {
            d += this.field_55130 * Math.pow(g, 1.25);
            e += this.field_55131 * Math.pow(g, 1.25);
        }
        if (this.field_55128) {
            d += (double)g * Math.cos((double)g * this.field_55132) * (double)this.field_55127;
            e += (double)g * Math.sin((double)g * this.field_55132) * (double)this.field_55127;
        }
        this.velocityX += d * (double)0.0025f;
        this.velocityZ += e * (double)0.0025f;
        this.velocityY -= (double)this.gravityStrength;
        this.angularVelocity += this.angularAcceleration / 20.0f;
        this.lastZRotation = this.zRotation;
        this.zRotation += this.angularVelocity / 20.0f;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.onGround || this.maxAge < 299 && (this.velocityX == 0.0 || this.velocityZ == 0.0)) {
            this.markDead();
        }
        if (this.dead) {
            return;
        }
        this.velocityX *= (double)this.velocityMultiplier;
        this.velocityY *= (double)this.velocityMultiplier;
        this.velocityZ *= (double)this.velocityMultiplier;
    }
}

