package net.noahsarch.deeperdark.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.noahsarch.deeperdark.Deeperdark;

/**
 * Custom sound events for the DeeperDark mod.
 *
 * NOTE: These sounds are NOT registered to the registry to avoid
 * client/server registry sync issues. Instead, they are created
 * directly with SoundEvent.of() which allows the server to send
 * the sound ID to clients. Clients with the mod will play the sound
 * from their resources, and clients without the mod will simply
 * not hear anything (no error).
 */
public class ModSounds {

    /**
     * The organ note block sound - plays when a noteblock is placed above a copper block.
     * This is created directly without registry registration to avoid sync issues.
     */
    public static final SoundEvent BLOCK_NOTE_BLOCK_ORGAN = SoundEvent.of(
            Identifier.of(Deeperdark.MOD_ID, "block.note_block.organ")
    );

    /**
     * Call this method during mod initialization to initialize sounds.
     * Since we don't register to the registry, this just logs that sounds are ready.
     */
    public static void registerSounds() {
        Deeperdark.LOGGER.info("[Deeper Dark] Custom sounds initialized (not registry-synced)");
    }
}
