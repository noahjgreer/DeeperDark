/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.SoundPreviewer
 *  net.minecraft.client.sound.SoundPreviewer$1
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundPreviewer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class SoundPreviewer {
    private static @Nullable SoundInstance currentSoundPreview;
    private static @Nullable SoundCategory category;

    public static void preview(SoundManager manager, SoundCategory category, float volume) {
        SoundPreviewer.stopPreviewOfOtherCategory((SoundManager)manager, (SoundCategory)category);
        if (SoundPreviewer.canPlaySound((SoundManager)manager)) {
            SoundEvent soundEvent;
            switch (1.field_62994[category.ordinal()]) {
                case 1: {
                    SoundEvent soundEvent2 = (SoundEvent)SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value();
                    break;
                }
                case 2: {
                    SoundEvent soundEvent2 = SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER;
                    break;
                }
                case 3: {
                    SoundEvent soundEvent2 = SoundEvents.BLOCK_GRASS_PLACE;
                    break;
                }
                case 4: {
                    SoundEvent soundEvent2 = SoundEvents.ENTITY_ZOMBIE_AMBIENT;
                    break;
                }
                case 5: {
                    SoundEvent soundEvent2 = SoundEvents.ENTITY_COW_AMBIENT;
                    break;
                }
                case 6: {
                    SoundEvent soundEvent2 = (SoundEvent)SoundEvents.ENTITY_GENERIC_EAT.value();
                    break;
                }
                case 7: {
                    SoundEvent soundEvent2 = (SoundEvent)SoundEvents.AMBIENT_CAVE.value();
                    break;
                }
                case 8: {
                    SoundEvent soundEvent2 = (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value();
                    break;
                }
                default: {
                    SoundEvent soundEvent2 = soundEvent = SoundEvents.INTENTIONALLY_EMPTY;
                }
            }
            if (soundEvent != SoundEvents.INTENTIONALLY_EMPTY) {
                currentSoundPreview = PositionedSoundInstance.ui((SoundEvent)soundEvent, (float)1.0f, (float)volume);
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

