/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.EndLightFlashManager
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class EndLightFlashManager {
    public static final int field_62214 = 30;
    private static final int INTERVAL = 600;
    private static final int MAX_START_TIME = 200;
    private static final int MIN_DURATION = 100;
    private static final int field_62035 = 380;
    private long currentWindow;
    private int startTime;
    private int duration;
    private float nextSkyFactor;
    private float lastSkyFactor;
    private float pitch;
    private float yaw;

    public void tick(long time) {
        this.update(time);
        this.lastSkyFactor = this.nextSkyFactor;
        this.nextSkyFactor = this.calcSkyFactor(time);
    }

    private void update(long time) {
        long l = time / 600L;
        if (l != this.currentWindow) {
            Random random = Random.create((long)l);
            random.nextFloat();
            this.startTime = MathHelper.nextBetween((Random)random, (int)0, (int)200);
            this.duration = MathHelper.nextBetween((Random)random, (int)100, (int)Math.min(380, 600 - this.startTime));
            this.pitch = MathHelper.nextBetween((Random)random, (float)-60.0f, (float)10.0f);
            this.yaw = MathHelper.nextBetween((Random)random, (float)-180.0f, (float)180.0f);
            this.currentWindow = l;
        }
    }

    private float calcSkyFactor(long time) {
        long l = time % 600L;
        if (l < (long)this.startTime || l > (long)(this.startTime + this.duration)) {
            return 0.0f;
        }
        return MathHelper.sin((double)((float)(l - (long)this.startTime) * (float)Math.PI / (float)this.duration));
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getSkyFactor(float tickProgress) {
        return MathHelper.lerp((float)tickProgress, (float)this.lastSkyFactor, (float)this.nextSkyFactor);
    }

    public boolean shouldFlash() {
        return this.nextSkyFactor > 0.0f && this.lastSkyFactor <= 0.0f;
    }
}

