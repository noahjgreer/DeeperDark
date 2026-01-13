/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
            MutableText mutableText = Text.literal(id.getPath());
            if ("FOR THE DEBUG!".equals(subtitle)) {
                mutableText = mutableText.append(Text.literal(" missing").formatted(Formatting.RED));
            }
            this.subtitle = mutableText;
        } else {
            this.subtitle = subtitle == null ? null : Text.translatable(subtitle);
        }
    }

    @Override
    public int getWeight() {
        int i = 0;
        for (SoundContainer<Sound> soundContainer : this.sounds) {
            i += soundContainer.getWeight();
        }
        return i;
    }

    @Override
    public Sound getSound(Random random) {
        int i = this.getWeight();
        if (this.sounds.isEmpty() || i == 0) {
            return SoundManager.MISSING_SOUND;
        }
        int j = random.nextInt(i);
        for (SoundContainer<Sound> soundContainer : this.sounds) {
            if ((j -= soundContainer.getWeight()) >= 0) continue;
            return soundContainer.getSound(random);
        }
        return SoundManager.MISSING_SOUND;
    }

    public void add(SoundContainer<Sound> container) {
        this.sounds.add(container);
    }

    public @Nullable Text getSubtitle() {
        return this.subtitle;
    }

    @Override
    public void preload(SoundSystem soundSystem) {
        for (SoundContainer<Sound> soundContainer : this.sounds) {
            soundContainer.preload(soundSystem);
        }
    }

    @Override
    public /* synthetic */ Object getSound(Random random) {
        return this.getSound(random);
    }
}
