/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.LookControl;

class CamelEntity.CamelLookControl
extends LookControl {
    CamelEntity.CamelLookControl() {
        super(CamelEntity.this);
    }

    @Override
    public void tick() {
        if (!CamelEntity.this.hasControllingPassenger()) {
            super.tick();
        }
    }
}
