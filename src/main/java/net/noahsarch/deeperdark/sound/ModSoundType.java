package net.noahsarch.deeperdark.sound;

import net.minecraft.world.level.block.SoundType;

public class ModSoundType extends SoundType {
    public static final SoundType BOX = new ModSoundType(1.0F, 1.0F);

    public ModSoundType(float volume, float pitch) {
        super(volume, pitch,
            ModSounds.BOX_BREAK, // break
            ModSounds.BOX_STEP,  // step
            ModSounds.BOX_HIT, // place
            ModSounds.BOX_HIT,   // hit
            ModSounds.BOX_BREAK   // fall
        );
    }
}
