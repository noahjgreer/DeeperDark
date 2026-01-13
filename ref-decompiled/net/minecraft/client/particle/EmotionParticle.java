/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.EmotionParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EmotionParticle
extends BillboardParticle {
    EmotionParticle(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0, sprite);
        this.ascending = true;
        this.velocityMultiplier = 0.86f;
        this.velocityX *= (double)0.01f;
        this.velocityY *= (double)0.01f;
        this.velocityZ *= (double)0.01f;
        this.velocityY += 0.1;
        this.scale *= 1.5f;
        this.maxAge = 16;
        this.collidesWithWorld = false;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f), (float)0.0f, (float)1.0f);
    }
}

