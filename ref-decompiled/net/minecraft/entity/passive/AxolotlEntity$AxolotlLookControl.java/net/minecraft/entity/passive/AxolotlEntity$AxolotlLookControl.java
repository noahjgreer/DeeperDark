/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.passive.AxolotlEntity;

class AxolotlEntity.AxolotlLookControl
extends YawAdjustingLookControl {
    public AxolotlEntity.AxolotlLookControl(AxolotlEntity axolotl, int yawAdjustThreshold) {
        super(axolotl, yawAdjustThreshold);
    }

    @Override
    public void tick() {
        if (!AxolotlEntity.this.isPlayingDead()) {
            super.tick();
        }
    }
}
