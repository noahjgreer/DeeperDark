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
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public static class FireworksSparkParticle.Flash
extends BillboardParticle {
    FireworksSparkParticle.Flash(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e, f, sprite);
        this.maxAge = 4;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    @Override
    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
        this.setAlpha(0.6f - ((float)this.age + tickProgress - 1.0f) * 0.25f * 0.5f);
        super.render(submittable, camera, tickProgress);
    }

    @Override
    public float getSize(float tickProgress) {
        return 7.1f * MathHelper.sin(((float)this.age + tickProgress - 1.0f) * 0.25f * (float)Math.PI);
    }
}
