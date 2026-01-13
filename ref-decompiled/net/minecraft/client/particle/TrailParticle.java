/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.TrailParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class TrailParticle
extends BillboardParticle {
    private final Vec3d target;

    TrailParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Vec3d target, int color, Sprite sprite) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, sprite);
        color = ColorHelper.scaleRgb((int)color, (float)(0.875f + this.random.nextFloat() * 0.25f), (float)(0.875f + this.random.nextFloat() * 0.25f), (float)(0.875f + this.random.nextFloat() * 0.25f));
        this.red = (float)ColorHelper.getRed((int)color) / 255.0f;
        this.green = (float)ColorHelper.getGreen((int)color) / 255.0f;
        this.blue = (float)ColorHelper.getBlue((int)color) / 255.0f;
        this.scale = 0.26f;
        this.target = target;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        int i = this.maxAge - this.age;
        double d = 1.0 / (double)i;
        this.x = MathHelper.lerp((double)d, (double)this.x, (double)this.target.getX());
        this.y = MathHelper.lerp((double)d, (double)this.y, (double)this.target.getY());
        this.z = MathHelper.lerp((double)d, (double)this.z, (double)this.target.getZ());
    }

    public int getBrightness(float tint) {
        return 0xF000F0;
    }
}

