/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

public static final class AbstractBoatEntity.Location
extends Enum<AbstractBoatEntity.Location> {
    public static final /* enum */ AbstractBoatEntity.Location IN_WATER = new AbstractBoatEntity.Location();
    public static final /* enum */ AbstractBoatEntity.Location UNDER_WATER = new AbstractBoatEntity.Location();
    public static final /* enum */ AbstractBoatEntity.Location UNDER_FLOWING_WATER = new AbstractBoatEntity.Location();
    public static final /* enum */ AbstractBoatEntity.Location ON_LAND = new AbstractBoatEntity.Location();
    public static final /* enum */ AbstractBoatEntity.Location IN_AIR = new AbstractBoatEntity.Location();
    private static final /* synthetic */ AbstractBoatEntity.Location[] field_7715;

    public static AbstractBoatEntity.Location[] values() {
        return (AbstractBoatEntity.Location[])field_7715.clone();
    }

    public static AbstractBoatEntity.Location valueOf(String string) {
        return Enum.valueOf(AbstractBoatEntity.Location.class, string);
    }

    private static /* synthetic */ AbstractBoatEntity.Location[] method_36670() {
        return new AbstractBoatEntity.Location[]{IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER, ON_LAND, IN_AIR};
    }

    static {
        field_7715 = AbstractBoatEntity.Location.method_36670();
    }
}
