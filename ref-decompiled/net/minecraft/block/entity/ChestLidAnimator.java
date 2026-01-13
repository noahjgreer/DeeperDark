/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.ChestLidAnimator
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.block.entity;

import net.minecraft.util.math.MathHelper;

public class ChestLidAnimator {
    private boolean open;
    private float progress;
    private float lastProgress;

    public void step() {
        this.lastProgress = this.progress;
        float f = 0.1f;
        if (!this.open && this.progress > 0.0f) {
            this.progress = Math.max(this.progress - 0.1f, 0.0f);
        } else if (this.open && this.progress < 1.0f) {
            this.progress = Math.min(this.progress + 0.1f, 1.0f);
        }
    }

    public float getProgress(float tickProgress) {
        return MathHelper.lerp((float)tickProgress, (float)this.lastProgress, (float)this.progress);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}

