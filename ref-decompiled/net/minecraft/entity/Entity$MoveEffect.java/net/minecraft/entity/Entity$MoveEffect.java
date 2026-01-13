/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public static final class Entity.MoveEffect
extends Enum<Entity.MoveEffect> {
    public static final /* enum */ Entity.MoveEffect NONE = new Entity.MoveEffect(false, false);
    public static final /* enum */ Entity.MoveEffect SOUNDS = new Entity.MoveEffect(true, false);
    public static final /* enum */ Entity.MoveEffect EVENTS = new Entity.MoveEffect(false, true);
    public static final /* enum */ Entity.MoveEffect ALL = new Entity.MoveEffect(true, true);
    final boolean sounds;
    final boolean events;
    private static final /* synthetic */ Entity.MoveEffect[] field_28636;

    public static Entity.MoveEffect[] values() {
        return (Entity.MoveEffect[])field_28636.clone();
    }

    public static Entity.MoveEffect valueOf(String string) {
        return Enum.valueOf(Entity.MoveEffect.class, string);
    }

    private Entity.MoveEffect(boolean sounds, boolean events) {
        this.sounds = sounds;
        this.events = events;
    }

    public boolean hasAny() {
        return this.events || this.sounds;
    }

    public boolean emitsGameEvents() {
        return this.events;
    }

    public boolean playsSounds() {
        return this.sounds;
    }

    private static /* synthetic */ Entity.MoveEffect[] method_36602() {
        return new Entity.MoveEffect[]{NONE, SOUNDS, EVENTS, ALL};
    }

    static {
        field_28636 = Entity.MoveEffect.method_36602();
    }
}
