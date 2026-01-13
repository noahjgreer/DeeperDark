/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.passive.PandaEntity;

static class PandaEntity.PandaMoveControl
extends MoveControl {
    private final PandaEntity panda;

    public PandaEntity.PandaMoveControl(PandaEntity panda) {
        super(panda);
        this.panda = panda;
    }

    @Override
    public void tick() {
        if (!this.panda.isIdle()) {
            return;
        }
        super.tick();
    }
}
