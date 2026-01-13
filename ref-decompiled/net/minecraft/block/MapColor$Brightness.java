/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package net.minecraft.block;

import com.google.common.base.Preconditions;

public static final class MapColor.Brightness
extends Enum<MapColor.Brightness> {
    public static final /* enum */ MapColor.Brightness LOW = new MapColor.Brightness(0, 180);
    public static final /* enum */ MapColor.Brightness NORMAL = new MapColor.Brightness(1, 220);
    public static final /* enum */ MapColor.Brightness HIGH = new MapColor.Brightness(2, 255);
    public static final /* enum */ MapColor.Brightness LOWEST = new MapColor.Brightness(3, 135);
    private static final MapColor.Brightness[] VALUES;
    public final int id;
    public final int brightness;
    private static final /* synthetic */ MapColor.Brightness[] field_34766;

    public static MapColor.Brightness[] values() {
        return (MapColor.Brightness[])field_34766.clone();
    }

    public static MapColor.Brightness valueOf(String string) {
        return Enum.valueOf(MapColor.Brightness.class, string);
    }

    private MapColor.Brightness(int id, int brightness) {
        this.id = id;
        this.brightness = brightness;
    }

    public static MapColor.Brightness validateAndGet(int id) {
        Preconditions.checkPositionIndex((int)id, (int)VALUES.length, (String)"brightness id");
        return MapColor.Brightness.get(id);
    }

    static MapColor.Brightness get(int id) {
        return VALUES[id];
    }

    private static /* synthetic */ MapColor.Brightness[] method_38483() {
        return new MapColor.Brightness[]{LOW, NORMAL, HIGH, LOWEST};
    }

    static {
        field_34766 = MapColor.Brightness.method_38483();
        VALUES = new MapColor.Brightness[]{LOW, NORMAL, HIGH, LOWEST};
    }
}
