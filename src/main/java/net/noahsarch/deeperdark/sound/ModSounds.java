package net.noahsarch.deeperdark.sound;

import net.noahsarch.deeperdark.Deeperdark;

/**
 * Custom sound events for the DeeperDark mod.
 *
 * NOTE: Sounds are NOT registered to the registry to avoid client/server registry sync issues.
 * Instead, we send PlaySoundS2CPacket directly with a Direct RegistryEntry (like /playsound does).
 * Clients with the resource pack will hear the sound, clients without will simply hear nothing.
 */
public class ModSounds {

    /**
     * Call this method during mod initialization.
     * No actual registration is needed since we use Direct RegistryEntry approach.
     */
    public static void registerSounds() {
        Deeperdark.LOGGER.info("[Deeper Dark] Custom sounds initialized (using direct packet approach)");
    }
}
