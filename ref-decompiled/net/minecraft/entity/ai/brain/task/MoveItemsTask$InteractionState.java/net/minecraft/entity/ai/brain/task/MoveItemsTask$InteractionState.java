/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

public static final class MoveItemsTask.InteractionState
extends Enum<MoveItemsTask.InteractionState> {
    public static final /* enum */ MoveItemsTask.InteractionState PICKUP_ITEM = new MoveItemsTask.InteractionState();
    public static final /* enum */ MoveItemsTask.InteractionState PICKUP_NO_ITEM = new MoveItemsTask.InteractionState();
    public static final /* enum */ MoveItemsTask.InteractionState PLACE_ITEM = new MoveItemsTask.InteractionState();
    public static final /* enum */ MoveItemsTask.InteractionState PLACE_NO_ITEM = new MoveItemsTask.InteractionState();
    private static final /* synthetic */ MoveItemsTask.InteractionState[] field_61249;

    public static MoveItemsTask.InteractionState[] values() {
        return (MoveItemsTask.InteractionState[])field_61249.clone();
    }

    public static MoveItemsTask.InteractionState valueOf(String string) {
        return Enum.valueOf(MoveItemsTask.InteractionState.class, string);
    }

    private static /* synthetic */ MoveItemsTask.InteractionState[] method_72451() {
        return new MoveItemsTask.InteractionState[]{PICKUP_ITEM, PICKUP_NO_ITEM, PLACE_ITEM, PLACE_NO_ITEM};
    }

    static {
        field_61249 = MoveItemsTask.InteractionState.method_72451();
    }
}
