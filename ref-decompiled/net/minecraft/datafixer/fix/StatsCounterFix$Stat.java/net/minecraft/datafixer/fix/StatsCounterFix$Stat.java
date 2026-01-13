/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.datafixer.fix;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

static final class StatsCounterFix.Stat
extends Record {
    final String type;
    final String typeKey;

    StatsCounterFix.Stat(String type, String typeKey) {
        this.type = type;
        this.typeKey = typeKey;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{StatsCounterFix.Stat.class, "type;typeKey", "type", "typeKey"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StatsCounterFix.Stat.class, "type;typeKey", "type", "typeKey"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StatsCounterFix.Stat.class, "type;typeKey", "type", "typeKey"}, this, object);
    }

    public String type() {
        return this.type;
    }

    public String typeKey() {
        return this.typeKey;
    }
}
