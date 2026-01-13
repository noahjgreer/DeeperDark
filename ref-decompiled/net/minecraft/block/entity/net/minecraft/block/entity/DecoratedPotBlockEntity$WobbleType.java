/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

public static final class DecoratedPotBlockEntity.WobbleType
extends Enum<DecoratedPotBlockEntity.WobbleType> {
    public static final /* enum */ DecoratedPotBlockEntity.WobbleType POSITIVE = new DecoratedPotBlockEntity.WobbleType(7);
    public static final /* enum */ DecoratedPotBlockEntity.WobbleType NEGATIVE = new DecoratedPotBlockEntity.WobbleType(10);
    public final int lengthInTicks;
    private static final /* synthetic */ DecoratedPotBlockEntity.WobbleType[] field_46667;

    public static DecoratedPotBlockEntity.WobbleType[] values() {
        return (DecoratedPotBlockEntity.WobbleType[])field_46667.clone();
    }

    public static DecoratedPotBlockEntity.WobbleType valueOf(String string) {
        return Enum.valueOf(DecoratedPotBlockEntity.WobbleType.class, string);
    }

    private DecoratedPotBlockEntity.WobbleType(int lengthInTicks) {
        this.lengthInTicks = lengthInTicks;
    }

    private static /* synthetic */ DecoratedPotBlockEntity.WobbleType[] method_54302() {
        return new DecoratedPotBlockEntity.WobbleType[]{POSITIVE, NEGATIVE};
    }

    static {
        field_46667 = DecoratedPotBlockEntity.WobbleType.method_54302();
    }
}
