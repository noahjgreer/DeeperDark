/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.entity.projectile;

import com.mojang.serialization.Codec;

public static final class PersistentProjectileEntity.PickupPermission
extends Enum<PersistentProjectileEntity.PickupPermission> {
    public static final /* enum */ PersistentProjectileEntity.PickupPermission DISALLOWED = new PersistentProjectileEntity.PickupPermission();
    public static final /* enum */ PersistentProjectileEntity.PickupPermission ALLOWED = new PersistentProjectileEntity.PickupPermission();
    public static final /* enum */ PersistentProjectileEntity.PickupPermission CREATIVE_ONLY = new PersistentProjectileEntity.PickupPermission();
    public static final Codec<PersistentProjectileEntity.PickupPermission> CODEC;
    private static final /* synthetic */ PersistentProjectileEntity.PickupPermission[] field_7591;

    public static PersistentProjectileEntity.PickupPermission[] values() {
        return (PersistentProjectileEntity.PickupPermission[])field_7591.clone();
    }

    public static PersistentProjectileEntity.PickupPermission valueOf(String string) {
        return Enum.valueOf(PersistentProjectileEntity.PickupPermission.class, string);
    }

    public static PersistentProjectileEntity.PickupPermission fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal > PersistentProjectileEntity.PickupPermission.values().length) {
            ordinal = 0;
        }
        return PersistentProjectileEntity.PickupPermission.values()[ordinal];
    }

    private static /* synthetic */ PersistentProjectileEntity.PickupPermission[] method_36663() {
        return new PersistentProjectileEntity.PickupPermission[]{DISALLOWED, ALLOWED, CREATIVE_ONLY};
    }

    static {
        field_7591 = PersistentProjectileEntity.PickupPermission.method_36663();
        CODEC = Codec.BYTE.xmap(PersistentProjectileEntity.PickupPermission::fromOrdinal, pickupPermission -> (byte)pickupPermission.ordinal());
    }
}
