/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

public static final class Fertilizable.FertilizableType
extends Enum<Fertilizable.FertilizableType> {
    public static final /* enum */ Fertilizable.FertilizableType NEIGHBOR_SPREADER = new Fertilizable.FertilizableType();
    public static final /* enum */ Fertilizable.FertilizableType GROWER = new Fertilizable.FertilizableType();
    private static final /* synthetic */ Fertilizable.FertilizableType[] field_47836;

    public static Fertilizable.FertilizableType[] values() {
        return (Fertilizable.FertilizableType[])field_47836.clone();
    }

    public static Fertilizable.FertilizableType valueOf(String string) {
        return Enum.valueOf(Fertilizable.FertilizableType.class, string);
    }

    private static /* synthetic */ Fertilizable.FertilizableType[] method_55771() {
        return new Fertilizable.FertilizableType[]{NEIGHBOR_SPREADER, GROWER};
    }

    static {
        field_47836 = Fertilizable.FertilizableType.method_55771();
    }
}
