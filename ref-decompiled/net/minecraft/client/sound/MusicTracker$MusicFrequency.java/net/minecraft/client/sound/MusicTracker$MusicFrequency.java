/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.MusicSound;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static final class MusicTracker.MusicFrequency
extends Enum<MusicTracker.MusicFrequency>
implements StringIdentifiable {
    public static final /* enum */ MusicTracker.MusicFrequency DEFAULT = new MusicTracker.MusicFrequency("DEFAULT", "options.music_frequency.default", 20);
    public static final /* enum */ MusicTracker.MusicFrequency FREQUENT = new MusicTracker.MusicFrequency("FREQUENT", "options.music_frequency.frequent", 10);
    public static final /* enum */ MusicTracker.MusicFrequency CONSTANT = new MusicTracker.MusicFrequency("CONSTANT", "options.music_frequency.constant", 0);
    public static final Codec<MusicTracker.MusicFrequency> CODEC;
    private final String name;
    private final int delayBetweenTracks;
    private final Text text;
    private static final /* synthetic */ MusicTracker.MusicFrequency[] field_60805;

    public static MusicTracker.MusicFrequency[] values() {
        return (MusicTracker.MusicFrequency[])field_60805.clone();
    }

    public static MusicTracker.MusicFrequency valueOf(String string) {
        return Enum.valueOf(MusicTracker.MusicFrequency.class, string);
    }

    private MusicTracker.MusicFrequency(String name, String translationKey, int minutesBetweenTracks) {
        this.name = name;
        this.delayBetweenTracks = minutesBetweenTracks * 1200;
        this.text = Text.translatable(translationKey);
    }

    int getDelayBeforePlaying(@Nullable MusicSound music, Random random) {
        if (music == null) {
            return this.delayBetweenTracks;
        }
        if (this == CONSTANT) {
            return 100;
        }
        int i = Math.min(music.minDelay(), this.delayBetweenTracks);
        int j = Math.min(music.maxDelay(), this.delayBetweenTracks);
        return MathHelper.nextInt(random, i, j);
    }

    public Text getText() {
        return this.text;
    }

    @Override
    public String asString() {
        return this.name;
    }

    private static /* synthetic */ MusicTracker.MusicFrequency[] method_71936() {
        return new MusicTracker.MusicFrequency[]{DEFAULT, FREQUENT, CONSTANT};
    }

    static {
        field_60805 = MusicTracker.MusicFrequency.method_71936();
        CODEC = StringIdentifiable.createCodec(MusicTracker.MusicFrequency::values);
    }
}
