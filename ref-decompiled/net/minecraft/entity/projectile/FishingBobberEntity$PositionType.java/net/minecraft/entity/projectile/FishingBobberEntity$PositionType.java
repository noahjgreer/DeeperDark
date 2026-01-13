/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

static final class FishingBobberEntity.PositionType
extends Enum<FishingBobberEntity.PositionType> {
    public static final /* enum */ FishingBobberEntity.PositionType ABOVE_WATER = new FishingBobberEntity.PositionType();
    public static final /* enum */ FishingBobberEntity.PositionType INSIDE_WATER = new FishingBobberEntity.PositionType();
    public static final /* enum */ FishingBobberEntity.PositionType INVALID = new FishingBobberEntity.PositionType();
    private static final /* synthetic */ FishingBobberEntity.PositionType[] field_23239;

    public static FishingBobberEntity.PositionType[] values() {
        return (FishingBobberEntity.PositionType[])field_23239.clone();
    }

    public static FishingBobberEntity.PositionType valueOf(String string) {
        return Enum.valueOf(FishingBobberEntity.PositionType.class, string);
    }

    private static /* synthetic */ FishingBobberEntity.PositionType[] method_36665() {
        return new FishingBobberEntity.PositionType[]{ABOVE_WATER, INSIDE_WATER, INVALID};
    }

    static {
        field_23239 = FishingBobberEntity.PositionType.method_36665();
    }
}
