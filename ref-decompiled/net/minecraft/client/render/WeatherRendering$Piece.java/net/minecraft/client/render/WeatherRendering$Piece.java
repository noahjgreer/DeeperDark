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
public static final class WeatherRendering.Piece
extends Record {
    final int x;
    final int z;
    final int bottomY;
    final int topY;
    final float uOffset;
    final float vOffset;
    final int lightCoords;

    public WeatherRendering.Piece(int x, int z, int bottomY, int topY, float uOffset, float vOffset, int lightCoords) {
        this.x = x;
        this.z = z;
        this.bottomY = bottomY;
        this.topY = topY;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.lightCoords = lightCoords;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{WeatherRendering.Piece.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WeatherRendering.Piece.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WeatherRendering.Piece.class, "x;z;bottomY;topY;uOffset;vOffset;lightCoords", "x", "z", "bottomY", "topY", "uOffset", "vOffset", "lightCoords"}, this, object);
    }

    public int x() {
        return this.x;
    }

    public int z() {
        return this.z;
    }

    public int bottomY() {
        return this.bottomY;
    }

    public int topY() {
        return this.topY;
    }

    public float uOffset() {
        return this.uOffset;
    }

    public float vOffset() {
        return this.vOffset;
    }

    public int lightCoords() {
        return this.lightCoords;
    }
}
