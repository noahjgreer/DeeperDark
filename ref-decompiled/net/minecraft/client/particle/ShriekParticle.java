/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BillboardParticleSubmittable
 *  net.minecraft.client.particle.ShriekParticle
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Quaternionf
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class ShriekParticle
extends BillboardParticle {
    private static final float X_ROTATION = 1.0472f;
    private int delay;

    ShriekParticle(ClientWorld world, double x, double y, double z, int delay, Sprite sprite) {
        super(world, x, y, z, 0.0, 0.0, 0.0, sprite);
        this.scale = 0.85f;
        this.delay = delay;
        this.maxAge = 30;
        this.gravityStrength = 0.0f;
        this.velocityX = 0.0;
        this.velocityY = 0.1;
        this.velocityZ = 0.0;
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge * 0.75f), (float)0.0f, (float)1.0f);
    }

    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
        if (this.delay > 0) {
            return;
        }
        this.alpha = 1.0f - MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge), (float)0.0f, (float)1.0f);
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationX(-1.0472f);
        this.render(submittable, camera, quaternionf, tickProgress);
        quaternionf.rotationYXZ((float)(-Math.PI), 1.0472f, 0.0f);
        this.render(submittable, camera, quaternionf, tickProgress);
    }

    public int getBrightness(float tint) {
        return 240;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public void tick() {
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        super.tick();
    }
}

