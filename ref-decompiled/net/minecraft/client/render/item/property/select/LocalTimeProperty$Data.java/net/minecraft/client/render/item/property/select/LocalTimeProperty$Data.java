/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.util.TimeZone
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.select;

import com.ibm.icu.util.TimeZone;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class LocalTimeProperty.Data
extends Record {
    final String format;
    final String localeId;
    final Optional<TimeZone> timeZone;

    LocalTimeProperty.Data(String format, String localeId, Optional<TimeZone> timeZone) {
        this.format = format;
        this.localeId = localeId;
        this.timeZone = timeZone;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{LocalTimeProperty.Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{LocalTimeProperty.Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{LocalTimeProperty.Data.class, "format;localeId;timeZone", "format", "localeId", "timeZone"}, this, object);
    }

    public String format() {
        return this.format;
    }

    public String localeId() {
        return this.localeId;
    }

    public Optional<TimeZone> timeZone() {
        return this.timeZone;
    }
}
