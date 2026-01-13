/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.scoreboard;

import java.util.Objects;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.text.Text;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jspecify.annotations.Nullable;

class Scoreboard.1
implements ScoreAccess {
    final /* synthetic */ ScoreboardScore field_47543;
    final /* synthetic */ boolean field_47544;
    final /* synthetic */ MutableBoolean field_47545;
    final /* synthetic */ ScoreboardObjective field_47546;
    final /* synthetic */ ScoreHolder field_47547;

    Scoreboard.1() {
        this.field_47543 = scoreboardScore;
        this.field_47544 = bl;
        this.field_47545 = mutableBoolean;
        this.field_47546 = scoreboardObjective;
        this.field_47547 = scoreHolder;
    }

    @Override
    public int getScore() {
        return this.field_47543.getScore();
    }

    @Override
    public void setScore(int score) {
        Text text;
        if (!this.field_47544) {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        boolean bl = this.field_47545.isTrue();
        if (this.field_47546.shouldDisplayAutoUpdate() && (text = this.field_47547.getDisplayName()) != null && !text.equals(this.field_47543.getDisplayText())) {
            this.field_47543.setDisplayText(text);
            bl = true;
        }
        if (score != this.field_47543.getScore()) {
            this.field_47543.setScore(score);
            bl = true;
        }
        if (bl) {
            this.update();
        }
    }

    @Override
    public @Nullable Text getDisplayText() {
        return this.field_47543.getDisplayText();
    }

    @Override
    public void setDisplayText(@Nullable Text text) {
        if (this.field_47545.isTrue() || !Objects.equals(text, this.field_47543.getDisplayText())) {
            this.field_47543.setDisplayText(text);
            this.update();
        }
    }

    @Override
    public void setNumberFormat(@Nullable NumberFormat numberFormat) {
        this.field_47543.setNumberFormat(numberFormat);
        this.update();
    }

    @Override
    public boolean isLocked() {
        return this.field_47543.isLocked();
    }

    @Override
    public void unlock() {
        this.setLocked(false);
    }

    @Override
    public void lock() {
        this.setLocked(true);
    }

    private void setLocked(boolean locked) {
        this.field_47543.setLocked(locked);
        if (this.field_47545.isTrue()) {
            this.update();
        }
        Scoreboard.this.resetScore(this.field_47547, this.field_47546);
    }

    private void update() {
        Scoreboard.this.updateScore(this.field_47547, this.field_47546, this.field_47543);
        this.field_47545.setFalse();
    }
}
