/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

public static final class DataOutput.OutputType
extends Enum<DataOutput.OutputType> {
    public static final /* enum */ DataOutput.OutputType DATA_PACK = new DataOutput.OutputType("data");
    public static final /* enum */ DataOutput.OutputType RESOURCE_PACK = new DataOutput.OutputType("assets");
    public static final /* enum */ DataOutput.OutputType REPORTS = new DataOutput.OutputType("reports");
    final String path;
    private static final /* synthetic */ DataOutput.OutputType[] field_39371;

    public static DataOutput.OutputType[] values() {
        return (DataOutput.OutputType[])field_39371.clone();
    }

    public static DataOutput.OutputType valueOf(String string) {
        return Enum.valueOf(DataOutput.OutputType.class, string);
    }

    private DataOutput.OutputType(String path) {
        this.path = path;
    }

    private static /* synthetic */ DataOutput.OutputType[] method_44109() {
        return new DataOutput.OutputType[]{DATA_PACK, RESOURCE_PACK, REPORTS};
    }

    static {
        field_39371 = DataOutput.OutputType.method_44109();
    }
}
