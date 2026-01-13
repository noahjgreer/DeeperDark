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
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class NoteParticle
extends BillboardParticle {
    NoteParticle(ClientWorld world, double x, double y, double z, double velocityX, Sprite sprite) {
        super(world, x, y, z, 0.0, 0.0, 0.0, sprite);
        this.velocityMultiplier = 0.66f;
        this.ascending = true;
        this.velocityX *= (double)0.01f;
        this.velocityY *= (double)0.01f;
        this.velocityZ *= (double)0.01f;
        this.velocityY += 0.2;
        this.red = Math.max(0.0f, MathHelper.sin(((float)velocityX + 0.0f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.green = Math.max(0.0f, MathHelper.sin(((float)velocityX + 0.33333334f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.blue = Math.max(0.0f, MathHelper.sin(((float)velocityX + 0.6666667f) * ((float)Math.PI * 2)) * 0.65f + 0.35f);
        this.scale *= 1.5f;
        this.maxAge = 6;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Override
    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            NoteParticle noteParticle = new NoteParticle(clientWorld, d, e, f, g, this.spriteProvider.getSprite(random));
            return noteParticle;
        }
    }
}
