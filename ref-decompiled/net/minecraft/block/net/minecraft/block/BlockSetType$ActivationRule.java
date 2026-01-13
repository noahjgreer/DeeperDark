/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

public static final class BlockSetType.ActivationRule
extends Enum<BlockSetType.ActivationRule> {
    public static final /* enum */ BlockSetType.ActivationRule EVERYTHING = new BlockSetType.ActivationRule();
    public static final /* enum */ BlockSetType.ActivationRule MOBS = new BlockSetType.ActivationRule();
    private static final /* synthetic */ BlockSetType.ActivationRule[] field_11363;

    public static BlockSetType.ActivationRule[] values() {
        return (BlockSetType.ActivationRule[])field_11363.clone();
    }

    public static BlockSetType.ActivationRule valueOf(String string) {
        return Enum.valueOf(BlockSetType.ActivationRule.class, string);
    }

    private static /* synthetic */ BlockSetType.ActivationRule[] method_36707() {
        return new BlockSetType.ActivationRule[]{EVERYTHING, MOBS};
    }

    static {
        field_11363 = BlockSetType.ActivationRule.method_36707();
    }
}
