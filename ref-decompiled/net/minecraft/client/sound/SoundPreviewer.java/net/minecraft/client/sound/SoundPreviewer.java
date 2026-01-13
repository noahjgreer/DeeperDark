/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class SoundPreviewer {
    private static @Nullable SoundInstance currentSoundPreview;
    private static @Nullable SoundCategory category;

    public static void preview(SoundManager manager, SoundCategory category, float volume) {
        SoundPreviewer.stopPreviewOfOtherCategory(manager, category);
        if (SoundPreviewer.canPlaySound(manager)) {
            SoundEvent soundEvent;
            switch (category) {
                case RECORDS: {
                    SoundEvent soundEvent2 = SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value();
                    break;
                }
                case WEATHER: {
                    SoundEvent soundEvent2 = SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER;
                    break;
                }
                case BLOCKS: {
                    SoundEvent soundEvent2 = SoundEvents.BLOCK_GRASS_PLACE;
                    break;
                }
                case HOSTILE: {
                    SoundEvent soundEvent2 = SoundEvents.ENTITY_ZOMBIE_AMBIENT;
                    break;
                }
                case NEUTRAL: {
                    SoundEvent soundEvent2 = SoundEvents.ENTITY_COW_AMBIENT;
                    break;
                }
                case PLAYERS: {
                    SoundEvent soundEvent2 = SoundEvents.ENTITY_GENERIC_EAT.value();
                    break;
                }
                case AMBIENT: {
                    SoundEvent soundEvent2 = SoundEvents.AMBIENT_CAVE.value();
                    break;
                }
                case UI: {
                    SoundEvent soundEvent2 = SoundEvents.UI_BUTTON_CLICK.value();
                    break;
                }
                default: {
                    SoundEvent soundEvent2 = soundEvent = SoundEvents.INTENTIONALLY_EMPTY;
                }
            }
            if (soundEvent != SoundEvents.INTENTIONALLY_EMPTY) {
                currentSoundPreview = PositionedSoundInstance.ui(soundEvent, 1.0f, volume);
                manager.play(currentSoundPreview);
            }
        }
    }

    private static void stopPreviewOfOtherCategory(SoundManager manager, SoundCategory category) {
        if (SoundPreviewer.category != category) {
            SoundPreviewer.category = category;
            if (currentSoundPreview != null) {
                manager.stop(currentSoundPreview);
            }
        }
    }

    private static boolean canPlaySound(SoundManager manager) {
        return currentSoundPreview == null || !manager.isPlaying(currentSoundPreview);
    }
}
