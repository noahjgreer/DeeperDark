/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.AbstractSoundInstance
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstance$AttenuationType
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PositionedSoundInstance
extends AbstractSoundInstance {
    public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, BlockPos pos) {
        this(sound, category, volume, pitch, random, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
    }

    public static PositionedSoundInstance ui(SoundEvent sound, float pitch) {
        return PositionedSoundInstance.ui((SoundEvent)sound, (float)pitch, (float)0.25f);
    }

    public static PositionedSoundInstance ui(RegistryEntry<SoundEvent> sound, float pitch) {
        return PositionedSoundInstance.ui((SoundEvent)((SoundEvent)sound.value()), (float)pitch);
    }

    public static PositionedSoundInstance ui(SoundEvent sound, float pitch, float volume) {
        return new PositionedSoundInstance(sound.id(), SoundCategory.UI, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static PositionedSoundInstance music(SoundEvent sound) {
        return new PositionedSoundInstance(sound.id(), SoundCategory.MUSIC, 1.0f, 1.0f, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static PositionedSoundInstance record(SoundEvent sound, Vec3d pos) {
        return new PositionedSoundInstance(sound, SoundCategory.RECORDS, 4.0f, 1.0f, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.LINEAR, pos.x, pos.y, pos.z);
    }

    public static PositionedSoundInstance ambient(SoundEvent sound, float pitch, float volume) {
        return new PositionedSoundInstance(sound.id(), SoundCategory.AMBIENT, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
    }

    public static PositionedSoundInstance ambient(SoundEvent sound) {
        return PositionedSoundInstance.ambient((SoundEvent)sound, (float)1.0f, (float)1.0f);
    }

    public static PositionedSoundInstance ambient(SoundEvent sound, Random random, double x, double y, double z) {
        return new PositionedSoundInstance(sound, SoundCategory.AMBIENT, 1.0f, 1.0f, random, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
    }

    public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, double x, double y, double z) {
        this(sound, category, volume, pitch, random, false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z);
    }

    private PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, boolean repeat, int repeatDelay, SoundInstance.AttenuationType attenuationType, double x, double y, double z) {
        this(sound.id(), category, volume, pitch, random, repeat, repeatDelay, attenuationType, x, y, z, false);
    }

    public PositionedSoundInstance(Identifier id, SoundCategory category, float volume, float pitch, Random random, boolean repeat, int repeatDelay, SoundInstance.AttenuationType attenuationType, double x, double y, double z, boolean relative) {
        super(id, category, random);
        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
        this.attenuationType = attenuationType;
        this.relative = relative;
    }
}

