/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.OminousSpawningParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class OminousSpawningParticle
extends BillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;
    private final int fromColor;
    private final int toColor;

    OminousSpawningParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int fromColor, int toColor, Sprite sprite) {
        super(world, x, y, z, sprite);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.lastX = x + velocityX;
        this.lastY = y + velocityY;
        this.lastZ = z + velocityZ;
        this.x = this.lastX;
        this.y = this.lastY;
        this.z = this.lastZ;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.2f);
        this.collidesWithWorld = false;
        this.maxAge = (int)(this.random.nextFloat() * 5.0f) + 25;
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public void move(double dx, double dy, double dz) {
    }

    public int getBrightness(float tint) {
        return 240;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        float f = (float)this.age / (float)this.maxAge;
        float g = 1.0f - f;
        this.x = this.startX + this.velocityX * (double)g;
        this.y = this.startY + this.velocityY * (double)g;
        this.z = this.startZ + this.velocityZ * (double)g;
        int i = ColorHelper.lerp((float)f, (int)this.fromColor, (int)this.toColor);
        this.setColor((float)ColorHelper.getRed((int)i) / 255.0f, (float)ColorHelper.getGreen((int)i) / 255.0f, (float)ColorHelper.getBlue((int)i) / 255.0f);
        this.setAlpha((float)ColorHelper.getAlpha((int)i) / 255.0f);
    }
}

