/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

public static final class StructureBlockBlockEntity.Action
extends Enum<StructureBlockBlockEntity.Action> {
    public static final /* enum */ StructureBlockBlockEntity.Action UPDATE_DATA = new StructureBlockBlockEntity.Action();
    public static final /* enum */ StructureBlockBlockEntity.Action SAVE_AREA = new StructureBlockBlockEntity.Action();
    public static final /* enum */ StructureBlockBlockEntity.Action LOAD_AREA = new StructureBlockBlockEntity.Action();
    public static final /* enum */ StructureBlockBlockEntity.Action SCAN_AREA = new StructureBlockBlockEntity.Action();
    private static final /* synthetic */ StructureBlockBlockEntity.Action[] field_12107;

    public static StructureBlockBlockEntity.Action[] values() {
        return (StructureBlockBlockEntity.Action[])field_12107.clone();
    }

    public static StructureBlockBlockEntity.Action valueOf(String string) {
        return Enum.valueOf(StructureBlockBlockEntity.Action.class, string);
    }

    private static /* synthetic */ StructureBlockBlockEntity.Action[] method_36718() {
        return new StructureBlockBlockEntity.Action[]{UPDATE_DATA, SAVE_AREA, LOAD_AREA, SCAN_AREA};
    }

    static {
        field_12107 = StructureBlockBlockEntity.Action.method_36718();
    }
}
