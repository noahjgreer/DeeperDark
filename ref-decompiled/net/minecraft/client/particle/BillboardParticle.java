/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BillboardParticle$Rotator
 *  net.minecraft.client.particle.BillboardParticleSubmittable
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleTextureSheet
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionf
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public abstract class BillboardParticle
extends Particle {
    protected float scale;
    protected float red = 1.0f;
    protected float green = 1.0f;
    protected float blue = 1.0f;
    protected float alpha = 1.0f;
    protected float zRotation;
    protected float lastZRotation;
    protected Sprite sprite;

    protected BillboardParticle(ClientWorld world, double x, double y, double z, Sprite sprite) {
        super(world, x, y, z);
        this.sprite = sprite;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected BillboardParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Sprite sprite) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.sprite = sprite;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    public Rotator getRotator() {
        return Rotator.ALL_AXIS;
    }

    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
        Quaternionf quaternionf = new Quaternionf();
        this.getRotator().setRotation(quaternionf, camera, tickProgress);
        if (this.zRotation != 0.0f) {
            quaternionf.rotateZ(MathHelper.lerp((float)tickProgress, (float)this.lastZRotation, (float)this.zRotation));
        }
        this.render(submittable, camera, quaternionf, tickProgress);
    }

    protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress) {
        Vec3d vec3d = camera.getCameraPos();
        float f = (float)(MathHelper.lerp((double)tickProgress, (double)this.lastX, (double)this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickProgress, (double)this.lastY, (double)this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickProgress, (double)this.lastZ, (double)this.z) - vec3d.getZ());
        this.renderVertex(submittable, rotation, f, g, h, tickProgress);
    }

    protected void renderVertex(BillboardParticleSubmittable submittable, Quaternionf rotation, float x, float y, float z, float tickProgress) {
        submittable.render(this.getRenderType(), x, y, z, rotation.x, rotation.y, rotation.z, rotation.w, this.getSize(tickProgress), this.getMinU(), this.getMaxU(), this.getMinV(), this.getMaxV(), ColorHelper.fromFloats((float)this.alpha, (float)this.red, (float)this.green, (float)this.blue), this.getBrightness(tickProgress));
    }

    public float getSize(float tickProgress) {
        return this.scale;
    }

    public Particle scale(float scale) {
        this.scale *= scale;
        return super.scale(scale);
    }

    public ParticleTextureSheet textureSheet() {
        return ParticleTextureSheet.SINGLE_QUADS;
    }

    public void updateSprite(SpriteProvider spriteProvider) {
        if (!this.dead) {
            this.setSprite(spriteProvider.getSprite(this.age, this.maxAge));
        }
    }

    protected void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    protected float getMinU() {
        return this.sprite.getMinU();
    }

    protected float getMaxU() {
        return this.sprite.getMaxU();
    }

    protected float getMinV() {
        return this.sprite.getMinV();
    }

    protected float getMaxV() {
        return this.sprite.getMaxV();
    }

    protected abstract RenderType getRenderType();

    public void setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    protected void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.red + "," + this.green + "," + this.blue + "," + this.alpha + "), Age " + this.age;
    }
}

