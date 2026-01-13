/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

public static final class MoveItemsTask.NavigationState
extends Enum<MoveItemsTask.NavigationState> {
    public static final /* enum */ MoveItemsTask.NavigationState TRAVELLING = new MoveItemsTask.NavigationState();
    public static final /* enum */ MoveItemsTask.NavigationState QUEUING = new MoveItemsTask.NavigationState();
    public static final /* enum */ MoveItemsTask.NavigationState INTERACTING = new MoveItemsTask.NavigationState();
    private static final /* synthetic */ MoveItemsTask.NavigationState[] field_61253;

    public static MoveItemsTask.NavigationState[] values() {
        return (MoveItemsTask.NavigationState[])field_61253.clone();
    }

    public static MoveItemsTask.NavigationState valueOf(String string) {
        return Enum.valueOf(MoveItemsTask.NavigationState.class, string);
    }

    private static /* synthetic */ MoveItemsTask.NavigationState[] method_72452() {
        return new MoveItemsTask.NavigationState[]{TRAVELLING, QUEUING, INTERACTING};
    }

    static {
        field_61253 = MoveItemsTask.NavigationState.method_72452();
    }
}
