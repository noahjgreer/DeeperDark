/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.AbstractListBuilder;

static final class NullOps.NullListBuilder
extends AbstractListBuilder<Unit, Unit> {
    public NullOps.NullListBuilder(DynamicOps<Unit> dynamicOps) {
        super(dynamicOps);
    }

    @Override
    protected Unit initBuilder() {
        return Unit.INSTANCE;
    }

    @Override
    protected Unit add(Unit unit, Unit unit2) {
        return unit;
    }

    @Override
    protected DataResult<Unit> build(Unit unit, Unit unit2) {
        return DataResult.success((Object)((Object)unit));
    }

    @Override
    protected /* synthetic */ Object initBuilder() {
        return this.initBuilder();
    }
}
