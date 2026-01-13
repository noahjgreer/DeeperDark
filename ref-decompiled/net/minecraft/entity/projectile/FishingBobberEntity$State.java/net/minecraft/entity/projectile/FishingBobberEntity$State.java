/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.projectile;

static final class FishingBobberEntity.State
extends Enum<FishingBobberEntity.State> {
    public static final /* enum */ FishingBobberEntity.State FLYING = new FishingBobberEntity.State();
    public static final /* enum */ FishingBobberEntity.State HOOKED_IN_ENTITY = new FishingBobberEntity.State();
    public static final /* enum */ FishingBobberEntity.State BOBBING = new FishingBobberEntity.State();
    private static final /* synthetic */ FishingBobberEntity.State[] field_7181;

    public static FishingBobberEntity.State[] values() {
        return (FishingBobberEntity.State[])field_7181.clone();
    }

    public static FishingBobberEntity.State valueOf(String string) {
        return Enum.valueOf(FishingBobberEntity.State.class, string);
    }

    private static /* synthetic */ FishingBobberEntity.State[] method_36664() {
        return new FishingBobberEntity.State[]{FLYING, HOOKED_IN_ENTITY, BOBBING};
    }

    static {
        field_7181 = FishingBobberEntity.State.method_36664();
    }
}
