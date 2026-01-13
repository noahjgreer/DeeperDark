/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public static final class Entity.RemovalReason
extends Enum<Entity.RemovalReason> {
    public static final /* enum */ Entity.RemovalReason KILLED = new Entity.RemovalReason(true, false);
    public static final /* enum */ Entity.RemovalReason DISCARDED = new Entity.RemovalReason(true, false);
    public static final /* enum */ Entity.RemovalReason UNLOADED_TO_CHUNK = new Entity.RemovalReason(false, true);
    public static final /* enum */ Entity.RemovalReason UNLOADED_WITH_PLAYER = new Entity.RemovalReason(false, false);
    public static final /* enum */ Entity.RemovalReason CHANGED_DIMENSION = new Entity.RemovalReason(false, false);
    private final boolean destroy;
    private final boolean save;
    private static final /* synthetic */ Entity.RemovalReason[] field_27005;

    public static Entity.RemovalReason[] values() {
        return (Entity.RemovalReason[])field_27005.clone();
    }

    public static Entity.RemovalReason valueOf(String string) {
        return Enum.valueOf(Entity.RemovalReason.class, string);
    }

    private Entity.RemovalReason(boolean destroy, boolean save) {
        this.destroy = destroy;
        this.save = save;
    }

    public boolean shouldDestroy() {
        return this.destroy;
    }

    public boolean shouldSave() {
        return this.save;
    }

    private static /* synthetic */ Entity.RemovalReason[] method_36603() {
        return new Entity.RemovalReason[]{KILLED, DISCARDED, UNLOADED_TO_CHUNK, UNLOADED_WITH_PLAYER, CHANGED_DIMENSION};
    }

    static {
        field_27005 = Entity.RemovalReason.method_36603();
    }
}
