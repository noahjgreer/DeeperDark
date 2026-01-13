/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.GlowParticle
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
public class GlowParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    GlowParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getFirst());
        this.velocityMultiplier = 0.96f;
        this.ascending = true;
        this.spriteProvider = spriteProvider;
        this.scale *= 0.75f;
        this.collidesWithWorld = false;
        this.updateSprite(spriteProvider);
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public int getBrightness(float tint) {
        float f = ((float)this.age + tint) / (float)this.maxAge;
        f = MathHelper.clamp((float)f, (float)0.0f, (float)1.0f);
        int i = super.getBrightness(tint);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((j += (int)(f * 15.0f * 16.0f)) > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
    }
}

