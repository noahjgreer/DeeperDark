/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.FontFilterType
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.client.font;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class FontFilterType
extends Enum<FontFilterType>
implements StringIdentifiable {
    public static final /* enum */ FontFilterType UNIFORM = new FontFilterType("UNIFORM", 0, "uniform");
    public static final /* enum */ FontFilterType JAPANESE_VARIANTS = new FontFilterType("JAPANESE_VARIANTS", 1, "jp");
    public static final Codec<FontFilterType> CODEC;
    private final String id;
    private static final /* synthetic */ FontFilterType[] field_49116;

    public static FontFilterType[] values() {
        return (FontFilterType[])field_49116.clone();
    }

    public static FontFilterType valueOf(String string) {
        return Enum.valueOf(FontFilterType.class, string);
    }

    private FontFilterType(String id) {
        this.id = id;
    }

    public String asString() {
        return this.id;
    }

    private static /* synthetic */ FontFilterType[] method_57030() {
        return new FontFilterType[]{UNIFORM, JAPANESE_VARIANTS};
    }

    static {
        field_49116 = FontFilterType.method_57030();
        CODEC = StringIdentifiable.createCodec(FontFilterType::values);
    }
}

