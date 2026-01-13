/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.sound.AbstractBeeSoundInstance
 *  net.minecraft.client.sound.MovingSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.TickableSoundInstance
 *  net.minecraft.entity.passive.BeeEntity
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractBeeSoundInstance
extends MovingSoundInstance {
    private static final float field_32991 = 0.0f;
    private static final float field_32992 = 1.2f;
    private static final float field_32993 = 0.0f;
    protected final BeeEntity bee;
    private boolean replaced;

    public AbstractBeeSoundInstance(BeeEntity entity, SoundEvent sound, SoundCategory soundCategory) {
        super(sound, soundCategory, SoundInstance.createRandom());
        this.bee = entity;
        this.x = (float)entity.getX();
        this.y = (float)entity.getY();
        this.z = (float)entity.getZ();
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.0f;
    }

    public void tick() {
        boolean bl = this.shouldReplace();
        if (bl && !this.isDone()) {
            MinecraftClient.getInstance().getSoundManager().playNextTick((TickableSoundInstance)this.getReplacement());
            this.replaced = true;
        }
        if (this.bee.isRemoved() || this.replaced) {
            this.setDone();
            return;
        }
        this.x = (float)this.bee.getX();
        this.y = (float)this.bee.getY();
        this.z = (float)this.bee.getZ();
        float f = (float)this.bee.getVelocity().horizontalLength();
        if (f >= 0.01f) {
            this.pitch = MathHelper.lerp((float)MathHelper.clamp((float)f, (float)this.getMinPitch(), (float)this.getMaxPitch()), (float)this.getMinPitch(), (float)this.getMaxPitch());
            this.volume = MathHelper.lerp((float)MathHelper.clamp((float)f, (float)0.0f, (float)0.5f), (float)0.0f, (float)1.2f);
        } else {
            this.pitch = 0.0f;
            this.volume = 0.0f;
        }
    }

    private float getMinPitch() {
        if (this.bee.isBaby()) {
            return 1.1f;
        }
        return 0.7f;
    }

    private float getMaxPitch() {
        if (this.bee.isBaby()) {
            return 1.5f;
        }
        return 1.1f;
    }

    public boolean shouldAlwaysPlay() {
        return true;
    }

    public boolean canPlay() {
        return !this.bee.isSilent();
    }

    protected abstract MovingSoundInstance getReplacement();

    protected abstract boolean shouldReplace();
}

