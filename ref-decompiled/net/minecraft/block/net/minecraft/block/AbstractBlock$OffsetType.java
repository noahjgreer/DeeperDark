/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

public static final class AbstractBlock.OffsetType
extends Enum<AbstractBlock.OffsetType> {
    public static final /* enum */ AbstractBlock.OffsetType NONE = new AbstractBlock.OffsetType();
    public static final /* enum */ AbstractBlock.OffsetType XZ = new AbstractBlock.OffsetType();
    public static final /* enum */ AbstractBlock.OffsetType XYZ = new AbstractBlock.OffsetType();
    private static final /* synthetic */ AbstractBlock.OffsetType[] field_10658;

    public static AbstractBlock.OffsetType[] values() {
        return (AbstractBlock.OffsetType[])field_10658.clone();
    }

    public static AbstractBlock.OffsetType valueOf(String string) {
        return Enum.valueOf(AbstractBlock.OffsetType.class, string);
    }

    private static /* synthetic */ AbstractBlock.OffsetType[] method_36719() {
        return new AbstractBlock.OffsetType[]{NONE, XZ, XYZ};
    }

    static {
        field_10658 = AbstractBlock.OffsetType.method_36719();
    }
}
