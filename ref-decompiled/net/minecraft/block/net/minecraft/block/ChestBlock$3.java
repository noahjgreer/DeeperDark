/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;

static class ChestBlock.3
implements DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction> {
    final /* synthetic */ LidOpenable field_21782;

    ChestBlock.3(LidOpenable lidOpenable) {
        this.field_21782 = lidOpenable;
    }

    @Override
    public Float2FloatFunction getFromBoth(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
        return tickProgress -> Math.max(chestBlockEntity.getAnimationProgress(tickProgress), chestBlockEntity2.getAnimationProgress(tickProgress));
    }

    @Override
    public Float2FloatFunction getFrom(ChestBlockEntity chestBlockEntity) {
        return chestBlockEntity::getAnimationProgress;
    }

    @Override
    public Float2FloatFunction getFallback() {
        return this.field_21782::getAnimationProgress;
    }

    @Override
    public /* synthetic */ Object getFallback() {
        return this.getFallback();
    }
}
