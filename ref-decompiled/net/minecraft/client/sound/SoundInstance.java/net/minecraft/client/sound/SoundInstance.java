/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.sound.v1.FabricSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface SoundInstance
extends FabricSoundInstance {
    public Identifier getId();

    public @Nullable WeightedSoundSet getSoundSet(SoundManager var1);

    public @Nullable Sound getSound();

    public SoundCategory getCategory();

    public boolean isRepeatable();

    public boolean isRelative();

    public int getRepeatDelay();

    public float getVolume();

    public float getPitch();

    public double getX();

    public double getY();

    public double getZ();

    public AttenuationType getAttenuationType();

    default public boolean shouldAlwaysPlay() {
        return false;
    }

    default public boolean canPlay() {
        return true;
    }

    public static Random createRandom() {
        return Random.create();
    }

    @Environment(value=EnvType.CLIENT)
    public static final class AttenuationType
    extends Enum<AttenuationType> {
        public static final /* enum */ AttenuationType NONE = new AttenuationType();
        public static final /* enum */ AttenuationType LINEAR = new AttenuationType();
        private static final /* synthetic */ AttenuationType[] field_5477;

        public static AttenuationType[] values() {
            return (AttenuationType[])field_5477.clone();
        }

        public static AttenuationType valueOf(String string) {
            return Enum.valueOf(AttenuationType.class, string);
        }

        private static /* synthetic */ AttenuationType[] method_36927() {
            return new AttenuationType[]{NONE, LINEAR};
        }

        static {
            field_5477 = AttenuationType.method_36927();
        }
    }
}
