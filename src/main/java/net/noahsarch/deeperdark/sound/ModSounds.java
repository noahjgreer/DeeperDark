package net.noahsarch.deeperdark.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.noahsarch.deeperdark.Deeperdark;

/**
 * Custom sound events for the DeeperDark mod.
 *
 * NOTE: Most sounds are NOT registered to the registry to avoid client/server registry sync issues.
 * Instead, we send ClientboundSoundPacket directly with a Direct Holder (like /playsound does).
 * Clients with the resource pack will hear the sound, clients without will simply hear nothing.
 *
 * Exception: sounds used by BlockSetType (e.g. door open/close) must be registry-registered so
 * the vanilla block machinery can play them correctly via level.playSound().
 */
public class ModSounds {

    public static final SoundEvent GLASS_DOOR_OPEN  = register("block.glass_door.open");
    public static final SoundEvent GLASS_DOOR_CLOSE = register("block.glass_door.close");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
    }

    public static void registerSounds() {
        Deeperdark.LOGGER.info("[Deeper Dark] Custom sounds initialized");
    }
}
