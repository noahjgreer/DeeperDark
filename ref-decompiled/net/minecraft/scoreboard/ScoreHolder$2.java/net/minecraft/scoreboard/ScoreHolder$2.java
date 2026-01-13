/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.scoreboard;

import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.text.Text;

static class ScoreHolder.2
implements ScoreHolder {
    final /* synthetic */ String field_47539;
    final /* synthetic */ Text field_47540;

    ScoreHolder.2() {
        this.field_47539 = string;
        this.field_47540 = text;
    }

    @Override
    public String getNameForScoreboard() {
        return this.field_47539;
    }

    @Override
    public Text getStyledDisplayName() {
        return this.field_47540;
    }
}
