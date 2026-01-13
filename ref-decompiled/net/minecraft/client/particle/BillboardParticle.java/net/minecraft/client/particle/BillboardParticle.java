/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

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
            quaternionf.rotateZ(MathHelper.lerp(tickProgress, this.lastZRotation, this.zRotation));
        }
        this.render(submittable, camera, quaternionf, tickProgress);
    }

    protected void render(BillboardParticleSubmittable submittable, Camera camera, Quaternionf rotation, float tickProgress) {
        Vec3d vec3d = camera.getCameraPos();
        float f = (float)(MathHelper.lerp((double)tickProgress, this.lastX, this.x) - vec3d.getX());
        float g = (float)(MathHelper.lerp((double)tickProgress, this.lastY, this.y) - vec3d.getY());
        float h = (float)(MathHelper.lerp((double)tickProgress, this.lastZ, this.z) - vec3d.getZ());
        this.renderVertex(submittable, rotation, f, g, h, tickProgress);
    }

    protected void renderVertex(BillboardParticleSubmittable submittable, Quaternionf rotation, float x, float y, float z, float tickProgress) {
        submittable.render(this.getRenderType(), x, y, z, rotation.x, rotation.y, rotation.z, rotation.w, this.getSize(tickProgress), this.getMinU(), this.getMaxU(), this.getMinV(), this.getMaxV(), ColorHelper.fromFloats(this.alpha, this.red, this.green, this.blue), this.getBrightness(tickProgress));
    }

    public float getSize(float tickProgress) {
        return this.scale;
    }

    @Override
    public Particle scale(float scale) {
        this.scale *= scale;
        return super.scale(scale);
    }

    @Override
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.red + "," + this.green + "," + this.blue + "," + this.alpha + "), Age " + this.age;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Rotator {
        public static final Rotator ALL_AXIS = (quaternion, camera, tickProgress) -> quaternion.set((Quaternionfc)camera.getRotation());
        public static final Rotator Y_AND_W_ONLY = (quaternion, camera, tickProgress) -> quaternion.set(0.0f, camera.getRotation().y, 0.0f, camera.getRotation().w);

        public void setRotation(Quaternionf var1, Camera var2, float var3);
    }

    @Environment(value=EnvType.CLIENT)
    public record RenderType(boolean translucent, Identifier textureAtlasLocation, RenderPipeline pipeline) {
        public static final RenderType BLOCK_ATLAS_TRANSLUCENT = new RenderType(true, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, RenderPipelines.TRANSLUCENT_PARTICLE);
        public static final RenderType ITEM_ATLAS_TRANSLUCENT = new RenderType(true, SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE, RenderPipelines.TRANSLUCENT_PARTICLE);
        public static final RenderType PARTICLE_ATLAS_OPAQUE = new RenderType(false, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, RenderPipelines.OPAQUE_PARTICLE);
        public static final RenderType PARTICLE_ATLAS_TRANSLUCENT = new RenderType(true, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, RenderPipelines.TRANSLUCENT_PARTICLE);
    }
}
