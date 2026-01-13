/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.FireflyParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FireflyParticle
extends BillboardParticle {
    private static final float field_56803 = 0.3f;
    private static final float field_56804 = 0.1f;
    private static final float field_56801 = 0.5f;
    private static final float field_56802 = 0.3f;
    private static final int MIN_MAX_AGE = 200;
    private static final int MAX_MAX_AGE = 300;

    FireflyParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
        this.ascending = true;
        this.velocityMultiplier = 0.96f;
        this.scale *= 0.75f;
        this.velocityY *= (double)0.8f;
        this.velocityX *= (double)0.8f;
        this.velocityZ *= (double)0.8f;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public int getBrightness(float tint) {
        return (int)(255.0f * FireflyParticle.method_67878((float)this.method_67879((float)this.age + tint), (float)0.1f, (float)0.3f));
    }

    public void tick() {
        super.tick();
        if (!this.world.getBlockState(BlockPos.ofFloored((double)this.x, (double)this.y, (double)this.z)).isAir()) {
            this.markDead();
            return;
        }
        this.setAlpha(FireflyParticle.method_67878((float)this.method_67879((float)this.age), (float)0.3f, (float)0.5f));
        if (this.random.nextFloat() > 0.95f || this.age == 1) {
            this.setVelocity((double)(-0.05f + 0.1f * this.random.nextFloat()), (double)(-0.05f + 0.1f * this.random.nextFloat()), (double)(-0.05f + 0.1f * this.random.nextFloat()));
        }
    }

    private float method_67879(float f) {
        return MathHelper.clamp((float)(f / (float)this.maxAge), (float)0.0f, (float)1.0f);
    }

    private static float method_67878(float f, float g, float h) {
        if (f >= 1.0f - g) {
            return (1.0f - f) / g;
        }
        if (f <= h) {
            return f / h;
        }
        return 1.0f;
    }
}

