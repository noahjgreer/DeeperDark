/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.passive.AxolotlEntity;

static class AxolotlEntity.AxolotlMoveControl
extends AquaticMoveControl {
    private final AxolotlEntity axolotl;

    public AxolotlEntity.AxolotlMoveControl(AxolotlEntity axolotl) {
        super(axolotl, 85, 10, 0.1f, 0.5f, false);
        this.axolotl = axolotl;
    }

    @Override
    public void tick() {
        if (!this.axolotl.isPlayingDead()) {
            super.tick();
        }
    }
}
