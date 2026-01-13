/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.decoration;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class DisplayEntity.BillboardMode
extends Enum<DisplayEntity.BillboardMode>
implements StringIdentifiable {
    public static final /* enum */ DisplayEntity.BillboardMode FIXED = new DisplayEntity.BillboardMode(0, "fixed");
    public static final /* enum */ DisplayEntity.BillboardMode VERTICAL = new DisplayEntity.BillboardMode(1, "vertical");
    public static final /* enum */ DisplayEntity.BillboardMode HORIZONTAL = new DisplayEntity.BillboardMode(2, "horizontal");
    public static final /* enum */ DisplayEntity.BillboardMode CENTER = new DisplayEntity.BillboardMode(3, "center");
    public static final Codec<DisplayEntity.BillboardMode> CODEC;
    public static final IntFunction<DisplayEntity.BillboardMode> FROM_INDEX;
    private final byte index;
    private final String name;
    private static final /* synthetic */ DisplayEntity.BillboardMode[] field_42414;

    public static DisplayEntity.BillboardMode[] values() {
        return (DisplayEntity.BillboardMode[])field_42414.clone();
    }

    public static DisplayEntity.BillboardMode valueOf(String string) {
        return Enum.valueOf(DisplayEntity.BillboardMode.class, string);
    }

    private DisplayEntity.BillboardMode(byte index, String name) {
        this.name = name;
        this.index = index;
    }

    @Override
    public String asString() {
        return this.name;
    }

    byte getIndex() {
        return this.index;
    }

    private static /* synthetic */ DisplayEntity.BillboardMode[] method_48882() {
        return new DisplayEntity.BillboardMode[]{FIXED, VERTICAL, HORIZONTAL, CENTER};
    }

    static {
        field_42414 = DisplayEntity.BillboardMode.method_48882();
        CODEC = StringIdentifiable.createCodec(DisplayEntity.BillboardMode::values);
        FROM_INDEX = ValueLists.createIndexToValueFunction(DisplayEntity.BillboardMode::getIndex, DisplayEntity.BillboardMode.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}
