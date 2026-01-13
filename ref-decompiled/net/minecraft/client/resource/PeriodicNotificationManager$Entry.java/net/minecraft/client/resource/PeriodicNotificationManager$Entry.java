/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class PeriodicNotificationManager.Entry
extends Record {
    final long delay;
    final long period;
    final String title;
    final String message;

    public PeriodicNotificationManager.Entry(long delay, long period, String title, String message) {
        this.delay = delay != 0L ? delay : period;
        this.period = period;
        this.title = title;
        this.message = message;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PeriodicNotificationManager.Entry.class, "delay;period;title;message", "delay", "period", "title", "message"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PeriodicNotificationManager.Entry.class, "delay;period;title;message", "delay", "period", "title", "message"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PeriodicNotificationManager.Entry.class, "delay;period;title;message", "delay", "period", "title", "message"}, this, object);
    }

    public long delay() {
        return this.delay;
    }

    public long period() {
        return this.period;
    }

    public String title() {
        return this.title;
    }

    public String message() {
        return this.message;
    }
}
