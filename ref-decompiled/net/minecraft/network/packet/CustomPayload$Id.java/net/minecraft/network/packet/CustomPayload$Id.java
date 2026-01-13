/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public static final class CustomPayload.Id<T extends CustomPayload>
extends Record {
    final Identifier id;

    public CustomPayload.Id(Identifier id) {
        this.id = id;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CustomPayload.Id.class, "id", "id"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CustomPayload.Id.class, "id", "id"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CustomPayload.Id.class, "id", "id"}, this, object);
    }

    public Identifier id() {
        return this.id;
    }
}
