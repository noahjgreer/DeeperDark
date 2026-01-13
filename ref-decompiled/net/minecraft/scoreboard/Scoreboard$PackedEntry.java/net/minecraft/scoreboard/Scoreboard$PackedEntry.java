/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.scoreboard;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.scoreboard.ScoreboardScore;

public static final class Scoreboard.PackedEntry
extends Record {
    final String owner;
    final String objective;
    final ScoreboardScore.Packed score;
    public static final Codec<Scoreboard.PackedEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("Name").forGetter(Scoreboard.PackedEntry::owner), (App)Codec.STRING.fieldOf("Objective").forGetter(Scoreboard.PackedEntry::objective), (App)ScoreboardScore.Packed.CODEC.forGetter(Scoreboard.PackedEntry::score)).apply((Applicative)instance, Scoreboard.PackedEntry::new));

    public Scoreboard.PackedEntry(String owner, String objective, ScoreboardScore.Packed score) {
        this.owner = owner;
        this.objective = objective;
        this.score = score;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{Scoreboard.PackedEntry.class, "owner;objective;score", "owner", "objective", "score"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Scoreboard.PackedEntry.class, "owner;objective;score", "owner", "objective", "score"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Scoreboard.PackedEntry.class, "owner;objective;score", "owner", "objective", "score"}, this, object);
    }

    public String owner() {
        return this.owner;
    }

    public String objective() {
        return this.objective;
    }

    public ScoreboardScore.Packed score() {
        return this.score;
    }
}
