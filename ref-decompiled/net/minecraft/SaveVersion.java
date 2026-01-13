/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SaveVersion
 *  net.minecraft.SharedConstants
 */
package net.minecraft;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.SharedConstants;

public record SaveVersion(int id, String series) {
    private final int id;
    private final String series;
    public static final String MAIN_SERIES = "main";

    public SaveVersion(int id, String series) {
        this.id = id;
        this.series = series;
    }

    public boolean isNotMainSeries() {
        return !this.series.equals(MAIN_SERIES);
    }

    public boolean isAvailableTo(SaveVersion other) {
        if (SharedConstants.OPEN_INCOMPATIBLE_WORLDS) {
            return true;
        }
        return this.series().equals(other.series());
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SaveVersion.class, "version;series", "id", "series"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SaveVersion.class, "version;series", "id", "series"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SaveVersion.class, "version;series", "id", "series"}, this, object);
    }

    public int id() {
        return this.id;
    }

    public String series() {
        return this.series;
    }
}

