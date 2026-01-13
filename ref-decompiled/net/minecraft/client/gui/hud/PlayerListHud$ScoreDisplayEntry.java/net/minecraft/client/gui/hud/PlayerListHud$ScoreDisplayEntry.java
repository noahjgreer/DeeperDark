/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class PlayerListHud.ScoreDisplayEntry
extends Record {
    final Text name;
    final int score;
    final @Nullable Text formattedScore;
    final int scoreWidth;

    PlayerListHud.ScoreDisplayEntry(Text name, int score, @Nullable Text formattedScore, int scoreWidth) {
        this.name = name;
        this.score = score;
        this.formattedScore = formattedScore;
        this.scoreWidth = scoreWidth;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerListHud.ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerListHud.ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerListHud.ScoreDisplayEntry.class, "name;score;formattedScore;scoreWidth", "name", "score", "formattedScore", "scoreWidth"}, this, object);
    }

    public Text name() {
        return this.name;
    }

    public int score() {
        return this.score;
    }

    public @Nullable Text formattedScore() {
        return this.formattedScore;
    }

    public int scoreWidth() {
        return this.scoreWidth;
    }
}
