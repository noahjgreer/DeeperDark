/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class PlayerListHud.Heart {
    private static final long COOLDOWN_TICKS = 20L;
    private static final long SCORE_DECREASE_HIGHLIGHT_TICKS = 20L;
    private static final long SCORE_INCREASE_HIGHLIGHT_TICKS = 10L;
    private int score;
    private int lastScore;
    private long lastScoreChangeTick;
    private long highlightEndTick;

    public PlayerListHud.Heart(int score) {
        this.lastScore = score;
        this.score = score;
    }

    public void tick(int score, long currentTick) {
        if (score != this.score) {
            long l = score < this.score ? 20L : 10L;
            this.highlightEndTick = currentTick + l;
            this.score = score;
            this.lastScoreChangeTick = currentTick;
        }
        if (currentTick - this.lastScoreChangeTick > 20L) {
            this.lastScore = score;
        }
    }

    public int getLastScore() {
        return this.lastScore;
    }

    public boolean useHighlighted(long currentTick) {
        return this.highlightEndTick > currentTick && (this.highlightEndTick - currentTick) % 6L >= 3L;
    }
}
