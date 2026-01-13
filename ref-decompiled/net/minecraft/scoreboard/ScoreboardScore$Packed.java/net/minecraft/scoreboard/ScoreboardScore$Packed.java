/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.scoreboard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public static final class ScoreboardScore.Packed
extends Record {
    final int value;
    final boolean locked;
    final Optional<Text> display;
    final Optional<NumberFormat> numberFormat;
    public static final MapCodec<ScoreboardScore.Packed> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.INT.optionalFieldOf("Score", (Object)0).forGetter(ScoreboardScore.Packed::value), (App)Codec.BOOL.optionalFieldOf("Locked", (Object)false).forGetter(ScoreboardScore.Packed::locked), (App)TextCodecs.CODEC.optionalFieldOf("display").forGetter(ScoreboardScore.Packed::display), (App)NumberFormatTypes.CODEC.optionalFieldOf("format").forGetter(ScoreboardScore.Packed::numberFormat)).apply((Applicative)instance, ScoreboardScore.Packed::new));

    public ScoreboardScore.Packed(int value, boolean locked, Optional<Text> display, Optional<NumberFormat> numberFormat) {
        this.value = value;
        this.locked = locked;
        this.display = display;
        this.numberFormat = numberFormat;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ScoreboardScore.Packed.class, "value;locked;display;numberFormat", "value", "locked", "display", "numberFormat"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ScoreboardScore.Packed.class, "value;locked;display;numberFormat", "value", "locked", "display", "numberFormat"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ScoreboardScore.Packed.class, "value;locked;display;numberFormat", "value", "locked", "display", "numberFormat"}, this, object);
    }

    public int value() {
        return this.value;
    }

    public boolean locked() {
        return this.locked;
    }

    public Optional<Text> display() {
        return this.display;
    }

    public Optional<NumberFormat> numberFormat() {
        return this.numberFormat;
    }
}
