/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.SoundContainer
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.SoundSystem
 *  net.minecraft.client.sound.WeightedSoundSet
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.random.Random
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WeightedSoundSet
implements SoundContainer<Sound> {
    private final List<SoundContainer<Sound>> sounds = Lists.newArrayList();
    private final @Nullable Text subtitle;

    public WeightedSoundSet(Identifier id, @Nullable String subtitle) {
        if (SharedConstants.SUBTITLES) {
            MutableText mutableText = Text.literal((String)id.getPath());
            if ("FOR THE DEBUG!".equals(subtitle)) {
                mutableText = mutableText.append((Text)Text.literal((String)" missing").formatted(Formatting.RED));
            }
            this.subtitle = mutableText;
        } else {
            this.subtitle = subtitle == null ? null : Text.translatable((String)subtitle);
        }
    }

    public int getWeight() {
        int i = 0;
        for (SoundContainer soundContainer : this.sounds) {
            i += soundContainer.getWeight();
        }
        return i;
    }

    public Sound getSound(Random random) {
        int i = this.getWeight();
        if (this.sounds.isEmpty() || i == 0) {
            return SoundManager.MISSING_SOUND;
        }
        int j = random.nextInt(i);
        for (SoundContainer soundContainer : this.sounds) {
            if ((j -= soundContainer.getWeight()) >= 0) continue;
            return (Sound)soundContainer.getSound(random);
        }
        return SoundManager.MISSING_SOUND;
    }

    public void add(SoundContainer<Sound> container) {
        this.sounds.add(container);
    }

    public @Nullable Text getSubtitle() {
        return this.subtitle;
    }

    public void preload(SoundSystem soundSystem) {
        for (SoundContainer soundContainer : this.sounds) {
            soundContainer.preload(soundSystem);
        }
    }

    public /* synthetic */ Object getSound(Random random) {
        return this.getSound(random);
    }
}

