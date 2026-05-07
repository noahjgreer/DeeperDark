package net.noahsarch.deeperdark.sound;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public class ModSoundType extends SoundType {
    public static final SoundType BOX = new ModSoundType(1.0F, 1.0F,
        ModSounds.BOX_BREAK,
        ModSounds.BOX_STEP,
        ModSounds.BOX_HIT,
        ModSounds.BOX_HIT,
        ModSounds.BOX_BREAK
    );
    public static final SoundType ENDER_PEARL_BLOCK = new ModSoundType(1.0F, 1.0F,
        net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_BREAK,
        net.minecraft.sounds.SoundEvents.RESIN_BRICKS_STEP,
        net.minecraft.sounds.SoundEvents.END_PORTAL_FRAME_FILL,
        net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_HIT,
        net.minecraft.sounds.SoundEvents.AMETHYST_BLOCK_BREAK
    );

    public ModSoundType(float volume, float pitch,
            SoundEvent breakSound, SoundEvent stepSound,
            SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
        super(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }
}
