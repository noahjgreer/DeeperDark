/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class CloudRenderer.CloudCells
extends Record {
    final long[] cells;
    final int width;
    final int height;

    public CloudRenderer.CloudCells(long[] cells, int width, int height) {
        this.cells = cells;
        this.width = width;
        this.height = height;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CloudRenderer.CloudCells.class, "cells;width;height", "cells", "width", "height"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CloudRenderer.CloudCells.class, "cells;width;height", "cells", "width", "height"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CloudRenderer.CloudCells.class, "cells;width;height", "cells", "width", "height"}, this, object);
    }

    public long[] cells() {
        return this.cells;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }
}
