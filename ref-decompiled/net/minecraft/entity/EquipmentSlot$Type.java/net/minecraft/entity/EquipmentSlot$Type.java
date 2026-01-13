/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public static final class EquipmentSlot.Type
extends Enum<EquipmentSlot.Type> {
    public static final /* enum */ EquipmentSlot.Type HAND = new EquipmentSlot.Type();
    public static final /* enum */ EquipmentSlot.Type HUMANOID_ARMOR = new EquipmentSlot.Type();
    public static final /* enum */ EquipmentSlot.Type ANIMAL_ARMOR = new EquipmentSlot.Type();
    public static final /* enum */ EquipmentSlot.Type SADDLE = new EquipmentSlot.Type();
    private static final /* synthetic */ EquipmentSlot.Type[] field_6179;

    public static EquipmentSlot.Type[] values() {
        return (EquipmentSlot.Type[])field_6179.clone();
    }

    public static EquipmentSlot.Type valueOf(String string) {
        return Enum.valueOf(EquipmentSlot.Type.class, string);
    }

    private static /* synthetic */ EquipmentSlot.Type[] method_36605() {
        return new EquipmentSlot.Type[]{HAND, HUMANOID_ARMOR, ANIMAL_ARMOR, SADDLE};
    }

    static {
        field_6179 = EquipmentSlot.Type.method_36605();
    }
}
