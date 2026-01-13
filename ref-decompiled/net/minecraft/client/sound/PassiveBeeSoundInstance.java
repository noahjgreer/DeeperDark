/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.AbstractBeeSoundInstance
 *  net.minecraft.client.sound.AggressiveBeeSoundInstance
 *  net.minecraft.client.sound.MovingSoundInstance
 *  net.minecraft.client.sound.PassiveBeeSoundInstance
 *  net.minecraft.entity.passive.BeeEntity
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AbstractBeeSoundInstance;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

@Environment(value=EnvType.CLIENT)
public class PassiveBeeSoundInstance
extends AbstractBeeSoundInstance {
    public PassiveBeeSoundInstance(BeeEntity entity) {
        super(entity, SoundEvents.ENTITY_BEE_LOOP, SoundCategory.NEUTRAL);
    }

    protected MovingSoundInstance getReplacement() {
        return new AggressiveBeeSoundInstance(this.bee);
    }

    protected boolean shouldReplace() {
        return this.bee.hasAngerTime();
    }
}

