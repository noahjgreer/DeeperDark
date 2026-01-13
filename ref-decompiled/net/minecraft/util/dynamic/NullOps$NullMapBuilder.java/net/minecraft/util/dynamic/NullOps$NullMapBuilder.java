/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.RecordBuilder$AbstractUniversalBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.util.Unit;

static final class NullOps.NullMapBuilder
extends RecordBuilder.AbstractUniversalBuilder<Unit, Unit> {
    public NullOps.NullMapBuilder(DynamicOps<Unit> ops) {
        super(ops);
    }

    protected Unit initBuilder() {
        return Unit.INSTANCE;
    }

    protected Unit append(Unit unit, Unit unit2, Unit unit3) {
        return unit3;
    }

    protected DataResult<Unit> build(Unit unit, Unit unit2) {
        return DataResult.success((Object)((Object)unit2));
    }

    protected /* synthetic */ Object append(Object key, Object value, Object builder) {
        return this.append((Unit)((Object)key), (Unit)((Object)value), (Unit)((Object)builder));
    }

    protected /* synthetic */ DataResult build(Object builder, Object prefix) {
        return this.build((Unit)((Object)builder), (Unit)((Object)prefix));
    }

    protected /* synthetic */ Object initBuilder() {
        return this.initBuilder();
    }
}
