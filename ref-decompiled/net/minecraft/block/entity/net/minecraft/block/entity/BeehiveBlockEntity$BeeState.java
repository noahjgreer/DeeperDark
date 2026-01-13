/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

public static final class BeehiveBlockEntity.BeeState
extends Enum<BeehiveBlockEntity.BeeState> {
    public static final /* enum */ BeehiveBlockEntity.BeeState HONEY_DELIVERED = new BeehiveBlockEntity.BeeState();
    public static final /* enum */ BeehiveBlockEntity.BeeState BEE_RELEASED = new BeehiveBlockEntity.BeeState();
    public static final /* enum */ BeehiveBlockEntity.BeeState EMERGENCY = new BeehiveBlockEntity.BeeState();
    private static final /* synthetic */ BeehiveBlockEntity.BeeState[] field_20430;

    public static BeehiveBlockEntity.BeeState[] values() {
        return (BeehiveBlockEntity.BeeState[])field_20430.clone();
    }

    public static BeehiveBlockEntity.BeeState valueOf(String string) {
        return Enum.valueOf(BeehiveBlockEntity.BeeState.class, string);
    }

    private static /* synthetic */ BeehiveBlockEntity.BeeState[] method_36714() {
        return new BeehiveBlockEntity.BeeState[]{HONEY_DELIVERED, BEE_RELEASED, EMERGENCY};
    }

    static {
        field_20430 = BeehiveBlockEntity.BeeState.method_36714();
    }
}
