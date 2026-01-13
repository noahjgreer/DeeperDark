/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

public static final class IllagerEntity.State
extends Enum<IllagerEntity.State> {
    public static final /* enum */ IllagerEntity.State CROSSED = new IllagerEntity.State();
    public static final /* enum */ IllagerEntity.State ATTACKING = new IllagerEntity.State();
    public static final /* enum */ IllagerEntity.State SPELLCASTING = new IllagerEntity.State();
    public static final /* enum */ IllagerEntity.State BOW_AND_ARROW = new IllagerEntity.State();
    public static final /* enum */ IllagerEntity.State CROSSBOW_HOLD = new IllagerEntity.State();
    public static final /* enum */ IllagerEntity.State CROSSBOW_CHARGE = new IllagerEntity.State();
    public static final /* enum */ IllagerEntity.State CELEBRATING = new IllagerEntity.State();
    public static final /* enum */ IllagerEntity.State NEUTRAL = new IllagerEntity.State();
    private static final /* synthetic */ IllagerEntity.State[] field_7209;

    public static IllagerEntity.State[] values() {
        return (IllagerEntity.State[])field_7209.clone();
    }

    public static IllagerEntity.State valueOf(String string) {
        return Enum.valueOf(IllagerEntity.State.class, string);
    }

    private static /* synthetic */ IllagerEntity.State[] method_36647() {
        return new IllagerEntity.State[]{CROSSED, ATTACKING, SPELLCASTING, BOW_AND_ARROW, CROSSBOW_HOLD, CROSSBOW_CHARGE, CELEBRATING, NEUTRAL};
    }

    static {
        field_7209 = IllagerEntity.State.method_36647();
    }
}
