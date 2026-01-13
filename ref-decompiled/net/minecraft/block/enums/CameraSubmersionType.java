/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.CameraSubmersionType
 */
package net.minecraft.block.enums;

/*
 * Exception performing whole class analysis ignored.
 */
public final class CameraSubmersionType
extends Enum<CameraSubmersionType> {
    public static final /* enum */ CameraSubmersionType LAVA = new CameraSubmersionType("LAVA", 0);
    public static final /* enum */ CameraSubmersionType WATER = new CameraSubmersionType("WATER", 1);
    public static final /* enum */ CameraSubmersionType POWDER_SNOW = new CameraSubmersionType("POWDER_SNOW", 2);
    public static final /* enum */ CameraSubmersionType ATMOSPHERIC = new CameraSubmersionType("ATMOSPHERIC", 3);
    public static final /* enum */ CameraSubmersionType NONE = new CameraSubmersionType("NONE", 4);
    private static final /* synthetic */ CameraSubmersionType[] field_27889;

    public static CameraSubmersionType[] values() {
        return (CameraSubmersionType[])field_27889.clone();
    }

    public static CameraSubmersionType valueOf(String string) {
        return Enum.valueOf(CameraSubmersionType.class, string);
    }

    private static /* synthetic */ CameraSubmersionType[] method_36764() {
        return new CameraSubmersionType[]{LAVA, WATER, POWDER_SNOW, ATMOSPHERIC, NONE};
    }

    static {
        field_27889 = CameraSubmersionType.method_36764();
    }
}

