/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

public static final class AxolotlEntity.State
extends Enum<AxolotlEntity.State> {
    public static final /* enum */ AxolotlEntity.State PLAYING_DEAD = new AxolotlEntity.State();
    public static final /* enum */ AxolotlEntity.State IN_WATER = new AxolotlEntity.State();
    public static final /* enum */ AxolotlEntity.State ON_GROUND = new AxolotlEntity.State();
    public static final /* enum */ AxolotlEntity.State IN_AIR = new AxolotlEntity.State();
    private static final /* synthetic */ AxolotlEntity.State[] field_52487;

    public static AxolotlEntity.State[] values() {
        return (AxolotlEntity.State[])field_52487.clone();
    }

    public static AxolotlEntity.State valueOf(String string) {
        return Enum.valueOf(AxolotlEntity.State.class, string);
    }

    private static /* synthetic */ AxolotlEntity.State[] method_61480() {
        return new AxolotlEntity.State[]{PLAYING_DEAD, IN_WATER, ON_GROUND, IN_AIR};
    }

    static {
        field_52487 = AxolotlEntity.State.method_61480();
    }
}
