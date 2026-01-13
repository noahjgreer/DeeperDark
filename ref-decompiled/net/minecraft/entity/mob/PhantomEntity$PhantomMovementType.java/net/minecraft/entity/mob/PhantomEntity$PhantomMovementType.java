/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

static final class PhantomEntity.PhantomMovementType
extends Enum<PhantomEntity.PhantomMovementType> {
    public static final /* enum */ PhantomEntity.PhantomMovementType CIRCLE = new PhantomEntity.PhantomMovementType();
    public static final /* enum */ PhantomEntity.PhantomMovementType SWOOP = new PhantomEntity.PhantomMovementType();
    private static final /* synthetic */ PhantomEntity.PhantomMovementType[] field_7316;

    public static PhantomEntity.PhantomMovementType[] values() {
        return (PhantomEntity.PhantomMovementType[])field_7316.clone();
    }

    public static PhantomEntity.PhantomMovementType valueOf(String string) {
        return Enum.valueOf(PhantomEntity.PhantomMovementType.class, string);
    }

    private static /* synthetic */ PhantomEntity.PhantomMovementType[] method_36653() {
        return new PhantomEntity.PhantomMovementType[]{CIRCLE, SWOOP};
    }

    static {
        field_7316 = PhantomEntity.PhantomMovementType.method_36653();
    }
}
