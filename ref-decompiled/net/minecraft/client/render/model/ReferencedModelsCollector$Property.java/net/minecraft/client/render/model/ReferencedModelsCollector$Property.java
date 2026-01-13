/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class ReferencedModelsCollector.Property<T>
extends Record {
    final int index;

    ReferencedModelsCollector.Property(int index) {
        this.index = index;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ReferencedModelsCollector.Property.class, "index", "index"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ReferencedModelsCollector.Property.class, "index", "index"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ReferencedModelsCollector.Property.class, "index", "index"}, this, object);
    }

    public int index() {
        return this.index;
    }
}
