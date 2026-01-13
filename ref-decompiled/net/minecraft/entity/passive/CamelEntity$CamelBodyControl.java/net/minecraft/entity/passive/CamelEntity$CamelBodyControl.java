/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.passive.CamelEntity;

class CamelEntity.CamelBodyControl
extends BodyControl {
    public CamelEntity.CamelBodyControl(CamelEntity camel) {
        super(camel);
    }

    @Override
    public void tick() {
        if (!CamelEntity.this.isStationary()) {
            super.tick();
        }
    }
}
