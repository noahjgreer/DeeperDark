/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

public static final class ShulkerBoxBlockEntity.AnimationStage
extends Enum<ShulkerBoxBlockEntity.AnimationStage> {
    public static final /* enum */ ShulkerBoxBlockEntity.AnimationStage CLOSED = new ShulkerBoxBlockEntity.AnimationStage();
    public static final /* enum */ ShulkerBoxBlockEntity.AnimationStage OPENING = new ShulkerBoxBlockEntity.AnimationStage();
    public static final /* enum */ ShulkerBoxBlockEntity.AnimationStage OPENED = new ShulkerBoxBlockEntity.AnimationStage();
    public static final /* enum */ ShulkerBoxBlockEntity.AnimationStage CLOSING = new ShulkerBoxBlockEntity.AnimationStage();
    private static final /* synthetic */ ShulkerBoxBlockEntity.AnimationStage[] field_12067;

    public static ShulkerBoxBlockEntity.AnimationStage[] values() {
        return (ShulkerBoxBlockEntity.AnimationStage[])field_12067.clone();
    }

    public static ShulkerBoxBlockEntity.AnimationStage valueOf(String string) {
        return Enum.valueOf(ShulkerBoxBlockEntity.AnimationStage.class, string);
    }

    private static /* synthetic */ ShulkerBoxBlockEntity.AnimationStage[] method_36717() {
        return new ShulkerBoxBlockEntity.AnimationStage[]{CLOSED, OPENING, OPENED, CLOSING};
    }

    static {
        field_12067 = ShulkerBoxBlockEntity.AnimationStage.method_36717();
    }
}
