/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.serialization.Codec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public static final class TrueTypeFontLoader.Shift
extends Record {
    final float x;
    final float y;
    public static final TrueTypeFontLoader.Shift NONE = new TrueTypeFontLoader.Shift(0.0f, 0.0f);
    public static final Codec<TrueTypeFontLoader.Shift> CODEC = Codec.floatRange((float)-512.0f, (float)512.0f).listOf().comapFlatMap(floatList2 -> Util.decodeFixedLengthList(floatList2, 2).map(floatList -> new TrueTypeFontLoader.Shift(((Float)floatList.get(0)).floatValue(), ((Float)floatList.get(1)).floatValue())), shift -> List.of(Float.valueOf(shift.x), Float.valueOf(shift.y)));

    public TrueTypeFontLoader.Shift(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TrueTypeFontLoader.Shift.class, "x;y", "x", "y"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TrueTypeFontLoader.Shift.class, "x;y", "x", "y"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TrueTypeFontLoader.Shift.class, "x;y", "x", "y"}, this, object);
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }
}
