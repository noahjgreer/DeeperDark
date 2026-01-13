/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.PassiveEntity;

static class LlamaEntity.LlamaData
extends PassiveEntity.PassiveData {
    public final LlamaEntity.Variant variant;

    LlamaEntity.LlamaData(LlamaEntity.Variant variant) {
        super(true);
        this.variant = variant;
    }
}
